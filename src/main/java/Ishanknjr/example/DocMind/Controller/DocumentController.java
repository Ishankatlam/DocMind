package Ishanknjr.example.DocMind.Controller;

import Ishanknjr.example.DocMind.DTO.ApiResponse;
import Ishanknjr.example.DocMind.DTO.DocumentMetaDataDto;
import Ishanknjr.example.DocMind.DTO.DocumentResponseDto;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import Ishanknjr.example.DocMind.Service.DocumentMetaDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

//    concept Api to upload multiple documents at once
    @PostMapping(value = "/upload-multiple" , consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload Multiple Documents",
            description = "Uploads multiple documents, processes them, and returns their metadata."
    )
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> uploadMultipleDocuments(

            @RequestParam("files")
            List<MultipartFile> files
    ) {
        // mark Implementation for uploading multiple documents
           List<DocumentResponseDto> responseDtos =  documentService.uploadMultipleDocuments(files);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<List<DocumentResponseDto>>builder()
                        .success(true)
                        .data(responseDtos)
                        .timestamp(LocalDateTime.now())
                        .message("Multiple Documents uploaded and indexed Successfully")
                        .build()
        );
    }

//    mark Api to list all documents
    @GetMapping
    @Operation(
            summary = "List All Documents",
            description = "Retrieves a list of all uploaded documents along with their metadata."
    )
       public ResponseEntity<ApiResponse<List<DocumentMetaDataDto>>> getAlldocuments()
       {
         List<DocumentMetaDataDto> documents = documentService.getAllDocuments();
         return ResponseEntity.ok(
                 ApiResponse.<List<DocumentMetaDataDto>>builder()
                         .success(true)
                         .data(documents)
                         .timestamp(LocalDateTime.now())
                         .message("all Documents is here")
                         .build()
         );
       }
       @GetMapping("/{id}")
       @Operation(
                summary = "Get Documents by ID",
                description = "Retrieves documents based on their unique identifiers."
       )
       public ResponseEntity<ApiResponse<DocumentMetaDataDto>> getDocumentsById(@PathVariable UUID id)
       {
           DocumentMetaDataDto document = documentService.getDocumentById(id);
              return ResponseEntity.ok(
                      ApiResponse.<DocumentMetaDataDto>builder()
                              .success(true)
                              .data(document)
                              .timestamp(LocalDateTime.now())
                              .message("single document is here")
                              .build()
              );
       }

       @DeleteMapping("/{id}")
       @Operation(
                summary = "Delete Document by ID",
                description = "Deletes a document based on its unique identifier."
       )
       public ResponseEntity<ApiResponse<Void>> deleteDocumentById(@PathVariable UUID id)
       {
           documentService.deleteDocumentById(id);
           return ResponseEntity.ok(
                   ApiResponse.<Void>builder()
                           .success(true)
                           .timestamp(LocalDateTime.now())
                           .message("Document deleted successfully")
                           .data(null)
                           .build()
           );
       }

}
