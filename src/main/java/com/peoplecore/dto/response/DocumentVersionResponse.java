package com.peoplecore.dto.response;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVersionResponse {

    private Long id;

    private String documentId;

    private Integer version;

    private String fileName;

    private Long fileSize;

    private String storageKey;

    private String versionComment;

    private String uploadedBy;

    private LocalDateTime uploadedAt;
}
