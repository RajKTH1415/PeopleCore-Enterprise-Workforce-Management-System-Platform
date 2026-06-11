package com.peoplecore.service;

import com.peoplecore.dto.response.DocumentAccessLogResponse;
import com.peoplecore.dto.response.DocumentVersionResponse;
import com.peoplecore.dto.response.EmployeeDocumentAuditResponse;
import com.peoplecore.dto.response.PageResponse;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface DocumentAuditService {

    PageResponse<EmployeeDocumentAuditResponse> getAuditLogs(
            Long documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request);

    PageResponse<DocumentAccessLogResponse> getAccessLogs(
            String documentId,
            int page,
            int size,
            String sortBy,
            String direction,
            HttpServletRequest request
    );

    List<DocumentVersionResponse> getDocumentVersions(
            String documentId
    );
}
