package main.java.com.jana.security;

import com.google.gson.Gson;
import main.java.com.jana.dtos.usuario.UsuarioLoginDTO;
import main.java.com.jana.dtos.usuario.UsuarioRegisterDTO;
import main.java.com.jana.exceptions.usuario.EmailJaExisteException;
import main.java.com.jana.model.Usuario;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class AuthController extends HttpServlet {
    private final AuthService authService = new AuthService();
    private final Gson gson = new Gson();
    private final TokenService tokenService = new TokenService();
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getPathInfo();

        try {
            if ("register".equals(path)) {
                handleRegister(req, resp);
            } else if ("login".equals(path)) {
                handleLogin(req, resp);
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("URL inválida!");
            }
        } catch (EmailJaExisteException e) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            resp.getWriter().write(e.getMessage());
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("Erro no servidor");
        }
    }

    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException, SQLException {
        UsuarioRegisterDTO dto = gson.fromJson(request.getReader(), UsuarioRegisterDTO.class);
        authService.register(dto);
        response.setStatus(HttpServletResponse.SC_CREATED);
        response.getWriter().write("Usuário registrado com sucesso");
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
