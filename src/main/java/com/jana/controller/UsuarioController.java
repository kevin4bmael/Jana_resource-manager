package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.usuario.UsuarioUpdateDTO;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.security.TokenService;
import main.java.com.jana.service.UsuarioService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsuarioController extends HttpServlet {
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();


    private String extrairToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }

            Long userId = TokenService.extractUserIdFromToken(token);


            resp.getWriter().write(gson.toJson(usuarioService.getUsuario(userId.intValue())));
            resp.setStatus(HttpServletResponse.SC_OK);

        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro no servidor: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }

            Long userId = TokenService.extractUserIdFromToken(token);
            UsuarioUpdateDTO dto = gson.fromJson(req.getReader(), UsuarioUpdateDTO.class);

            usuarioService.updateUsuario(userId.intValue(), dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Usuário atualizado com sucesso");
        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro no servidor: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }

            Long userId = TokenService.extractUserIdFromToken(token);
            usuarioService.deleteUsuario(userId.intValue());

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("Usuário deletado com sucesso");
        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro no servidor: " + e.getMessage());
        }
    }
}