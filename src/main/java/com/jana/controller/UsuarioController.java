package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.usuario.UsuarioUpdateDTO;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.service.UsuarioService;
import main.java.com.jana.utils.TokenUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UsuarioController extends HttpServlet {
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");


            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("Token ausente ou inválido");
                return;
            }

            String listarTodos = req.getParameter("all");

            if (listarTodos != null && listarTodos.equals("true")) {

                String jsonTodos = gson.toJson(usuarioService.getAllUsuarios());
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(jsonTodos);
            } else {

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(usuarioService.getUsuario(userId)));
            }

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