package main.java.com.jana.controller;

import com.google.gson.Gson;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.dtos.usuario.UsuarioUpdateDTO;
import com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import com.jana.model.enums.Perfil;
import com.jana.service.UsuarioService;
import com.jana.utils.TokenUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsuarioController extends HttpServlet {
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // 1. Identifica quem está fazendo a requisição
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido"));
                return;
            }

            String listarTodos = req.getParameter("all");

            if (listarTodos != null && listarTodos.equals("true")) {
                UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

                if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                    resp.getWriter().write(gson.toJson("Acesso negado: Apenas administradores podem listar todos os usuários."));
                    return;
                }

                String jsonTodos = gson.toJson(usuarioService.getAllUsuarios());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(jsonTodos);
            } else {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(usuarioService.getUsuario(userId)));
            }

        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }

            UsuarioUpdateDTO dto = gson.fromJson(req.getReader(), UsuarioUpdateDTO.class);

            usuarioService.updateUsuario(userId, dto);

            resp.setContentType("application/json");
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
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }
            usuarioService.deleteUsuario(userId);

            resp.setContentType("application/json");
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