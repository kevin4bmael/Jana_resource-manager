package main.java.com.jana.security;


import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityFilter implements Filter {
    private final TokenService tokenService;

    public SecurityFilter() {
        this.tokenService = new TokenService();
    }


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String token = extractToken(request);
        if(token!=null) {
            try {
                String email = tokenService.verifyAndExtractToken(token);
                request.setAttribute("email", email);
                filterChain.doFilter(servletRequest, servletResponse);
            } catch (Exception e) {
                HttpServletResponse resp = (HttpServletResponse) servletResponse;
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token inválido ou ausente");
            }
        }
    }

    @Override
    public void destroy() {

    }


    private String extractToken(HttpServletRequest request){
        String authHeader = request.getHeader("Authorization");
        if(authHeader ==null){
            return null;
        }
        return authHeader.replaceAll("Bearer ", "");
    }
}
