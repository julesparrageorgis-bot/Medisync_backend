package ma.medisync.medisync_backend.service;

import lombok.RequiredArgsConstructor;
import ma.medisync.medisync_backend.entity.AuditLog;
import ma.medisync.medisync_backend.repository.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLog logAction(AuditLog auditLog) {
        auditLog.setTimestamp(LocalDateTime.now());
        return auditLogRepository.save(auditLog);
    }

    public Optional<AuditLog> getLogById(Long id) {
        return auditLogRepository.findById(id);
    }

    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAll();
    }

    public List<AuditLog> getUserLogs(Long userId) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null && log.getUser().getId().equals(userId))
                .toList();
    }

    public List<AuditLog> getLogsByAction(String action) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getAction().equals(action))
                .toList();
    }

    public List<AuditLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findAll().stream()
                .filter(log -> !log.getTimestamp().isBefore(startDate) && 
                        !log.getTimestamp().isAfter(endDate))
                .toList();
    }

    public List<AuditLog> getLogsByModule(String entityType) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getEntityType() != null && log.getEntityType().equals(entityType))
                .toList();
    }

    public List<AuditLog> getSensitiveActionLogs() {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getAction().contains("DELETE") || 
                        log.getAction().contains("UPDATE") ||
                        log.getAction().contains("ACCESS"))
                .toList();
    }

    public List<AuditLog> getFailedActions() {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getStatus() != null && log.getStatus().equals("FAILED"))
                .toList();
    }

    public List<AuditLog> getUserActionsByDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findAll().stream()
                .filter(log -> log.getUser() != null && log.getUser().getId().equals(userId) &&
                        !log.getTimestamp().isBefore(startDate) && 
                        !log.getTimestamp().isAfter(endDate))
                .toList();
    }

    public AuditLog updateLog(Long id, AuditLog logDetails) {
        Optional<AuditLog> log = auditLogRepository.findById(id);
        if (log.isPresent()) {
            AuditLog l = log.get();
            l.setAction(logDetails.getAction());
            l.setEntityType(logDetails.getEntityType());
            l.setStatus(logDetails.getStatus());
            l.setDescription(logDetails.getDescription());
            return auditLogRepository.save(l);
        }
        return null;
    }

    public boolean deleteLog(Long id) {
        if (auditLogRepository.existsById(id)) {
            auditLogRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long getTotalLogsCount() {
        return auditLogRepository.count();
    }

    public long getUserLogsCount(Long userId) {
        return getUserLogs(userId).size();
    }
}
