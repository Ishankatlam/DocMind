package Ishanknjr.example.DocMind.Exceptions;


public class DocumentProcessingExceptions extends RuntimeException {
    public DocumentProcessingExceptions(String message) {
        super(message);
    }
    public DocumentProcessingExceptions() {
        super("Error in processing document");
    }

    public DocumentProcessingExceptions(String message, Throwable cause) {
        super(message, cause);
    }
}
