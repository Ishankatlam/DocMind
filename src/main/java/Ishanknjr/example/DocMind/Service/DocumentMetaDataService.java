package Ishanknjr.example.DocMind.Service;

import Ishanknjr.example.DocMind.DTO.DocumentResponseDto;
import Ishanknjr.example.DocMind.Entity.DocumentMetaData;
import Ishanknjr.example.DocMind.Enums.DocumentStatus;
import Ishanknjr.example.DocMind.Repository.DocumentMetaDataRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentMetaDataService {

    private static final Logger log = LoggerFactory.getLogger(DocumentMetaDataService.class);
    private final DocumentMetaDataRepo documentMetaDataRepo;
    private final DocumentParserService parserService;
    private final DocumentIngestionService ingestionService;

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


        documentMetaData =
                documentMetaDataRepo.save(documentMetaData);


// note parse the file list fo document
        List<Document> parsedDocs = parserService.parse(file);

//    note    intgest service  for returning service chunks
        int chunksCreated = ingestionService.ingest(documentMetaData , parsedDocs);


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

}
