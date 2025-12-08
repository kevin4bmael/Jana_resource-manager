package com.jana.security;

import com.google.gson.Gson;
import com.jana.utils.TokenUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@WebFilter("/*")
public class SecurityFilter implements Filter {

    private final TokenService tokenService = new TokenService();

    private final Gson gson = new Gson();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        String path = request.getRequestURI();


        if (path.contains("/auth/login") || path.contains("/auth/register")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        String token = TokenUtils.extrairToken(request);

        if (token != null) {
            try {
                String email = tokenService.verifyAndExtractToken(token);
                request.setAttribute("userEmail", email);
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {

                enviarErroJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado");
            }
        } else {
            enviarErroJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente");
        }
    }

    @Override
    public void destroy() {}

    private void enviarErroJson(HttpServletResponse response, int status, String mensagem) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String json = gson.toJson(Collections.singletonMap("erro", mensagem));

        response.getWriter().write(json);
    }
}