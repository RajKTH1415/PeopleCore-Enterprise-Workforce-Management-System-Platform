package com.peoplecore.controller;
import com.peoplecore.dto.request.*;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.CertificationStatus;
import com.peoplecore.service.CertificationIssuerService;
import com.peoplecore.service.CertificationService;
import com.peoplecore.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/certifications")
public class CertificationsController {

    private final CertificationService certificationService;
    private final CertificationIssuerService certificationIssuerService;

    public CertificationsController(CertificationService certificationService, CertificationIssuerService certificationIssuerService){
        this.certificationService = certificationService;
        this.certificationIssuerService = certificationIssuerService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CertificationResponse>> createCertification(@RequestBody CertificationRequest certificationRequest , HttpServletRequest httpServletRequest){
         CertificationResponse certificationResponse = certificationService.createCertification(certificationRequest);
         return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.OK.value(), "Certification created successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }

    @PostMapping("/bulk")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> bulkCreateCertifications(@RequestBody List<CertificationRequest> requests, HttpServletRequest httpServletRequest) {
        List<CertificationResponse> response = certificationService.bulkCreateCertifications(requests);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Certifications created successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> getCertificationById(@PathVariable("id") Long Id , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.getById(Id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification fetched successfully",httpServletRequest.getRequestURI(), certificationResponse));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> deleteCertificateById(@PathVariable("id") Long id , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.deleteCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification deleted successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<CertificationResponse>>> getAllCertifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate") String sortBy,
            @RequestParam(defaultValue = "desc") String direction,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String issuer,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "false") Boolean includeDeleted,
            HttpServletRequest httpServletRequest) {

        PageResponse<CertificationResponse> response =
                certificationService.getAllCertifications(
                        page, size, sortBy, direction, name, issuer, search, includeDeleted);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(),"All certifications fetched successfully",httpServletRequest.getRequestURI(),response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CertificationResponse>> updateCertificate(@PathVariable("id") Long id , @RequestBody CertificationRequest certificationRequest , HttpServletRequest httpServletRequest){
        CertificationResponse certificationResponse = certificationService.updateCertification(id, certificationRequest);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification updated successfully", httpServletRequest.getRequestURI(), certificationResponse));
    }

    @PutMapping("/bulk")
    public ResponseEntity<ApiResponse<List<CertificationResponse>>> bulkUpdateCertifications(@RequestBody
            List<BulkUpdateCertificationRequest> requests,
            HttpServletRequest httpServletRequest) {

        List<CertificationResponse> response = certificationService.bulkUpdateCertifications(requests);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certifications updated successfully", httpServletRequest.getRequestURI(), response));
    }

    @PutMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<CertificationResponse>> restoreCertification(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        CertificationResponse response = certificationService.restoreCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification restored successfully", httpServletRequest.getRequestURI(), response));
    }

    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<ApiResponse<Void>> permanentlyDeleteCertification(@PathVariable Long id, HttpServletRequest httpServletRequest) {
        certificationService.permanentlyDeleteCertification(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification permanently deleted successfully", httpServletRequest.getRequestURI(), null ));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CertificationResponse>> updateCertificationStatus(
            @PathVariable Long id,
            @RequestBody UpdateCertificationStatusRequest request,
            HttpServletRequest httpRequest) {

        CertificationResponse response = certificationService.updateCertificationStatus(id, request);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification status updated successfully", httpRequest.getRequestURI(), response));
    }

    @GetMapping("/analytics/usage")
    public ResponseEntity<ApiResponse<CertificationUsageAnalyticsResponse>> getCertificationUsageAnalytics(HttpServletRequest httpServletRequest) {
        CertificationUsageAnalyticsResponse response = certificationService.getCertificationUsageAnalytics();
      return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification usage analytics fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @PostMapping("/{certificationId}/skills")
    public ResponseEntity<ApiResponse<CertificationSkillResponse>> addSkills(@PathVariable("certificationId") Long id, @RequestBody CertificationSkillRequest request, HttpServletRequest httpServletRequest) {
        CertificationSkillResponse response = certificationService.addSkills(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Skills mapped to certification successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{certificationId}/skills")
    public ResponseEntity<ApiResponse<CertificationSkillResponse>> getSkills(@PathVariable("certificationId") Long id, HttpServletRequest httpServletRequest) {
        CertificationSkillResponse response = certificationService.getSkills(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification skills fetched successfully", httpServletRequest.getRequestURI(), response));
    }
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSuggestions(@RequestParam String query, HttpServletRequest httpServletRequest) {

        List<String> suggestions = certificationService.getSuggestions(query);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Suggestions fetched successfully", httpServletRequest.getRequestURI(), suggestions));
    }

    @GetMapping("/issuers")
    public ResponseEntity<ApiResponse<List<CertificationIssuerResponse>>>
    getAllIssuers(
            HttpServletRequest httpServletRequest
    ) {

        List<CertificationIssuerResponse> response =
                certificationIssuerService.getAllIssuers();

        return ResponseEntity.status(HttpStatus.OK)
                .body(
                        ApiResponse.success(
                                HttpStatus.OK.value(),
                                "Certification issuers fetched successfully",
                                httpServletRequest.getRequestURI(),
                                response
                        )
                );
    }
    @PostMapping("/issuers")
    public ResponseEntity<ApiResponse<CertificationIssuerResponse>> createIssuer(@RequestBody CertificationIssuerRequest request, HttpServletRequest httpServletRequest) {
        CertificationIssuerResponse response = certificationIssuerService.createIssuer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(HttpStatus.CREATED.value(), "Certification issuer created successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/{id}/audit")
    public ResponseEntity<ApiResponse<List<CertificationAuditResponse>>> getCertificationAudit(@PathVariable("id") Long id, HttpServletRequest httpServletRequest) {

        List<CertificationAuditResponse> response = certificationService.getCertificationAudit(id);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.success(HttpStatus.OK.value(), "Certification audit logs fetched successfully", httpServletRequest.getRequestURI(), response));
    }

    @GetMapping("/export")
    public ResponseEntity<ApiResponse<ExportResponse>> exportCertifications(
            @RequestParam String format,

            @RequestParam(required = false)
            CertificationStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            HttpServletRequest httpServletRequest) {

        LocalDateTime fromDateTime =
                from != null
                        ? from.atStartOfDay()
                        : null;

        LocalDateTime toDateTime =
                to != null
                        ? to.atTime(23, 59, 59)
                        : null;

        String fileName =
                certificationService.exportCertifications(
                        format,
                        status,
                        fromDateTime,
                        toDateTime);

        ExportResponse exportResponse =
                ExportResponse.builder()
                        .success(true)
                        .message(
                                "Certification export generated successfully"
                        )
                        .fileName(fileName)
                        .format(format)
                        .generatedAt(
                                LocalDateTime.now().toString()
                        )
                        .expiresIn("10 minutes")
                        .downloadUrl(
                                httpServletRequest.getScheme()
                                        + "://"
                                        + httpServletRequest.getServerName()
                                        + ":"
                                        + httpServletRequest.getServerPort()
                                        + "/api/v1/certifications/download/"
                                        + fileName
                        )
                        .build();

        ApiResponse<ExportResponse> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Export completed successfully",
                        httpServletRequest.getRequestURI(),
                        exportResponse
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<Resource> downloadCertificationExport(
            @PathVariable String fileName) {
        return certificationService.downloadCertificationExport(fileName);
    }

    @GetMapping("/exports/history")
    public ResponseEntity<ApiResponse<List<ExportHistoryResponse>>> getExportHistory(
            HttpServletRequest request) {

        List<ExportHistoryResponse> exportHistory = certificationService.getExportHistory();

        ApiResponse<List<ExportHistoryResponse>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Export history fetched successfully",
                        request.getRequestURI(),
                        exportHistory);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/downloads/history")
    public ResponseEntity<ApiResponse<List<ExportHistoryResponse>>> getDownloadHistory(
            HttpServletRequest request
    ) {

        List<ExportHistoryResponse> downloadHistory =
                certificationService.getDownloadHistory();

        ApiResponse<List<ExportHistoryResponse>> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Download history fetched successfully",
                        request.getRequestURI(),
                        downloadHistory
                );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/exports/{fileName}")
    public ResponseEntity<ApiResponse<String>> deleteExportFile(
            @PathVariable String fileName,
            HttpServletRequest request
    ) {

        certificationService.deleteExportFile(fileName);

        ApiResponse<String> response =
                ApiResponse.success(
                        HttpStatus.OK.value(),
                        "Export file deleted successfully",
                        request.getRequestURI(),
                        fileName
                );

        return ResponseEntity.ok(response);
    }
}
