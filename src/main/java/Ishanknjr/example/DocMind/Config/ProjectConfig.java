package Ishanknjr.example.DocMind.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import jakarta.annotation.PostConstruct;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

@Configuration
public class ProjectConfig {

    private static final Logger log = LoggerFactory.getLogger(ProjectConfig.class);

    @Value("${spring.ai.google.genai.api-key:}")
    private String apiKey;

    @PostConstruct
    public void validateGoogleApiKey() {
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("Missing Gemini API key. Set GEMINI_API_KEY or spring.ai.google.genai.api-key.");
        }
        log.info("Google Gemini API key detected: {}", maskKey(apiKey));
    }

    private String maskKey(String key) {
        if (!StringUtils.hasText(key)) {
            return "<missing>";
        }
        String suffix = key.length() <= 4 ? key : key.substring(key.length() - 4);
        return "****" + suffix;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("DocMind - AI Intelligence & RAG Backend")
                                .description("REST API for DocMind : Multi-format document ingestion, vector embeddings with PostgreSQL pgvector, and hybrid conversational Q&A with Google Gemini.")
                                .version("1.0.0")
                                .contact(new Contact()
                                        .name("Substring Technologies")
                                        .email("support@substringtechnologies.com")
                                        .url("https://www.substringtechnologies.com")
                                )
                );
    }

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("""
                        You are DocMind, an intelligent, versatile, and friendly AI document intelligence assistant.
                        
                        Your Capabilities:
                        
                        1. Document-Grounded Q&A: When context from the user's uploaded documents is provided, prioritize and base your answer directly on that context, citing document names and page numbers when available.
                        
                        2. General Knowledge & Conversation: If the user engages in general conversation (greetings, chit-chat, programming questions, math, explanations, summaries, or general knowledge) that may not be present in the uploaded documents, answer using your broader knowledge.
                        
                        3. Hybrid Synthesis: If the document context partially covers a topic, synthesize the document facts with your broader knowledge to give a complete, high-quality answer.
                        
                        4. Tone & Format: Always be warm, professional, clear, and structured. Use Markdown (headings, bullet points, bold text, code blocks) to make responses easy to read.
                        
                        """)

                .build();
    }

//    @Bean
//    public EmbeddingModel embeddingModel() {
//        OpenAiApi openAiApi = new OpenAiApi(
//                "https://generativelanguage.googleapis.com/v1beta/openai",
//                apiKey
//        );
//        return new OpenAiEmbeddingModel(openAiApi);
//    }

    // Explicit VectorStore Bean definition
    @Bean
    public VectorStore vectorStore(JdbcTemplate jdbcTemplate, EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .schemaName("public")
                .vectorTableName("vector_store")
                .dimensions(3072)
                .distanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .indexType(PgVectorStore.PgIndexType.NONE)
                .initializeSchema(true)
                .build();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}