package Ishanknjr.example.DocMind.Service;

import Ishanknjr.example.DocMind.DTO.DocumentMetaDataDto;
import Ishanknjr.example.DocMind.DTO.DocumentResponseDto;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import Ishanknjr.example.DocMind.Enums.DocumentStatus;
import Ishanknjr.example.DocMind.Exceptions.DocumentProcessingExceptions;
import Ishanknjr.example.DocMind.Exceptions.ResourceNotFoundException;
import Ishanknjr.example.DocMind.Repository.DocumentMetaDataRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentMetaDataService {

    private static final Logger log = LoggerFactory.getLogger(DocumentMetaDataService.class);
    private final DocumentMetaDataRepo documentMetaDataRepo;
    private final DocumentParserService parserService;
    private final DocumentIngestionService ingestionService;
    private final ModelMapper modelMapper;
    public final JdbcTemplate jdbcTemplate;

//  note   method to upload and pass document
    public DocumentResponseDto uploadAndProcess(MultipartFile file) {
       String fileName =  file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
      String contentType =  file.getContentType() != null ? file.getContentType() : "application/octet-stream";

//   hack    documentmeta data
        DocumentMetaData documentMetaData = DocumentMetaData.builder()
                .fileName(fileName)
                .contentType(contentType)
                .status(DocumentStatus.UPLOADING)
                .fileSize(file.getSize())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        //      bug   save the document metadata
        System.out.println(documentMetaData.toString());

       int chunksCreated =0;

        try{
          List<Document> parsedDocs = parserService.parse(file);
          chunksCreated = ingestionService.ingest(documentMetaData , parsedDocs);
        }catch(DocumentProcessingExceptions e){
            log.info("DocumentMetaData deleting: processing due to fail processing" + e.getMessage());
            documentMetaDataRepo.delete(documentMetaData);
            throw e;
        }


        documentMetaData =
                documentMetaDataRepo.save(documentMetaData);


// note parse the file list fo document
        List<Document> parsedDocs = parserService.parse(file);

//    note    intgest service  for returning service chunks
         chunksCreated = ingestionService.ingest(documentMetaData , parsedDocs);


//    note    documentMetaData.setTotalChunks(chunksCreated);


       return DocumentResponseDto.builder()
               .id(documentMetaData.getId())
               .filename(documentMetaData.getFileName())
               .fileSize(documentMetaData.getFileSize())
               .chunksCreated(chunksCreated)
               .status(documentMetaData.getStatus())
               .message("Document successfully processed and indexed")
               .build();


    }

    public List<DocumentResponseDto> uploadMultipleDocuments(List<MultipartFile> files) {
        List<DocumentResponseDto> responseDtos = new ArrayList<>();

        for(MultipartFile file : files){
            DocumentResponseDto result = this.uploadAndProcess(file);
            responseDtos.add(result);
        }

        return responseDtos;
    }

    public List<DocumentMetaDataDto> getAllDocuments() {
       List<DocumentMetaData> alldocuments =  documentMetaDataRepo.findAllByOrderByCreatedAtDesc();

      return  alldocuments.stream()
              .map(documentMetaData -> modelMapper.map(documentMetaData , DocumentMetaDataDto.class))
              .toList();
    }

    public DocumentMetaDataDto  getDocumentById(UUID id) {
    DocumentMetaData documentMetaData =  documentMetaDataRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        return modelMapper.map(documentMetaData , DocumentMetaDataDto.class);
    }

    public void deleteDocumentById(UUID id) {
        DocumentMetaData doc = documentMetaDataRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));

        // mark Delete the document metadata
         try
         {
             String deleteVectorSql = "DELETE FROM vector_store WHERE metadata --> 'documentId' = ?";
            int deletedCount =  jdbcTemplate.update(deleteVectorSql , id.toString());
            log.info("deleted {} vector chunks form document id {} ", deletedCount, id);
         }catch(Exception e)
         {
             log.warn("Cloud not delete vectors from vector store directly {}",e.getMessage());

         }


    }
}
