package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.RecursoDAO;
import main.java.com.jana.dao.UsuarioDAO; 
import main.java.com.jana.dtos.recurso.RecursoRegisterDTO;
import main.java.com.jana.dtos.recurso.RecursoResponseDTO;
import main.java.com.jana.dtos.recurso.RecursoUpdateDTO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.recurso.RecursoNaoEncontradoException;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.security.TokenService;
import main.java.com.jana.service.RecursoService;
import main.java.com.jana.service.UsuarioService; 

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/recursos/*")

public class RecursoController extends HttpServlet {
    private final RecursoService recursoService = new RecursoService(new RecursoDAO());
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido"));
                return;
            }
            
            TokenService.extractUserIdFromToken(token); 

            Integer id = extrairIdDaUrl(req);

            if (id == null) {
                List<RecursoResponseDTO> recursos = recursoService.getAllRecursos();
                resp.getWriter().write(gson.toJson(recursos));
            } else {
                RecursoResponseDTO recurso = recursoService.getRecurso(id);
                resp.getWriter().write(gson.toJson(recurso));
            }
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (RecursoNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (Exception e) { 
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson("Token inválido ou erro no servidor: " + e.getMessage()));
            e.printStackTrace(); 
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido"));
                return;
            }

            Long userId = TokenService.extractUserIdFromToken(token);
            
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId.intValue()); 
            
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson("Acesso negado. Requer perfil de Administrador."));
                return;
            }
           
            RecursoRegisterDTO dto = gson.fromJson(req.getReader(), RecursoRegisterDTO.class);
            recursoService.createRecurso(dto, userId.intValue());
            
            resp.setStatus(HttpServletResponse.SC_CREATED); 
            resp.getWriter().write(gson.toJson("Recurso cadastrado com sucesso"));

        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson("Usuário do token inválido."));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String token = extrairToken(req);
            if (token == null) { 
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido"));
                return;
            }
            
            Long userId = TokenService.extractUserIdFromToken(token);
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId.intValue()); 
            
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson("Acesso negado. Requer perfil de Administrador."));
                return;
            }

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("ID do recurso ausente na URL. Ex: /recursos/123"));
                return;
            }

            RecursoUpdateDTO dto = gson.fromJson(req.getReader(), RecursoUpdateDTO.class);

            recursoService.updateRecurso(id, dto);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Recurso atualizado com sucesso"));

        } catch (RecursoNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            resp.getWriter().write(gson.toJson("Usuário do token inválido."));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido"));
                return;
            }
            
            Long userId = TokenService.extractUserIdFromToken(token);
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId.intValue()); 
            
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson("Acesso negado. Requer perfil de Administrador."));
                return;
            }
   
            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("ID do recurso ausente na URL. Ex: /recursos/123"));
                return;
            }

            recursoService.deleteRecurso(id);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Recurso deletado com sucesso"));

        } catch (RecursoNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
            resp.getWriter().write(gson.toJson("Usuário do token inválido."));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    private String extrairToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    private Integer extrairIdDaUrl(HttpServletRequest req) {
        String pathInfo = req.getPathInfo(); 
        
        if (pathInfo == null || pathInfo.equals("/")) {
            return null; 
        }
        
        try {
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null; 
        }
    }
}