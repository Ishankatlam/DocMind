package Ishanknjr.example.DocMind.Exceptions;


import Ishanknjr.example.DocMind.DTO.ApiResponse;
import Ishanknjr.example.DocMind.Repository.DocumentMetaDataRepo;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice

public class GlobalExceptionhandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionhandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex){

        logger.warn("Resource not found : {}" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.builder()
                        .success(false)
                        .message(ex.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build());

    }
   @ExceptionHandler(DocumentProcessingExceptions.class)
    public ResponseEntity<ApiResponse<Object>> handleProcessingException(DocumentProcessingExceptions ex){
          logger.error(" Document Processing Exception : {}" , ex.getMessage() , ex);
          return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                  .body(ApiResponse.builder()
                          .success(false)
                                  .message("Failed to process Document:" + ex.getMessage())
                                  .data(null)
                                  .timestamp(LocalDateTime.now()).build()


                          );
    }
   @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxSize(MaxUploadSizeExceededException ex){
        logger.warn("Max Upload Size Exceeded : {}" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(ApiResponse.builder()
                        .success(false)
                        .message("File size exceeds the allowed limit (25MB) " + ex.getMessage())
                        .data(null)
                        .timestamp(LocalDateTime.now()).build()
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String , String>>> handleValidation(MethodArgumentNotValidException ex){
//         key value pair --> validation error
         Map<String , String > error = new HashMap<>();

         for(FieldError feilderror : ex.getBindingResult().getFieldErrors())
         {
             error.put(feilderror.getField() , feilderror.getDefaultMessage());

         }

         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                 .body(
                         ApiResponse.<Map<String , String>>builder()
                                 .success(false)
                                 .message("Validation failed")
                                 .data(error)
                                 .timestamp(LocalDateTime.now())
                                 .build()
                 );
    }

     @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex){
        logger.warn("Invalid Arguments : {}" , ex.getMessage());
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(
                        ApiResponse.builder()
                .success(false)
                .message(ex.getMessage())
                .data(null)
                                .timestamp(LocalDateTime.now())
                                .build()

                );
    }

//     generalized exception handler


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenerics(Exception ex){
        logger.error("Exception : {}" , ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.builder()
                        .success(false)
                        .message("An unexpected error occured :" + ex)
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build()
        );


    }

}
