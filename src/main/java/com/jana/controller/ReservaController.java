package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.ReservaDAO;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.reserva.ReservaRegisterDTO;
import main.java.com.jana.dtos.reserva.ReservaResponseDTO;
import main.java.com.jana.dtos.reserva.ReservaUpdateDTO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.reserva.ReservaNaoEncontradaException;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.security.TokenService;
import main.java.com.jana.service.ReservaService;
import main.java.com.jana.service.UsuarioService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/reservas/*")
public class ReservaController extends HttpServlet {

    private final ReservaService reservaService = new ReservaService(new ReservaDAO());
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido."));
                return;
            }
            Long userIdToken = TokenService.extractUserIdFromToken(token);
            UsuarioResponseDTO usuarioToken = usuarioService.getUsuario(userIdToken.intValue());

            Integer id = extrairIdDaUrl(req);

            if (id != null) {
                ReservaResponseDTO reserva = reservaService.getReserva(id);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(reserva));

            } else {
                if (usuarioToken.perfil() != Perfil.ADMIN) {
                    List<ReservaResponseDTO> reservas = reservaService.getReservasByUserId(userIdToken.intValue());
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().write(gson.toJson(reservas));
                    return;
                }

                List<ReservaResponseDTO> reservas = reservaService.getAllReservas();
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(reservas));
            }

        } catch (ReservaNaoEncontradaException | UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no banco de dados: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido."));
                return;
            }
            Long userIdToken = TokenService.extractUserIdFromToken(token);

            ReservaRegisterDTO dto = gson.fromJson(req.getReader(), ReservaRegisterDTO.class);

            reservaService.createReserva(dto, userIdToken.intValue());

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(gson.toJson("Reserva agendada com sucesso."));

        } catch (UsuarioNaoEncontradoException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(gson.toJson("Usuário do token inválido."));
        } catch (BusinessException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no banco de dados: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido."));
                return;
            }
            Long userIdToken = TokenService.extractUserIdFromToken(token);
            UsuarioResponseDTO usuarioToken = usuarioService.getUsuario(userIdToken.intValue());

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("ID da reserva ausente na URL."));
                return;
            }

            ReservaUpdateDTO dto = gson.fromJson(req.getReader(), ReservaUpdateDTO.class);

            ReservaResponseDTO reservaExistente = reservaService.getReserva(id);
            if (reservaExistente.userId() != userIdToken.intValue() && usuarioToken.perfil() != Perfil.ADMIN) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson("Acesso negado. Você só pode editar suas próprias reservas."));
                return;
            }

            reservaService.updateReserva(id, dto);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Reserva ID " + id + " atualizada com sucesso."));

        } catch (ReservaNaoEncontradaException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (BusinessException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no banco de dados: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        try {
            String token = extrairToken(req);
            if (token == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write(gson.toJson("Token ausente ou inválido."));
                return;
            }
            Long userIdToken = TokenService.extractUserIdFromToken(token);
            UsuarioResponseDTO usuarioToken = usuarioService.getUsuario(userIdToken.intValue());

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson("ID da reserva ausente na URL."));
                return;
            }

            ReservaResponseDTO reservaExistente = reservaService.getReserva(id);
            if (reservaExistente.userId() != userIdToken.intValue() && usuarioToken.perfil() != Perfil.ADMIN) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write(gson.toJson("Acesso negado. Você só pode deletar suas próprias reservas."));
                return;
            }

            reservaService.deleteReserva(id);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson("Reserva deletada com sucesso."));

        } catch (ReservaNaoEncontradaException e) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(e.getMessage()));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no banco de dados: " + e.getMessage()));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson("Erro no servidor: " + e.getMessage()));
            e.printStackTrace();
        }
    }
}