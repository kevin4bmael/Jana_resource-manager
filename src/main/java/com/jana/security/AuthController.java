package com.jana.security;

import com.google.gson.Gson;
import com.jana.dtos.usuario.UsuarioLoginDTO;
import com.jana.dtos.usuario.UsuarioRegisterDTO;
import com.jana.exceptions.usuario.EmailJaExisteException;
import com.jana.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();
    private final TokenService tokenService = new TokenService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        String path = req.getPathInfo();

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        if (path == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String json = gson.toJson(Collections.singletonMap("erro", "Caminho incompleto"));
            resp.getWriter().write(json);
            return;
        }

        try {
            if ("/register".equals(path)) {
                handleRegister(req, resp);
            } else if ("/login".equals(path)) {
                handleLogin(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                String json = gson.toJson(Collections.singletonMap("erro", "URL inválida! Path recebido: " + path));
                resp.getWriter().write(json);
            }
        } catch (EmailJaExisteException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            String json = gson.toJson(Collections.singletonMap("erro", e.getMessage()));
            resp.getWriter().write(json);
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            String json = gson.toJson(Collections.singletonMap("erro", "Erro interno no servidor"));
            resp.getWriter().write(json);
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UsuarioRegisterDTO dto = gson.fromJson(request.getReader(), UsuarioRegisterDTO.class);
        authService.register(dto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = gson.toJson(Collections.singletonMap("mensagem", "Usuário registrado com sucesso"));
        response.getWriter().write(json);
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UsuarioLoginDTO dto = gson.fromJson(request.getReader(), UsuarioLoginDTO.class);

        Usuario usuarioLogado = authService.login(dto);

        if (usuarioLogado != null) {
            String token = tokenService.generateToken(usuarioLogado);

            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/json");

            response.getWriter().write("{\"token\": \"" + token + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Usuário ou senha inválidos");
        }
    }
}
