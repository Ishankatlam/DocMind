package Ishanknjr.example.DocMind.Service;

import Ishanknjr.example.DocMind.Config.AppPropertiesConfig;
import Ishanknjr.example.DocMind.DTO.*;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.jdbc.core.JdbcOperationsExtensionsKt.query;

@Service
@RequiredArgsConstructor
public class RagService {
    private static final Logger logger = LoggerFactory.getLogger(RagService.class);

    private final VectorStore vectorStore;

    private final AppPropertiesConfig appPropertiesConfig;

    private final ChatClient chatClient;

//    note to ask any question releted to tthe document
    public ChatResponseDto askQuestions(ChatRequestDto request){

        long startTime = System.currentTimeMillis();
        logger.info("processing question: '{}' (scoped documentId: {})", request.getQuestion(), request.getDocumentId());

       List<Document> similardocuments = this.retrieveReleventDocuments(
                request.getQuestion(),
                request.getDocumentId(),
                request.getTopK(),
                request.getMinSimilarity()
        );

     List<CitationDto> citationDtos =   similardocuments.stream()
               .map(this::mapToCitation)
               .toList();

     String contextText = buildContextText(similardocuments);

     String prompt = buildPrompt(request.getQuestion(), contextText);
   String answer = this.chatClient.prompt()
            .user(prompt)
            .call()
            .content();

   long responseTime = System.currentTimeMillis() - startTime;
   logger.info("Question processed in {} ms. Answer: {}", responseTime, citationDtos.size());
   return ChatResponseDto.builder()
           .answer(answer)
           .converstationId(request.getConverstationId())
           .citations(citationDtos)
           .responseTimeMs(responseTime)
           .build();
    }


//    hack this streams
    public Flux<String> streamQuestionsAnswer(ChatRequestDto requestDto){
        logger.info("Streaming query: '{}'", requestDto.getQuestion());
        List<Document> relevantdocuments = this.retrieveReleventDocuments(
                requestDto.getQuestion(),
                requestDto.getDocumentId(),
                requestDto.getTopK(),
                requestDto.getMinSimilarity()
        );
        String contextText = buildContextText(relevantdocuments);
        String userprompt = buildPrompt(requestDto.getQuestion(), contextText);

        return this.chatClient.prompt()
                .user(userprompt)
                .stream()
                .content();
    }
//  note to build the prompt for the chat model based on the question and context text
    private String buildPrompt(@NotBlank(message = "Question cannot be blank") String question, String contextText) {

        if(contextText != null && !contextText.isBlank()) {
            return String.format("""
                    Document Context:
                    -----------------
                    %s
                    
                    User Message / Question: %s
                    Instructions:
                    - If the user's question relates to the document context above, prioritize answering using that context.
                    - If the user is asking a general question, greeting, or discussing topics beyond the document context,
                    """, contextText, question);
        }
        else
        {
            return String.format("""
                    User Message / Question: 
                    
                    %s
                    
                    Instructions:
                    - Respond helpfully, accurately, and conversationally to the user's message using your broad knowledge base.
                    """, question);
        }

    }


//note to build context text from the similar documents
    private  String buildContextText(List<Document> similardocuments) {
        if(similardocuments.isEmpty() || similardocuments == null) {
            return "";
        }

        return similardocuments.stream()
                .map(doc -> {
                    String fileName = doc.getMetadata().getOrDefault("filename", "UnknownFile").toString();
                    Object page = doc.getMetadata().getOrDefault("page" , "N/A");
                    return String.format("[Source: %s | Page: %s]\n%s", fileName, page, doc.getText());
                }).collect(Collectors.joining("\\n\\n---\\n\\n"));

    }


//note to search for similar chunks of documents based on the query and other parameters
    public SearchResultDto searchSimilarChunks(SearchRequestDto request) {

        List<Document> matchedDocs = retrieveReleventDocuments(request.getQuery(),
                request.getDocumentId(),
                request.getTopK(),
                request.getSimilartySearch());

        List<CitationDto> citations = matchedDocs.stream()
                .map(this::mapToCitation)
                .toList();
        return SearchResultDto.builder()
                .query(request.getQuery())
                .totalMatches(citations.size())
                .matches(citations)
                .build();
    }

//    concept to map the document to citation dto
    private CitationDto mapToCitation(Document document) {
        Map<String, Object> meta = document.getMetadata();
        UUID documentId = null;
        if(meta.get("documentId") != null) {
            try{
                documentId = UUID.fromString(meta.get("documentId").toString());
            } catch (Exception e) {
                    throw new RuntimeException(e);
            }
        }
        Integer chunkIndex = null;
        if(meta.get("chunkIndex") instanceof Number n) {
            chunkIndex = n.intValue();
        }

        Integer pageNumber = null;
        if(meta.get("pageNumber") instanceof Number n) {
            pageNumber = n.intValue();
        }

        Double score = null;
        if(meta.get("similarityScore") instanceof Number n) {
            score = 1.0 - n.doubleValue();
        }

        return CitationDto.builder()
                .documentid(documentId)
                .filename((String) meta.getOrDefault("filename" , "Unknown"))
                .chunkIndex(chunkIndex)
                .pageNumber(pageNumber)
                .snippet(document.getText())
                .similarityScore(score)
                .metadata(meta)
                .build();
    }

//    hack using vector store for searching similar documents based on the query and other parameters
    private List<Document> retrieveReleventDocuments(@NotBlank(message = "Query cannot be blank") String query, UUID documentId, Integer topK, Double similartySearch) {

       //note Implement the logic to retrieve relevant documents from the vector store based on the query, documentId, topK, and similaritySearch
        // hack This is a placeholder implementation and should be replaced with actual retrieval logic

        int effectiveTopK = (topK != null && topK > 0) ? topK : appPropertiesConfig.getRag().getTopK();
        double effectiveSimilaritySearch = (similartySearch != null && similartySearch > 0) ? similartySearch : appPropertiesConfig.getRag().getSimilarityThreshold();

        SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
                .query(query)
                .topK(effectiveTopK);

        if(effectiveSimilaritySearch > 0.0) {
            searchRequestBuilder.similarityThreshold(effectiveSimilaritySearch);
        }

        if(documentId != null) {
            FilterExpressionBuilder b = new FilterExpressionBuilder();
            searchRequestBuilder.filterExpression(b.eq("documentId", documentId.toString()).build());
        }

        try{
            List<Document> documents = vectorStore.similaritySearch(searchRequestBuilder.build());
            logger.error("Retrieved {} chunks for query: '{}' (scoped documentId: {})", documents.size(), query, documentId);
            return documents;
        }catch(Exception e){
             logger.error("Similarty search failed foe query: '{}'", query, e);
             return Collections.emptyList();
        }
    }


}
