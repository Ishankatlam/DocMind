package Ishanknjr.example.DocMind.Service;

import Ishanknjr.example.DocMind.Config.AppPropertiesConfig;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import Ishanknjr.example.DocMind.Enums.DocumentStatus;
import Ishanknjr.example.DocMind.Exceptions.DocumentProcessingExceptions;
import Ishanknjr.example.DocMind.Repository.DocumentMetaDataRepo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Getter
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class.getName());
    private final VectorStore vectorStore;
    private final DocumentMetaDataRepo documentMetaDataRepo;
    private final AppPropertiesConfig appProperties;


    public int ingest(DocumentMetaData metadata, List<Document> parsedDocs) {
        log.info("Ingesting document [id={},name={},pages={}]",metadata.getId(),metadata.getFileName(),metadata.getFileSize());

        try{

            metadata.setStatus(DocumentStatus.PROCESSING);
            metadata.setTotalPages(parsedDocs.size());
//        hack    1.Text chunking using TokenTextSplitter

            TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                    .withChunkSize(appProperties.getRag().getChunkSize())
                    .withMinChunkSizeChars(appProperties.getRag().getMinChunkSizeChars())
                    .withMinChunkLengthToEmbed(appProperties.getRag().getMinChunkLengthToEmbed())
                    .withMaxNumChunks(appProperties.getRag().getMaxChunks())
                    .withKeepSeparator(true)
                    .build();

            List<Document> chunks = tokenTextSplitter.apply(parsedDocs);
            if(chunks.isEmpty())
            {
                metadata.setStatus(DocumentStatus.FAILED);
                metadata.setErrorMessage("Document Appears to empty or unscannable");
                documentMetaDataRepo.save(metadata);
                return 0;
            }

//       note   2. metadata Enrichment on each chunks
            List<Document> enrichedChunks = new ArrayList<>();
            for(int i=0;i<chunks.size();i++)
            {
                Document chunk = chunks.get(i);
                Map<String , Object> enrichMetaData = new HashMap<>(chunk.getMetadata());
                enrichMetaData.put("documentId" , metadata.getId().toString());
                enrichMetaData.put("filename" , metadata.getFileName());
                enrichMetaData.put("contentType" , metadata.getContentType());
                enrichMetaData.put("chunkIndex" ,i);

//             Note   3. preserve or calculate the page number if available
                   Object pageNumber = chunk.getMetadata().get("page_Number");
                   if(pageNumber == null)
                   {
                       pageNumber = chunk.getMetadata().get("pageNumber");
                   }
                   if(pageNumber != null)
                   {
                       enrichMetaData.put("pageNumber" , pageNumber);
                   }
                Document enrichedDoc = new Document(chunk.getText(), enrichMetaData);
                   enrichedChunks.add(enrichedDoc);

            }
//         note     4.write chunks and embeddings to pgvector

            log.info("write {} vector chunks to Pgvector for document: {}" , enrichedChunks.size() , metadata.getFileName());

            vectorStore.add(enrichedChunks);

//            note   5.update document status to index
            metadata.setStatus(DocumentStatus.INDEXED);
            metadata.setTotalChunks(enrichedChunks.size());
            metadata.setErrorMessage(null);
            documentMetaDataRepo.save(metadata);

            return enrichedChunks.size();



        }catch(Exception e){
            log.error("Failed to ingest document into vector store: {}" , metadata.getFileName() , e);
            metadata.setStatus(DocumentStatus.FAILED);
            metadata.setErrorMessage(e.getMessage());
            documentMetaDataRepo.save(metadata);
            throw new DocumentProcessingExceptions("Failed to index Document: " + e.getMessage() , e);
        }
    }

}
