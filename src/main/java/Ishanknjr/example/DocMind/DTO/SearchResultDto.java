package Ishanknjr.example.DocMind.DTO;

import com.google.genai.types.Citation;
import lombok.*;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultDto {

    private String query;
    private int totalMatches;
    private List<CitationDto> matches;


}
