package com.peoplecore.service.Impl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.peoplecore.dto.request.UpdateDocumentRequest;
import com.peoplecore.dto.response.*;
import com.peoplecore.enums.AccessType;
import com.peoplecore.enums.ActionType;
import com.peoplecore.enums.DocumentType;
import com.peoplecore.exception.*;
import com.peoplecore.module.*;
import com.peoplecore.repository.*;
import com.peoplecore.service.EmployeesDocumentsService;
import com.peoplecore.service.FileStorageService;
import com.peoplecore.service.OcrService;
import com.peoplecore.service.SkillParserService;
import com.peoplecore.util.DocumentValidator;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Transactional
@Service
public class EmployeesDocumentsServiceImpl implements EmployeesDocumentsService {


    private final DocumentAccessLogRepository  documentAccessLogRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;
    private final OcrService ocrService;
    private final SkillParserService skillParserService;
    private final CertificationRepository certificationRepository;
    private final EmployeeRepository employeeRepository;
    private final EmployeeCertificationsRepository employeeCertificationsRepository;
    private final EmployeeDocumentRepository employeeDocumentRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final DocumentAuditRepository documentAuditRepository;
    private final EmployeeDocumentSkillMappingRepository employeeDocumentSkillMappingRepository;
    private final EmployeeDocumentCertificationMappingRepository employeeDocumentCertificationMappingRepository;

    @Value("${app.file.upload-dir}")
    private String uploadDir;


    public EmployeesDocumentsServiceImpl(DocumentAccessLogRepository documentAccessLogRepository, FileStorageService fileStorageService, ObjectMapper objectMapper, OcrService ocrService, SkillParserService skillParserService, CertificationRepository certificationRepository, EmployeeRepository employeeRepository, EmployeeCertificationsRepository employeeCertificationsRepository, EmployeeDocumentRepository employeeDocumentRepository, DocumentVersionRepository documentVersionRepository, DocumentAuditRepository documentAuditRepository, EmployeeDocumentSkillMappingRepository employeeDocumentSkillMappingRepository, EmployeeDocumentCertificationMappingRepository employeeDocumentCertificationMappingRepository) {
        this.documentAccessLogRepository = documentAccessLogRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
        this.ocrService = ocrService;
        this.skillParserService = skillParserService;
        this.certificationRepository = certificationRepository;
        this.employeeRepository = employeeRepository;
        this.employeeCertificationsRepository = employeeCertificationsRepository;
        this.employeeDocumentRepository = employeeDocumentRepository;
        this.documentVersionRepository = documentVersionRepository;
        this.documentAuditRepository = documentAuditRepository;
        this.employeeDocumentSkillMappingRepository = employeeDocumentSkillMappingRepository;
        this.employeeDocumentCertificationMappingRepository = employeeDocumentCertificationMappingRepository;
    }

    /*just for testing*/
    @Override
    @Transactional
    public void deleteAllDocumentsSystem() {

        List<EmployeeDocument> documents =
                employeeDocumentRepository.findAll();

        if (documents.isEmpty()) {
            throw new NoDataFoundException(
                    "No documents found to delete");
        }

        try {

            for (EmployeeDocument doc : documents) {

                // Delete Physical File
                if (doc.getFileUrl() != null &&
                        !doc.getFileUrl().isBlank()) {

                    Path path = Paths.get(doc.getFileUrl());

                    if (Files.exists(path)) {
                        Files.delete(path);
                    }
                }

                // Delete Child Records First
                employeeDocumentSkillMappingRepository
                        .deleteByDocumentId(doc.getId());

                employeeDocumentCertificationMappingRepository
                        .deleteByDocumentId(doc.getId());

                documentVersionRepository
                        .deleteByDocumentRefId(doc.getId());

                documentAuditRepository
                        .deleteByDocumentId(doc.getId());
            }

            employeeDocumentRepository.deleteAll();

        } catch (IOException ex) {

            throw new DocumentBulkDeletionException(
                    "Failed to delete document files",
                    ex
            );

        } catch (Exception ex) {

            throw new DocumentBulkDeletionException(
                    "Failed to delete all documents",
                    ex
            );
        }
    }

    @Override
    public DocumentDetailsResponse getDocumentById(Long employeeId, String documentId) {


        Employee employee = employeeRepository
                .findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id : "
                                        + employeeId
                        ));

        // 1. Fetch document
        EmployeeDocument doc = employeeDocumentRepository
                .findByEmployeeIdAndDocumentId(employeeId, documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found with ID : "
                                        + documentId
                                        + " for employee : "
                                        + employeeId
                        ));

        //  2. Fetch version history
        List<DocumentVersionHistory> versions = documentVersionRepository
                .findByDocumentRefIdOrderByVersionDesc(doc.getId());

        if (versions.isEmpty()) {

            throw new NoDataFoundException(
                    "No version history found for document : "
                            + documentId
            );
        }

        List<DocumentVersionDto> versionDtos = versions.stream()
                .map(v -> DocumentVersionDto.builder()
                        .version(v.getVersion())
                        .fileName(v.getFileName())
                        .fileSize(v.getFileSize())
                        .storageKey(v.getStorageKey())
                        .uploadedBy(v.getUploadedBy())
                        .versionComment(v.getVersionComment())
                        .uploadedAt(v.getUploadedAt())
                        .build())
                .toList();

        //  3. Fetch audit logs
        List<EmployeeDocumentAudit> audits = documentAuditRepository
                .findByDocumentIdOrderByPerformedAtDesc(doc.getId());

        List<DocumentAuditDto> auditDtos = audits.stream()
                .map(a -> DocumentAuditDto.builder()
                        .actionType(a.getActionType().name())
                        .accessType(a.getAccessType().name())
                        .remarks(a.getRemarks())
                        .performedBy(a.getPerformedBy())
                        .performedAt(a.getPerformedAt())
                        .oldValue(parseJson(a.getOldValue()))
                        .newValue(parseJson(a.getNewValue()))
                        .build())
                .toList();

        // 4. Build response
        return DocumentDetailsResponse.builder()
                .documentId(doc.getDocumentId())
                .employeeId(employeeId)
                .documentType(doc.getDocumentType())
                .category(doc.getDocumentCategory())
                .title(doc.getTitle())
                .description(doc.getDescription())

                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileSize(doc.getFileSize())

                .version(doc.getVersion())
                .status(doc.getStatus())
                .verificationStatus("PENDING") // or from DB

                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())

                .isPrimary(doc.getIsPrimary())
                .tags(doc.getTags() != null ? Arrays.asList(doc.getTags()) : null)

                .uploadedAt(doc.getUploadedAt())
                .updatedAt(doc.getUpdatedAt())

                .versions(versionDtos)
                .audits(auditDtos)
                .build();
    }
    private Object parseJson(String json) {
        try {
            if (json == null) return null;
            return objectMapper.readValue(json, Object.class);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public DocumentResponse uploadDocument(Long employeeId,
                                           MultipartFile file,
                                           String documentType,
                                           String category,
                                           String title,
                                           String description,
                                           String documentNumber,
                                           LocalDate issueDate,
                                           LocalDate expiryDate,
                                           Boolean isPrimary,
                                           List<String> tags,
                                           HttpServletRequest request) {

        try {

            // 1. FILE VALIDATION
            DocumentValidator.validate(
                    file,
                    documentType,
                    category,
                    title,
                    issueDate,
                    expiryDate
            );

            //  2. READ FILE BYTES (IMPORTANT FIX)
            byte[] fileBytes = file.getBytes();

            //  3. GENERATE HASH
            String fileHash = DigestUtils.sha256Hex(fileBytes);

            // ⚡ 4. DUPLICATE CHECK
            Optional<EmployeeDocument> existingDocOpt =
                    employeeDocumentRepository.findByEmployeeIdAndFileHash(employeeId, fileHash);

            Employee employeeRef = employeeRepository.getReferenceById(employeeId);

            int version = 1;
            EmployeeDocument doc;
            String oldValueJson = null;

            if (existingDocOpt.isPresent()) {

                // UPDATE FLOW
                doc = existingDocOpt.get();

                //  capture OLD state BEFORE change
                oldValueJson = objectMapper.writeValueAsString(doc);

                version = doc.getVersion() + 1;

                doc.setVersion(version);
                doc.setUpdatedAt(LocalDateTime.now());

            } else {

                //  NEW DOCUMENT
                String documentId = "DOC_" + UUID.randomUUID().toString().substring(0, 8);

                doc = EmployeeDocument.builder()
                        .employeeId(employeeId)
                        .documentId(documentId)
                        .documentType(documentType)
                        .documentCategory(category)
                        .title(title)
                        .description(description)
                        .fileName(file.getOriginalFilename())
                        .fileType(file.getContentType())
                        .fileSize(file.getSize())
                        .fileHash(fileHash)
                        .documentNumber(documentNumber)
                        .issueDate(issueDate)
                        .expiryDate(expiryDate)
                        .isPrimary(isPrimary)
                        .tags(tags != null ? tags.toArray(new String[0]) : null)
                        .version(1)
                        .status("ACTIVE")
                        .uploadedAt(LocalDateTime.now())
                        .createdBy("SYSTEM")
                        .build();
            }

            //  5. STORAGE STRUCTURE
            String baseDir = uploadDir + "/documents/" + employeeId + "/";
            Files.createDirectories(Paths.get(baseDir));

            String extension = Objects.requireNonNull(file.getOriginalFilename())
                    .substring(file.getOriginalFilename().lastIndexOf("."));

            String storageFileName = doc.getDocumentId() + "_v" + version + extension;

            Path filePath = Paths.get(baseDir + storageFileName);
            Files.write(filePath, fileBytes);

            doc.setFileUrl(filePath.toString());
            doc.setFileName(file.getOriginalFilename());

            employeeDocumentRepository.save(doc);
            String newValueJson = objectMapper.writeValueAsString(doc);


            String versionComment;

            if (!existingDocOpt.isPresent()) {
                versionComment = "Initial upload";
            } else if ("RESUME".equalsIgnoreCase(documentType)) {
                versionComment = "Resume updated and reprocessed";
            } else if ("CERTIFICATE".equalsIgnoreCase(documentType)) {
                versionComment = "Certificate document updated";
            } else {
                versionComment = "File replaced with new version";
            }

            //  6. VERSION HISTORY
            DocumentVersionHistory versionHistory = DocumentVersionHistory.builder()
                    .documentRefId(doc.getId())
                    .documentId(doc.getDocumentId())
                    .version(version)
                    .fileName(doc.getFileName())
                    .fileSize(file.getSize())
                    .storageKey(storageFileName)
                    .uploadedBy("SYSTEM")
                    .versionComment(versionComment)
                    .uploadedAt(LocalDateTime.now())
                    .build();

            documentVersionRepository.save(versionHistory);

            //  7. CONDITIONAL LOGIC
            if ("CERTIFICATE".equalsIgnoreCase(documentType)) {

                //  1. Fetch employee
                Employee employee = employeeRepository
                        .findById(employeeId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Employee not found with id: "
                                                + employeeId));

                //  2. Fetch certification (by title)
                Certification certification = certificationRepository
                        .findByNameIgnoreCase(title)
                        .orElseGet(() -> {
                            Certification newCert = Certification.builder()
                                    .name(title)
                                    //.createdBy("SYSTEM")
                                    .build();
                            return certificationRepository.save(newCert);
                        });

                //  3. Find or create employee_certification
                EmployeeCertification employeeCertification =
                        employeeCertificationsRepository
                                .findByEmployeeAndCertification(employee, certification)
                                .orElseGet(() -> {
                                    EmployeeCertification newEmpCert = EmployeeCertification.builder()
                                            .employee(employee)
                                            .certification(certification)
                                            .issueDate(issueDate)
                                            .expiryDate(expiryDate)
                                            .status("ACTIVE")
                                            .fileUrl(doc.getFileUrl())
                                            .fileName(doc.getFileName())
                                            .fileSize(doc.getFileSize())
                                            .uploadedAt(LocalDateTime.now())
                                            .build();

                                    return employeeCertificationsRepository.save(newEmpCert);
                                });

//                boolean exists = employeeDocumentCertificationMappingRepository
//                        .existsByDocumentIdAndEmployeeCertificationId(
//                                doc.getId(),
//                                employeeCertification.getId()
//                        );

                if (!employeeDocumentCertificationMappingRepository
                        .existsByDocumentIdAndEmployeeCertificationId(
                                doc.getId(),
                                employeeCertification.getId()
                        )) {

                    employeeDocumentCertificationMappingRepository.save(
                            EmployeeDocumentCertificationMapping.builder()
                                    .documentId(doc.getId())
                                    .employeeCertificationId(employeeCertification.getId())
                                    .status("ACTIVE")
                                    .createdBy("SYSTEM")
                                    .build()
                    );
                }

            } else if ("RESUME".equalsIgnoreCase(documentType)) {

                // ⚡ OCR TRIGGER
                String parsedText = ocrService.extractText(fileBytes);

                //  SKILL EXTRACTION
                List<String> skills = skillParserService.extractSkills(parsedText);

                List<EmployeeDocumentSkillMapping> mappings = skills.stream()
                        .map(skill -> EmployeeDocumentSkillMapping.builder()
                                .employeeId(employeeId)
                                .documentId(doc.getId())
                                .employeeSkillId(employeeId)
                                .build())
                        .toList();

                employeeDocumentSkillMappingRepository.saveAll(mappings);
            }


            ActionType actionType = existingDocOpt.isPresent()
                    ? ActionType.REPLACE_FILE
                    : ActionType.UPLOAD;


            Map<String, String> diffMap = generateOldNewDiff(oldValueJson, newValueJson);

            String oldDiffJson = diffMap.get("old");
            String newDiffJson = diffMap.get("new");

            EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                    .documentId(doc.getId())
                    .employeeId(employeeId)
                    .actionType(actionType) //
                    .fileName(doc.getFileName())
                    .fileUrl(doc.getFileUrl())
                    .remarks(existingDocOpt.isPresent() ? "Version updated" : "Initial upload")
//                    .actionType(ActionType.UPLOAD) //
                    .accessType(AccessType.WRITE)
                    .performedBy("SYSTEM")
                    .status("SUCCESS")
                    .oldValue(oldDiffJson)
                    .newValue(newDiffJson)
                    .performedAt(LocalDateTime.now())
                    .ipAddress(request.getRemoteAddr())
                    .userAgent(request.getHeader("User-Agent"))
                    .build();

            documentAuditRepository.save(audit);

            return DocumentResponse.builder()
                    .documentId(doc.getDocumentId())
                    .employeeId(employeeId)
                    .documentType(documentType)
                    .category(category)
                    .title(title)
                    .fileUrl(doc.getFileUrl())
                    .fileName(doc.getFileName())
                    .fileSize(file.getSize())
                    .version(version)
                    .issueDate(issueDate)
//                    .isPrimary(true)
                    .isPrimary(doc.getIsPrimary())
                    .expiryDate(doc.getExpiryDate())
                    .tags(tags)
                    .status("ACTIVE")
                    .verificationStatus("PENDING")
                    .uploadedAt(doc.getUploadedAt())
                    .updatedAt(LocalDateTime.now())
                    .build();

        } catch (IOException ex) {

            throw new DocumentUploadException(
                    "Unable to store document file");
        }

    }

    @Override
    public PageResponse<DocumentResponse> getAllDocuments(
            Long employeeId,
            String documentType,
            String category,
            String verificationStatus,
            Boolean isDeleted,
            Boolean isPrimary,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            Boolean expired,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        // ================= EMPLOYEE VALIDATION =================

        employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found with id: " + employeeId));

        // ================= PAGINATION VALIDATION =================

        if (page < 0) {
            throw new InvalidRequestException(
                    "Page number cannot be negative");
        }

        if (size <= 0 || size > 100) {
            throw new InvalidRequestException(
                    "Page size must be between 1 and 100");
        }

        // ================= DATE VALIDATION =================

        if (expiryBefore != null &&
                expiryAfter != null &&
                expiryBefore.isBefore(expiryAfter)) {

            throw new InvalidRequestException(
                    "expiryBefore cannot be earlier than expiryAfter");
        }

        // ================= SORT DIRECTION VALIDATION =================

        if (!List.of("ASC", "DESC")
                .contains(sortDir.toUpperCase())) {

            throw new InvalidRequestException(
                    "Invalid sort direction. Allowed values: ASC or DESC");
        }

        // ================= SORT FIELD VALIDATION =================

        Set<String> allowedSortFields = Set.of(
                "uploadedAt",
                "updatedAt",
                "title",
                "documentType",
                "documentCategory",
                "expiryDate",
                "version",
                "status"
        );

        if (!allowedSortFields.contains(sortBy)) {

            throw new InvalidRequestException(
                    "Invalid sort field: " + sortBy);
        }

        // ================= VERIFICATION STATUS VALIDATION =================

        if (verificationStatus != null &&
                !List.of("PENDING", "VERIFIED", "REJECTED")
                        .contains(verificationStatus.toUpperCase())) {

            throw new InvalidRequestException(
                    "Invalid verification status");
        }

        // ================= SORT =================

        Sort sort = Sort.by(
                Sort.Direction.valueOf(sortDir.toUpperCase()),
                sortBy
        );

        Pageable pageable = PageRequest.of(page, size, sort);

        // ================= SPECIFICATION =================

        Specification<EmployeeDocument> spec =
                buildSpecification(
                        employeeId,
                        documentType,
                        category,
                        verificationStatus,
                        isDeleted,
                        isPrimary,
                        expiryBefore,
                        expiryAfter,
                        expired,
                        search,
                        tags
                );

        Page<EmployeeDocument> result =
                employeeDocumentRepository.findAll(spec, pageable);

        // Optional Business Rule
        if (result.isEmpty()) {
            throw new NoDataFoundException(
                    "No documents found for employee id: "
                            + employeeId);
        }

        List<DocumentResponse> content =
                result.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return PageResponse.<DocumentResponse>builder()
                .content(content)
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .first(result.isFirst())
                .last(result.isLast())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .sortBy(sortBy)
                .direction(sortDir)
                .build();
    }

    @Override
    public PageResponse<DocumentResponse> getDocuments(
            Long employeeId,
            String type,
            String status,
            String verificationStatus,
            String category,
            Boolean isDeleted,
            Boolean isPrimary,
            String search,
            List<String> tags,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {

        // ================= EMPLOYEE VALIDATION =================

        if (employeeId != null) {

            employeeRepository.findById(employeeId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Employee not found with id: " + employeeId));
        }

        // ================= PAGINATION VALIDATION =================

        if (page < 0) {
            throw new InvalidRequestException(
                    "Page number cannot be negative");
        }

        if (size < 1 || size > 100) {
            throw new InvalidRequestException(
                    "Page size must be between 1 and 100");
        }

        // ================= SORT VALIDATION =================

        Set<String> allowedSortFields = Set.of(
                "uploadedAt",
                "updatedAt",
                "fileName",
                "documentType",
                "documentCategory",
                "expiryDate",
                "version",
                "status"
        );

        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "uploadedAt";
        }

        if (!allowedSortFields.contains(sortBy)) {
            throw new InvalidRequestException(
                    "Invalid sort field: " + sortBy);
        }

        if (!List.of("ASC", "DESC")
                .contains(sortDir.toUpperCase())) {

            throw new InvalidRequestException(
                    "Invalid sort direction. Allowed values: ASC or DESC");
        }

        // ================= DOCUMENT TYPE VALIDATION =================

        if (type != null && !type.isBlank()) {

            try {
                DocumentType.valueOf(type.toUpperCase());
            } catch (Exception ex) {

                throw new InvalidRequestException(
                        "Invalid document type: " + type);
            }
        }

        // ================= STATUS VALIDATION =================

        if (status != null &&
                !List.of("ACTIVE", "INACTIVE", "ARCHIVED")
                        .contains(status.toUpperCase())) {

            throw new InvalidRequestException(
                    "Invalid document status");
        }

        // ================= VERIFICATION STATUS =================

        if (verificationStatus != null &&
                !List.of("PENDING", "VERIFIED", "REJECTED")
                        .contains(verificationStatus.toUpperCase())) {

            throw new InvalidRequestException(
                    "Invalid verification status");
        }

        // ================= SORT =================

        Sort sort = Sort.by(
                Sort.Direction.valueOf(sortDir.toUpperCase()),
                sortBy
        );

        Pageable pageable =
                PageRequest.of(page, size, sort);

        // ================= QUERY =================

        Specification<EmployeeDocument> spec =
                buildSpecification(
                        employeeId,
                        type,
                        status,
                        verificationStatus,
                        category,
                        isDeleted,
                        isPrimary,
                        search,
                        tags
                );

        Page<EmployeeDocument> result =
                employeeDocumentRepository.findAll(spec, pageable);

        return PageResponse.<DocumentResponse>builder()
                .content(
                        result.getContent()
                                .stream()
                                .map(this::mapToResponse)
                                .toList()
                )
                .page(result.getNumber())
                .size(result.getSize())
                .totalElements(result.getTotalElements())
                .totalPages(result.getTotalPages())
                .numberOfElements(result.getNumberOfElements())
                .first(result.isFirst())
                .last(result.isLast())
                .hasNext(result.hasNext())
                .hasPrevious(result.hasPrevious())
                .sortBy(sortBy)
                .direction(sortDir)
                .build();
    }

    @Override
    public DocumentResponse getDocumentById(String documentId) {

        EmployeeDocument document = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found with id: " + documentId));

        return mapToResponse(document);
    }

    @Override
    @Transactional
    public DocumentResponse updateDocumentMetadata(
            Long employeeId,
            String documentId,
            UpdateDocumentRequest request) {

        EmployeeDocument doc = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found: " + documentId)
                );

        // Security Check
        if (!doc.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized access to document");
        }

        //  Soft delete check
        if (Boolean.TRUE.equals(doc.getIsDeleted())) {
            throw new RuntimeException("Cannot update deleted document");
        }

        //  Update only non-null fields (PATCH-like behavior)
        if (request.getTitle() != null)
            doc.setTitle(request.getTitle());

        if (request.getDescription() != null)
            doc.setDescription(request.getDescription());

        if (request.getDocumentNumber() != null)
            doc.setDocumentNumber(request.getDocumentNumber());

        if (request.getIssueDate() != null)
            doc.setIssueDate(request.getIssueDate());

        if (request.getExpiryDate() != null)
            doc.setExpiryDate(request.getExpiryDate());

        //  IMPORTANT BUSINESS LOGIC
        if (Boolean.TRUE.equals(request.getIsPrimary())) {

            // remove previous primary
            employeeDocumentRepository.clearPrimaryForEmployee(employeeId);

            doc.setIsPrimary(true);
        }

        doc.setUpdatedAt(LocalDateTime.now());

        EmployeeDocument saved = employeeDocumentRepository.save(doc);



        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DeleteDocumentResponse deleteDocument(
            Long employeeId,
            String documentId,
            HttpServletRequest request) {

        EmployeeDocument doc = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Document not found with id: " + documentId));

        // Ownership Validation
        if (!doc.getEmployeeId().equals(employeeId)) {

            throw new AccessDeniedException(
                    "Document does not belong to employee id: "
                            + employeeId);
        }

        // Already Deleted Validation
        if (Boolean.TRUE.equals(doc.getIsDeleted())) {

            throw new InvalidRequestException(
                    "Document has already been deleted");
        }

        String oldValue = convertToJson(doc);

        doc.setIsDeleted(true);
        doc.setDeletedAt(LocalDateTime.now());
        doc.setDeletedBy("SYSTEM");
        doc.setUpdatedAt(LocalDateTime.now());

        // Remove Primary Flag
        if (Boolean.TRUE.equals(doc.getIsPrimary())) {
            doc.setIsPrimary(false);
        }

        EmployeeDocument saved =
                employeeDocumentRepository.save(doc);

        String newValue = convertToJson(saved);

        EmployeeDocumentAudit audit =
                EmployeeDocumentAudit.builder()
                        .documentId(saved.getId())
                        .employeeId(saved.getEmployeeId())
                        .actionType(ActionType.DELETE)
                        .accessType(AccessType.WRITE)
                        .fileName(saved.getFileName())
                        .fileUrl(saved.getFileUrl())
                        .remarks("Document soft deleted")
                        .performedBy("SYSTEM")
                        .performedAt(LocalDateTime.now())
                        .ipAddress(request.getRemoteAddr())
                        .userAgent(request.getHeader("User-Agent"))
                        .status("SUCCESS")
                        .oldValue(oldValue)
                        .newValue(newValue)
                        .build();

        documentAuditRepository.save(audit);

        return DeleteDocumentResponse.builder()
                .documentId(saved.getDocumentId())
                .deleted(true)
                .deletedAt(saved.getDeletedAt())
                .deletedBy(saved.getDeletedBy())
                .build();
    }

    private String toJson(EmployeeDocument doc) {
        try {
            return new ObjectMapper().writeValueAsString(doc);
        } catch (Exception e) {
            return "{}";
        }
    }

    @Override
    @Transactional
    public RestoreDocumentResponse restoreDocument(
            Long employeeId,
            String documentId,
            HttpServletRequest request) {

        EmployeeDocument doc = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found: " + documentId)
                );

        // Ownership validation
        if (!doc.getEmployeeId().equals(employeeId)) {
            throw new RuntimeException("Unauthorized access");
        }

        //  If not deleted
        if (!Boolean.TRUE.equals(doc.getIsDeleted())) {
            throw new RuntimeException("Document is not deleted");
        }

        // 🔹 Take snapshot BEFORE restore
        String oldValue = convertToJson(doc);

        //  Restore logic
        doc.setIsDeleted(false);
        doc.setDeletedAt(null);
        doc.setDeletedBy(null);
        doc.setUpdatedAt(LocalDateTime.now());

        employeeDocumentRepository.save(doc);

        //  AFTER restore
        String newValue = convertToJson(doc);


        String user = "SYSTEM"; // replace with logged-in user

        // Save audit
        EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                .documentId(doc.getId())
                .employeeId(doc.getEmployeeId())
                .actionType(ActionType.RESTORE)
                .actionType(ActionType.UPDATE_METADATA)
                .accessType(AccessType.WRITE)
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .remarks("Document restored")
                .performedBy(user)
                .performedAt(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .status(doc.getStatus())
                .oldValue(oldValue)
                .status("SUCCESS")
                .newValue(newValue)
                .build();

        documentAuditRepository.save(audit);

        //  Response
        return RestoreDocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .restored(true)
                .restoredAt(LocalDateTime.now())
                .restoredBy(user)
                .build();
    }

    @Override
    public DocumentResponse restoreVersion(Long employeeId, String documentId, Integer version, HttpServletRequest request) {

        // 1. Fetch document
       EmployeeDocument document = employeeDocumentRepository.findByDocumentId(documentId).orElseThrow(() -> new RuntimeException("Document not found"));
        // 2. Ownership check
        if (!document.getEmployeeId().equals(employeeId)){
            throw new RuntimeException("Unauthorized access");
        }
        //fetch requested version
        DocumentVersionHistory oldVersion = documentVersionRepository.findByDocumentIdAndVersion(documentId, version)
                .orElseThrow(()-> new RuntimeException("Version not found"));

        String oldValueJson = toJson(document);

        // 5. Backup CURRENT version into history (VERY IMPORTANT)
        DocumentVersionHistory backup = DocumentVersionHistory.builder()
                .documentRefId(document.getId())
                .documentId(document.getDocumentId())
                .version(document.getVersion())
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .storageKey(document.getFileUrl())
                .versionComment("Backup before restore")
                .uploadedBy("SYSTEM")
                .uploadedAt(LocalDateTime.now())
                .build();

        documentVersionRepository.save(backup);

        //restore old version
        document.setFileName(oldVersion.getFileName());
        document.setFileUrl(oldVersion.getStorageKey());
        document.setFileSize(oldVersion.getFileSize());

       /* // increment version//do not use old version*/
        document.setVersion(document.getVersion() + 1);

        document.setUpdatedAt(LocalDateTime.now());

        employeeDocumentRepository.save(document);

        String newValueJson = convertToJson(document);

        Map<String, String> diffMap = generateOldNewDiff(oldValueJson, newValueJson);
        String oldDiffJson = diffMap.get("old");
        String newDiffJson = diffMap.get("new");

        EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                .documentId(document.getId())
                .employeeId(employeeId)
                .actionType(ActionType.RESTORE)
                .actionType(ActionType.REPLACE_FILE)
                .accessType(AccessType.WRITE)
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .remarks("Restored version: " + version)
                .performedBy("SYSTEM")
                .performedAt(LocalDateTime.now())
                .ipAddress(request.getRemoteAddr())
                .userAgent(request.getHeader("User-Agent"))
                .status("SUCCESS")
                .oldValue(oldDiffJson)
                .newValue(newDiffJson)
                .build();

        documentAuditRepository.save(audit);

        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .employeeId(document.getEmployeeId())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileSize(document.getFileSize())
                .version(document.getVersion())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public DocumentResponse replaceDocument(Long employeeId, String documentId, MultipartFile file, HttpServletRequest request) {

        EmployeeDocument document = employeeDocumentRepository.findByDocumentId(documentId)
                .orElseThrow(()-> new RuntimeException("Document not found"));

        if (!document.getEmployeeId().equals(employeeId)){
            throw new RuntimeException("Unauthorized access");
        }

        String oldValueJson = convertToJson(document);
        DocumentVersionHistory versionHistory = DocumentVersionHistory.builder()
                .documentRefId(document.getId()) // FK
                .documentId(document.getDocumentId()) // business ID
                .version(document.getVersion()) // current version
                .fileName(document.getFileName())
                .fileSize(document.getFileSize())
                .storageKey(document.getFileUrl()) // IMPORTANT
                .versionComment("File replaced")
                .uploadedBy("SYSTEM")
                .uploadedAt(LocalDateTime.now())
                .build();

        documentVersionRepository.save(versionHistory);

        // 6. Upload NEW file
        String folder = employeeId + "/" + documentId;

        String newPath;

        try {
            newPath = fileStorageService.uploadFile(file, folder);
        } catch (Exception e) {
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }

        // 6. Update document
        document.setFileName(file.getOriginalFilename());
        document.setFileUrl(newPath);
        document.setFileSize(file.getSize());
        document.setVersion(document.getVersion() + 1);
        document.setUpdatedAt(LocalDateTime.now());

        employeeDocumentRepository.save(document);

        // 7. Capture NEW state
        String newValueJson = convertToJson(document);

        // 8. Diff
        Map<String, String> diffMap = generateOldNewDiff(oldValueJson, newValueJson);
        String oldDiffJson = diffMap.get("old");
        String newDiffJson = diffMap.get("new");
        // 9. Audit log
        EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                .documentId(document.getId())
                .employeeId(employeeId)
                .actionType(ActionType.REPLACE_FILE)
                .actionType(ActionType.REPLACE_FILE) //  matches constraint
                .accessType(AccessType.WRITE) //  matches constraint
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .remarks("File replaced with new version")
                .performedBy("SYSTEM")
                .performedAt(LocalDateTime.now())
                .ipAddress(getClientIp(request))
                .userAgent(getUserAgent(request))
                .status("SUCCESS")
                .oldValue(oldDiffJson)
                .newValue(newDiffJson)
                .build();

        documentAuditRepository.save(audit);

        // 10. Response
        return DocumentResponse.builder()
                .documentId(document.getDocumentId())
                .employeeId(document.getEmployeeId())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileSize(document.getFileSize())
                .version(document.getVersion())
                .updatedAt(document.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public DownloadDocumentResponse downloadDocument(String documentId,
                                                     HttpServletRequest request) {

        // 1. Fetch document
        EmployeeDocument doc = employeeDocumentRepository
                .findByDocumentId(documentId)
                .orElseThrow(() ->
                        new RuntimeException("Document not found"));

        // 2. Deleted check
        if (Boolean.TRUE.equals(doc.getIsDeleted())) {
            throw new RuntimeException("Document has been deleted");
        }

        // 3. Access validation (optional future RBAC)
        validateDocumentAccess(doc);

        // 4. File path
        Path filePath = Paths.get(doc.getFileUrl());

        Resource resource;

        try {
            resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid file path");
        }

        // 5. Increase download count
        doc.setDownloadCount(
                doc.getDownloadCount() == null
                        ? 1
                        : doc.getDownloadCount() + 1
        );

        employeeDocumentRepository.save(doc);

        // 6. Insert access log
        DocumentAccessLog log = DocumentAccessLog.builder()
                .documentId(doc.getDocumentId())
                .documentRefId(doc.getId())
                .accessType("DOWNLOAD")
                .accessedBy("SYSTEM")
                .accessedAt(LocalDateTime.now())
                .ipAddress(getClientIp(request))
                .userAgent(getUserAgent(request))
                .build();

        documentAccessLogRepository.save(log);

        // 7. Audit log
        EmployeeDocumentAudit audit = EmployeeDocumentAudit.builder()
                .documentId(doc.getId())
                .employeeId(doc.getEmployeeId())
                .actionType(ActionType.DOWNLOAD)
                .actionType(ActionType.DOWNLOAD)
                .accessType(AccessType.READ)
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .remarks("Document downloaded")
                .performedBy("SYSTEM")
                .performedAt(LocalDateTime.now())
                .ipAddress(getClientIp(request))
                .userAgent(getUserAgent(request))
                .status("SUCCESS")
                .build();

        documentAuditRepository.save(audit);

        // 8. Response
        return DownloadDocumentResponse.builder()
                .resource(resource)
                .fileName(doc.getFileName())
                .contentType(doc.getFileType())
                .build();
    }

    @Override
    public DownloadDocumentResponse previewDocument(
            String documentId,
            HttpServletRequest request) {

        EmployeeDocument document =
                employeeDocumentRepository.findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        Resource resource;

        try {
            Path path = Paths.get(document.getFileUrl());

            resource = new UrlResource(path.toUri());

            if (!resource.exists()) {
                throw new RuntimeException("File not found");
            }

        } catch (Exception e) {
            throw new RuntimeException("Unable to preview file");
        }

        return DownloadDocumentResponse.builder()
                .fileName(document.getFileName())
                .contentType(document.getFileType())
                .resource(resource)
                .build();
    }

    @Override
    @Transactional
    public DocumentResponse uploadNewVersion(
            String documentId,
            MultipartFile file,
            HttpServletRequest request) {

        EmployeeDocument document =
                employeeDocumentRepository
                        .findByDocumentId(documentId)
                        .orElseThrow(() ->
                                new RuntimeException("Document not found"));

        int nextVersion = document.getVersion() + 1;

        String folder =
                document.getEmployeeId() +
                        "/" +
                        documentId;

        String uploadedPath;

        try {
            uploadedPath =
                    fileStorageService.uploadFile(
                            file,
                            folder
                    );
        } catch (Exception e) {
            throw new RuntimeException(
                    "File upload failed"
            );
        }

        document.setVersion(nextVersion);
        document.setFileName(
                file.getOriginalFilename()
        );
        document.setFileUrl(uploadedPath);
        document.setFileSize(file.getSize());
        document.setUpdatedAt(
                LocalDateTime.now()
        );

        employeeDocumentRepository.save(document);

        DocumentVersionHistory history =
                DocumentVersionHistory.builder()
                        .documentRefId(document.getId())
                        .documentId(document.getDocumentId())
                        .version(nextVersion)
                        .fileName(file.getOriginalFilename())
                        .fileSize(file.getSize())
                        .storageKey(uploadedPath)
                        .versionComment(
                                "New version uploaded"
                        )
                        .uploadedBy("SYSTEM")
                        .uploadedAt(
                                LocalDateTime.now()
                        )
                        .build();

        documentVersionRepository.save(history);

        EmployeeDocumentAudit audit =
                EmployeeDocumentAudit.builder()
                        .documentId(document.getId())
                        .employeeId(document.getEmployeeId())
                        .actionType(
                                ActionType.REPLACE_FILE
                        )
                        .accessType(
                                AccessType.WRITE
                        )
                        .remarks(
                                "New version uploaded"
                        )
                        .performedBy("SYSTEM")
                        .performedAt(
                                LocalDateTime.now()
                        )
                        .status("SUCCESS")
                        .ipAddress(
                                getClientIp(request)
                        )
                        .userAgent(
                                getUserAgent(request)
                        )
                        .build();

        documentAuditRepository.save(audit);

        return DocumentResponse.builder()
                .documentId(
                        document.getDocumentId()
                )
                .employeeId(
                        document.getEmployeeId()
                )
                .fileName(
                        document.getFileName()
                )
                .fileUrl(
                        document.getFileUrl()
                )
                .version(
                        document.getVersion()
                )
                .build();
    }

    @Override
    public List<DocumentVersionResponse> getVersions(
            String documentId) {

        return documentVersionRepository
                .findByDocumentIdOrderByVersionDesc(
                        documentId)
                .stream()
                .map(v ->
                        DocumentVersionResponse
                                .builder()
                                .id(v.getId())
                                .documentId(v.getDocumentId())
                                .version(v.getVersion())
                                .fileName(v.getFileName())
                                .fileSize(v.getFileSize())
                                .storageKey(v.getStorageKey())
                                .versionComment(
                                        v.getVersionComment())
                                .uploadedBy(
                                        v.getUploadedBy())
                                .uploadedAt(
                                        v.getUploadedAt())
                                .build())
                .toList();
    }

    @Override
    public DocumentVersionResponse getVersion(
            String documentId,
            Integer version) {

        DocumentVersionHistory history =
                documentVersionRepository
                        .findByDocumentIdAndVersion(
                                documentId,
                                version)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Version not found"));

        return DocumentVersionResponse.builder()
                .id(history.getId())
                .documentId(
                        history.getDocumentId())
                .version(
                        history.getVersion())
                .fileName(
                        history.getFileName())
                .fileSize(
                        history.getFileSize())
                .storageKey(
                        history.getStorageKey())
                .versionComment(
                        history.getVersionComment())
                .uploadedBy(
                        history.getUploadedBy())
                .uploadedAt(
                        history.getUploadedAt())
                .build();
    }

    private void validateDocumentAccess(EmployeeDocument doc) {

        // Example future RBAC

        if ("PRIVATE".equalsIgnoreCase(doc.getAccessLevel())) {

            // current user validation
            // manager validation
            // HR validation

        }
    }

    private String convertToJson(EmployeeDocument doc) {
        try {
            return objectMapper.writeValueAsString(doc);
        } catch (Exception e) {
            throw new RuntimeException("JSON conversion error");
        }
    }

    private Specification<EmployeeDocument> buildSpecification(
            Long employeeId,
            String type,
            String status,
            String verificationStatus,
            String category,
            Boolean isDeleted,
            Boolean isPrimary,
            String search,
            List<String> tags
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Employee filter
            if (employeeId != null) {
                predicates.add(cb.equal(root.get("employeeId"), employeeId));
            }

            // Type
            if (type != null) {
                predicates.add(cb.equal(root.get("documentType"), type));
            }

            // Category
            if (category != null) {
                predicates.add(cb.equal(root.get("documentCategory"), category));
            }

            // Status
            if (status != null) {
                predicates.add(cb.equal(cb.lower(root.get("status")), status.toLowerCase()));
            }

            // Verification status
            if (verificationStatus != null) {
                predicates.add(cb.equal(root.get("verificationStatus"), verificationStatus));
            }

            // Primary
            if (Boolean.TRUE.equals(isPrimary)) {
                predicates.add(cb.isTrue(root.get("isPrimary")));
            }

            // Deleted
            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("isDeleted"), isDeleted));
            }

            // Search
            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("fileName")), like)
                ));
            }

            // Tags
            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tag : tags) {
                    tagPredicates.add(cb.like(
                            cb.function("array_to_string", String.class,
                                    root.get("tags"),
                                    cb.literal(",")
                            ),
                            "%" + tag.toLowerCase() + "%"
                    ));
                }

                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Specification<EmployeeDocument> buildSpecification(
            Long employeeId,
            String documentType,
            String category,
            String verificationStatus,
            Boolean isDeleted,
            Boolean isPrimary,
            LocalDate expiryBefore,
            LocalDate expiryAfter,
            Boolean expired,
            String search,
            List<String> tags
    ) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();


            predicates.add(cb.equal(root.get("employeeId"), employeeId));

            predicates.add(cb.or(
                    cb.isNull(root.get("status")),
                    cb.equal(cb.lower(cb.trim(root.get("status"))), "active")
            ));

            if (documentType != null)
                predicates.add(cb.equal(root.get("documentType"), documentType));

            if (category != null)
                predicates.add(cb.equal(root.get("documentCategory"), category));

            if (verificationStatus != null)
                predicates.add(cb.equal(root.get("verificationStatus"), verificationStatus));

            if (Boolean.TRUE.equals(isPrimary))
                predicates.add(cb.isTrue(root.get("isPrimary")));

            if (isDeleted != null) {
                predicates.add(cb.equal(root.get("isDeleted"), isDeleted));
            }

            if (expiryBefore != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("expiryDate"), expiryBefore));

            if (expiryAfter != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("expiryDate"), expiryAfter));

            if (Boolean.TRUE.equals(expired))
                predicates.add(cb.lessThan(root.get("expiryDate"), LocalDate.now()));

            if (search != null && !search.isBlank()) {
                String like = "%" + search.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), like),
                        cb.like(cb.lower(root.get("fileName")), like)
                ));
            }

            if (tags != null && !tags.isEmpty()) {
                List<Predicate> tagPredicates = new ArrayList<>();

                for (String tag : tags) {
                    tagPredicates.add(cb.like(
                            cb.function("array_to_string", String.class,
                                    root.get("tags"),
                                    cb.literal(",")
                            ),
                            "%" + tag.toLowerCase() + "%"
                    ));
                }

                predicates.add(cb.or(tagPredicates.toArray(new Predicate[0])));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
    private DocumentResponse mapToResponse(EmployeeDocument doc) {
        return DocumentResponse.builder()
                .documentId(doc.getDocumentId())
                .employeeId(doc.getEmployeeId())
                .documentType(doc.getDocumentType())
                .category(doc.getDocumentCategory())
                .title(doc.getTitle())
                .fileName(doc.getFileName())
                .fileUrl(doc.getFileUrl())
                .fileSize(doc.getFileSize())
                .version(doc.getVersion())
                .status(doc.getStatus())
                .verificationStatus(doc.getVerificationStatus())
                .issueDate(doc.getIssueDate())
                .expiryDate(doc.getExpiryDate())
                .isPrimary(doc.getIsPrimary())
                .tags(doc.getTags() != null ? Arrays.asList(doc.getTags()) : null)
                .uploadedAt(doc.getUploadedAt())
                .updatedAt(doc.getUpdatedAt())
                .build();
    }


    private Map<String, String> generateOldNewDiff(String oldJson, String newJson) {
        try {
            if (oldJson == null || newJson == null) return Map.of();

            Map<String, Object> oldMap = objectMapper.readValue(oldJson, Map.class);
            Map<String, Object> newMap = objectMapper.readValue(newJson, Map.class);

            Map<String, Object> oldDiff = new HashMap<>();
            Map<String, Object> newDiff = new HashMap<>();

            for (String key : newMap.keySet()) {
                Object oldVal = oldMap.get(key);
                Object newVal = newMap.get(key);

                if (!Objects.equals(oldVal, newVal)) {
                    oldDiff.put(key, oldVal);
                    newDiff.put(key, newVal);
                }
            }

            return Map.of(
                    "old", objectMapper.writeValueAsString(oldDiff),
                    "new", objectMapper.writeValueAsString(newDiff)
            );

        } catch (Exception e) {
            throw new RuntimeException("Diff generation failed", e);
        }
    }
    private String getClientIp(HttpServletRequest request) {

        String xfHeader = request.getHeader("X-Forwarded-For");

        if (xfHeader != null && !xfHeader.isEmpty()
                && !"unknown".equalsIgnoreCase(xfHeader)) {

            return xfHeader.split(",")[0];
        }

        String ip = request.getRemoteAddr();

        // Convert localhost IPv6 to IPv4
        if ("0:0:0:0:0:0:0:1".equals(ip)) {

            try {
                ip = java.net.InetAddress
                        .getLocalHost()
                        .getHostAddress();
            } catch (Exception e) {
                ip = "127.0.0.1";
            }
        }

        return ip;
    }
    private String getUserAgent(HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");

        if (userAgent == null || userAgent.isBlank()) {
            return "UNKNOWN";
        }

        return userAgent;
    }
}
