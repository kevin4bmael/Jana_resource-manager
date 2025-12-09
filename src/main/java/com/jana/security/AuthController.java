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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/auth/*")
public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();
    private final TokenService tokenService = new TokenService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String path = req.getPathInfo();

        if (path == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(Map.of("erro", "Caminho incompleto")));
            return;
        }

        try {
            if ("/register".equals(path)) {
                handleRegister(req, resp);
            } else if ("/login".equals(path)) {
                handleLogin(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write(gson.toJson(Map.of("erro", "URL inválida")));
            }
        } catch (EmailJaExisteException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(gson.toJson(Map.of("erro", e.getMessage())));
        } catch (SQLException e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("erro", "Erro interno")));
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(Map.of("erro", "Erro inesperado")));
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UsuarioRegisterDTO dto = gson.fromJson(request.getReader(), UsuarioRegisterDTO.class);
        authService.register(dto);

        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write(gson.toJson(Map.of("mensagem", "Usuário registrado com sucesso")));
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UsuarioLoginDTO dto = gson.fromJson(request.getReader(), UsuarioLoginDTO.class);
        Usuario usuario = authService.login(dto);

        if (usuario == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(gson.toJson(Map.of("erro", "Credenciais inválidas")));
            return;
        }

        String token = tokenService.generateToken(usuario);

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("token", token);
        resposta.put("id", usuario.getUserId());
        resposta.put("nome", usuario.getNome());
        resposta.put("email", usuario.getEmail());
        resposta.put("perfil", usuario.getPerfil().name());

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(gson.toJson(resposta));
    }
}