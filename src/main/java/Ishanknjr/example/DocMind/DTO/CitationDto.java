package Ishanknjr.example.DocMind.DTO;

import lombok.*;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CitationDto {

    private UUID documentid;
    private String filename;
    private Integer chunkIndex;
    private Integer pageNumber;
    private String snippet;
    private Double similarityScore;
    private Map<String , Object> metadata;



}
