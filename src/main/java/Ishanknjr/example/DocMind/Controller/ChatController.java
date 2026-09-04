package Ishanknjr.example.DocMind.Controller;

import Ishanknjr.example.DocMind.DTO.*;
import Ishanknjr.example.DocMind.Service.RagService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(
        name = "Chat Management",
        description = "All chat related APIs goes here "
)
@RequiredArgsConstructor
public class ChatController {

    private final RagService ragService;

    @PostMapping("/query")
    @Operation(
            summary = "Ask Questions",
            description = "Accepts a question against all documents or a specific document with citation and returns the answer from the LLM"
    )
    public ResponseEntity<ApiResponse<ChatResponseDto>> askQuestions(
            @Valid @RequestBody ChatRequestDto requestDto
    ){
       ChatResponseDto chatResponseDto = ragService.askQuestions(requestDto);
         return ResponseEntity.ok(

                 ApiResponse.
                         <ChatResponseDto>
                         builder()
                         .success(true)
                         .message(null)
                         .data(chatResponseDto)
                         .timestamp(LocalDateTime.now())
                         .build()
         );
    }

    @PostMapping("/stream")
    @Operation
    public Flux<String> streamQuestions(
            @Valid @RequestBody ChatRequestDto requestDto
    ){
        return ragService.streamQuestionsAnswer(requestDto);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Search Similar Chunks",
            description = "Accepts a search request and returns similar chunks from the vector store"
    )
    public ResponseEntity<ApiResponse<SearchResultDto>> searchSimilar(@Valid @RequestBody SearchRequestDto request)
    {
        SearchResultDto results = ragService.searchSimilarChunks(request);
        return ResponseEntity.ok(
                ApiResponse.<SearchResultDto>builder()
                        .success(true)
                        .message(null)
                        .data(results)
                        .timestamp(LocalDateTime.now())
                        .build()
        );
    }


}
