package com.jana.controller; // Ajustado o package

import com.google.gson.Gson;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.dtos.usuario.UsuarioUpdateDTO;
import com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import com.jana.model.enums.Perfil;
import com.jana.service.UsuarioService;
import com.jana.utils.TokenUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@WebServlet("/usuarios/*")
public class UsuarioController extends HttpServlet {


    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }

            String listarTodos = req.getParameter("all");

            if (listarTodos != null && listarTodos.equals("true")) {

                UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

                if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                    sendJson(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso negado: Apenas administradores.");
                    return;
                }

                sendJson(resp, HttpServletResponse.SC_OK, usuarioService.getAllUsuarios());
            } else {

                sendJson(resp, HttpServletResponse.SC_OK, usuarioService.getUsuario(userId));
            }

        } catch (UsuarioNaoEncontradoException e) {
            sendJson(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Bom para ver o erro no console do servidor
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro no servidor: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }


            UsuarioUpdateDTO dto = gson.fromJson(req.getReader(), UsuarioUpdateDTO.class);


            usuarioService.updateUsuario(userId, dto);


            sendJson(resp, HttpServletResponse.SC_OK, Map.of("mensagem", "Usuário atualizado com sucesso"));

        } catch (UsuarioNaoEncontradoException e) {
            sendJson(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro no servidor: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                sendJson(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }


            usuarioService.deleteUsuario(userId);

            sendJson(resp, HttpServletResponse.SC_OK, Map.of("mensagem", "Usuário deletado com sucesso"));

        } catch (UsuarioNaoEncontradoException e) {
            sendJson(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            sendJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro no servidor: " + e.getMessage());
        }
    }

    // Método auxiliar para padronizar todas as respostas JSON
    private void sendJson(HttpServletResponse resp, int statusCode, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(statusCode);


        String jsonOutput = gson.toJson(data);
        resp.getWriter().write(jsonOutput);
    }
}