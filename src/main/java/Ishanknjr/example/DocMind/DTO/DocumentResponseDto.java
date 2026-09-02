package Ishanknjr.example.DocMind.DTO;


import Ishanknjr.example.DocMind.Enums.DocumentStatus;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponseDto {
    private UUID id;
    private String filename;
    private Long fileSize;
    private DocumentStatus  status;
    private int chunksCreated;
    private String message;

}
