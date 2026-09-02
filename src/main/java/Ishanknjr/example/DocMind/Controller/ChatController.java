package Ishanknjr.example.DocMind.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(
        name = "Chat Management",
        description = "All chat related APIs goes here "
)
public class ChatController {

      @PostMapping
      @Operation(
              summary = "Upload and index a document(PDF , DOCX , MD  , CSv , TEXT)",
              description = "This api is used to upload and index documents files"
      )
    public ResponseEntity<String> chat(){
        return ResponseEntity.ok("This is just testing Configurations");
    }
}
