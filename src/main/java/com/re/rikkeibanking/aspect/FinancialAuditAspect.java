package com.re.rikkeibanking.aspect;

import com.re.rikkeibanking.entity.AuditLog;
import com.re.rikkeibanking.repository.AuditRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Component
@RequiredArgsConstructor
public class FinancialAuditAspect {

    private final AuditRepository auditRepository;

    @Around("@annotation(logAudit)")
    public Object logFinancialAction(ProceedingJoinPoint joinPoint, LogAudit logAudit) throws Throwable {
        long start = System.currentTimeMillis();
        String method = joinPoint.getSignature().toShortString();
        try {
            Object result = joinPoint.proceed();
            saveAudit(
                    logAudit.value(),
                    "SUCCESS",
                    buildSuccessMessage(method, joinPoint.getArgs(), result, System.currentTimeMillis() - start)
            );
            return result;
        } catch (Throwable throwable) {
            saveAudit(
                    logAudit.value(),
                    "FAILED",
                    buildFailedMessage(method, joinPoint.getArgs(), throwable, System.currentTimeMillis() - start)
            );
            throw throwable;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveAudit(String action, String status, String message) {
        AuditLog auditLog = new AuditLog();
        auditLog.setAction(action);
        auditLog.setActor(currentActor());
        auditLog.setStatus(status);
        auditLog.setMessage(truncate(message, 500));
        auditRepository.save(auditLog);
    }

    private String buildSuccessMessage(String method, Object[] args, Object result, long durationMs) {
        return "method=" + method
                + "; durationMs=" + durationMs
                + "; request=" + serializeArgs(args)
                + "; response=" + serializeValue(result);
    }

    private String buildFailedMessage(String method, Object[] args, Throwable throwable, long durationMs) {
        return "method=" + method
                + "; durationMs=" + durationMs
                + "; request=" + serializeArgs(args)
                + "; error=" + throwable.getClass().getSimpleName()
                + ": " + throwable.getMessage();
    }

    private String currentActor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return "system";
        }
        return authentication.getName();
    }

    private String sanitize(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replaceAll("(?i)\"(password|oldPin|newPin|confirmNewPin|transactionPin|pin|token|refreshToken|cardNumber|idNumber)\"\\s*:\\s*\"[^\"]*\"", "\"$1\":\"***\"")
                .replaceAll("(?i)(password|oldPin|newPin|confirmNewPin|transactionPin|pin|token|refreshToken|cardNumber|idNumber)=([^,)};]+)", "$1=***");
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private String serializeArgs(Object[] args) {
        return Arrays.stream(args)
                .filter(arg -> !(arg instanceof Authentication))
                .filter(arg -> !(arg instanceof MultipartFile))
                .map(this::serializeValue)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private String serializeValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (isSimpleValue(value)) {
            return sanitize(String.valueOf(value));
        }
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .map(this::serializeValue)
                    .collect(Collectors.joining(", ", "[", "]"));
        }
        if (value instanceof Map<?, ?> map) {
            return map.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + serializeValue(entry.getValue()))
                    .collect(Collectors.joining(", ", "{", "}"));
        }
        return sanitize(serializeBean(value));
    }

    private boolean isSimpleValue(Object value) {
        return value instanceof String
                || value instanceof Number
                || value instanceof Boolean
                || value instanceof Enum<?>
                || value instanceof BigDecimal
                || value instanceof Temporal;
    }

    private String serializeBean(Object value) {
        Method[] methods = value.getClass().getMethods();
        return Arrays.stream(methods)
                .filter(method -> Modifier.isPublic(method.getModifiers()))
                .filter(method -> method.getParameterCount() == 0)
                .filter(method -> method.getName().startsWith("get") || method.getName().startsWith("is"))
                .filter(method -> !method.getName().equals("getClass"))
                .map(method -> propertyName(method) + "=" + invokeGetter(value, method))
                .collect(Collectors.joining(", ", "{", "}"));
    }

    private String propertyName(Method method) {
        String methodName = method.getName();
        String rawName = methodName.startsWith("get")
                ? methodName.substring(3)
                : methodName.substring(2);
        if (rawName.isEmpty()) {
            return rawName;
        }
        return Character.toLowerCase(rawName.charAt(0)) + rawName.substring(1);
    }

    private String invokeGetter(Object target, Method method) {
        try {
            Object value = method.invoke(target);
            return isSimpleValue(value) || value == null ? String.valueOf(value) : value.getClass().getSimpleName();
        } catch (ReflectiveOperationException exception) {
            return "<unavailable>";
        }
    }
}
