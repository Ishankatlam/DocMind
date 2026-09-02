package Ishanknjr.example.DocMind.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchRequestDto {

    @NotBlank(message = "Query cannot be blank")
    private String query;

    private UUID documentId;
    private Integer topK;
    private Double similartySearch ;


}
