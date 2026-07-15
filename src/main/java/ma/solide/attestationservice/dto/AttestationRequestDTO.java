package ma.solide.attestationservice.dto;

import lombok.Data;

@Data
public class AttestationRequestDTO {
    private Integer userId;
    private String studentName;
    private String className;
    private String type;       // enrollment | attendance | conduct | academic | registration
    private String reason;     // optional reason / note from student
}

