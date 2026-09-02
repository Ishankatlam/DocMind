package Ishanknjr.example.DocMind.Controller;

import Ishanknjr.example.DocMind.DTO.ApiResponse;
import Ishanknjr.example.DocMind.DTO.DocumentResponseDto;
import Ishanknjr.example.DocMind.Service.DocumentMetaDataService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/documents")
@Tag(
        name="Document Management",
        description = "Endpoints for uploading , listing and managing documents and their vector embeddings"
)
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentMetaDataService documentService;

  @PostMapping(value = "/upload" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
          @RequestParam("file")MultipartFile file
          ) {

//       process the file
      DocumentResponseDto documentResponseDto =
              documentService.uploadAndProcess(file);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<DocumentResponseDto>builder()
                        .success(true)
                        .data(documentResponseDto)
                        .timestamp(LocalDateTime.now())
                        .message("Documents uploaded and indexed Successfully")

                        .build()
        );
    }

}
