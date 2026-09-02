package Ishanknjr.example.DocMind.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatResponseDto {
    private String answer;
    private String converstationId;
    private List<CitationDto> citations;
    private Long responseTimeMs;
}
