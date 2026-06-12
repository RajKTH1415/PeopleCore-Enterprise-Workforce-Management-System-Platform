package com.peoplecore.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRestoreHistoryResponse {

    private Long id;

    private Long documentId;

    private Long employeeId;

    private String actionType;

    private String fileName;

    private String fileUrl;

    private String performedBy;

    private LocalDateTime performedAt;

    private String ipAddress;

    private String userAgent;

    private String status;

    private String remarks;

    private String oldValue;

    private String newValue;
}