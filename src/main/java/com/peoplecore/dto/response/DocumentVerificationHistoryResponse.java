package com.peoplecore.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerificationHistoryResponse {

    private Long id;

    private Long documentId;

    private Long employeeId;

    private String actionType;

    private String status;

    private String remarks;

    private String performedBy;

    private LocalDateTime performedAt;

    private String ipAddress;

    private String oldValue;

    private String newValue;
}
