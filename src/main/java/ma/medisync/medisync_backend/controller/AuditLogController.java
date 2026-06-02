package ma.medisync.medisync_backend.controller;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.AuditLog;
import ma.medisync.medisync_backend.service.AuditLogService;
import ma.medisync.medisync_backend.service.ApiResponseMapper;
import ma.medisync.medisync_backend.dto.ApiResponses.AuditLogResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;
    private final ApiResponseMapper mapper;

    @GetMapping
    public List<AuditLogResponse> all() {
        return auditLogService.getAllLogs().stream().map(mapper::auditLog).toList();
    }
}
