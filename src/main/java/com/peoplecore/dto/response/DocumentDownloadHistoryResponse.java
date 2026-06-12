package com.peoplecore.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDownloadHistoryResponse {

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
}
