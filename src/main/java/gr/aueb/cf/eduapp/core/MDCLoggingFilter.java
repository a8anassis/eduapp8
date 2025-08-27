package gr.aueb.cf.eduapp.core;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/*
 * Mapped Diagnostic Context for contextual info inject into loggers.
 */
@Component
public class MDCLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String user = "anonymous";
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) user = auth.getName();

            // --- Client IP ---
            String clientIp = request.getHeader("X-Forwarded-For"); // if proxy is used like Nginx/Caddy
            if (clientIp != null && !clientIp.isEmpty()) {
                clientIp = clientIp.split(",")[0].trim();  // Get original client IP if behind a proxy
            } else {
                clientIp = request.getRemoteAddr();        // Fallback to direct connection
            }
            if ("0:0:0:0:0:0:0:1".equals(clientIp)) {
                clientIp = "127.0.0.1";
            }

            // --- Put values into MDC ---
            MDC.put("user", user);
            MDC.put("ip", clientIp);

            filterChain.doFilter(request, response);
        } finally {
            // Always clear MDC to avoid leaking data between threads
            MDC.clear();
        }
    }
}

