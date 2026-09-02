package Ishanknjr.example.DocMind.Service;

import Ishanknjr.example.DocMind.Exceptions.DocumentProcessingExceptions;
import org.slf4j.Logger;
import org.springframework.lang.Nullable;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class DocumentParserService {

    private static final Logger log = LoggerFactory.getLogger(DocumentParserService.class.getName());

    public List<Document> parse(MultipartFile file) {
        String fileName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType().toLowerCase() : "";

        log.info("Parsing Document: {}, size: {} bytes, contentType: {}", fileName, file.getSize(), contentType);

        try {
            Resource resource = new ByteArrayResource(file.getBytes()) {
                @Override
                public @Nullable String getFilename() {
                    return fileName;
                }
            };

            if (fileName.toLowerCase().endsWith(".pdf") || contentType.contains("application/pdf")) {
                return parsePdf(resource);
            } else {
                return parseGenericFile(resource);
            }

        } catch (IOException ex) {
            log.error("Failed to read file bytes {}", fileName, ex);
            throw new DocumentProcessingExceptions("Could not read uploaded file: " + fileName, ex);
        } catch (Exception e) {
            log.error("Failed during document parsing {}", fileName, e);
            throw new DocumentProcessingExceptions("Failed to parse document content: " + fileName, e);
        }
    }

    private List<Document> parsePdf(Resource resource) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageBottomMargin(0)
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(
                        ExtractedTextFormatter.builder()
                                .withNumberOfBottomTextLinesToDelete(0)
                                .build()
                )
                .build();
        PagePdfDocumentReader pagePdfDocumentReader = new PagePdfDocumentReader(resource, config);
        return pagePdfDocumentReader.read();
    }

    private List<Document> parseGenericFile(Resource resource) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        return tikaDocumentReader.read();
    }
}