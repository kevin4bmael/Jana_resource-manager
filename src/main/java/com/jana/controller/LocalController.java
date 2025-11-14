package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.LocalDAO;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.local.LocalRegisterDTO;
import main.java.com.jana.dtos.local.LocalResponseDTO;
import main.java.com.jana.dtos.local.LocalUpdateDTO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.LocalNaoEncontradoException;
import main.java.com.jana.exceptions.UsuarioNaoEncontradoException;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.security.TokenService;
import main.java.com.jana.service.LocalService;
import main.java.com.jana.service.UsuarioService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/locais/*") 

public class LocalController extends HttpServlet {
    private final LocalService localService = new LocalService(new LocalDAO());
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
                List<LocalResponseDTO> locais = localService.getAllLocais();
                resp.getWriter().write(gson.toJson(locais));
            } else {
                LocalResponseDTO local = localService.getLocal(id);
                resp.getWriter().write(gson.toJson(local));
            }
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (LocalNaoEncontradoException e) {
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

            LocalRegisterDTO dto = gson.fromJson(req.getReader(), LocalRegisterDTO.class);

            localService.createLocal(dto, userId.intValue());
            
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson("Local cadastrado com sucesso"));

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
                resp.getWriter().write(gson.toJson("ID do local ausente na URL."));
                return;
            }

            LocalUpdateDTO dto = gson.fromJson(req.getReader(), LocalUpdateDTO.class);

            localService.updateLocal(id, dto);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Local atualizado com sucesso"));

        } catch (LocalNaoEncontradoException e) {
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
                resp.getWriter().write(gson.toJson("ID do local ausente na URL."));
                return;
            }

            localService.deleteLocal(id);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Local deletado com sucesso"));

        } catch (LocalNaoEncontradoException e) {
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