package br.com.arenaxp.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public static final String SESSAO_USUARIO_ID = "USUARIO_LOGADO_ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Libera a verificação prévia de CORS do navegador
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String uri = request.getRequestURI();
        HttpSession session = request.getSession(false);
        boolean autenticado = session != null && session.getAttribute(SESSAO_USUARIO_ID) != null;

        if (uri.equals("/")
                || uri.equals("/index.html")
                || uri.equals("/style.css")
                || uri.equals("/script.js")
                || uri.equals("/login.html")
                || uri.equals("/api/auth/login")
                || uri.equals("/api/auth/registrar")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.equals("/error")) {
            return true;
        }

        if (!autenticado) {
            if (uri.startsWith("/api/")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"erro\":\"Sessão expirada ou usuário não autenticado.\"}");
            } else {
                response.sendRedirect("/login.html");
            }
            return false;
        }

        return true;
    }
}