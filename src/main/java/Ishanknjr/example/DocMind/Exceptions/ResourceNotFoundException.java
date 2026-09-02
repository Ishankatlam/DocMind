package Ishanknjr.example.DocMind.Exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException() {
        super("Resource you are looking not found");
    }
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
