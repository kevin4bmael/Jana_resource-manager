package main.java.com.jana.controller;

import com.google.gson.*;
import main.java.com.jana.dao.ReservaDAO;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.local.MensagemResponse;
import main.java.com.jana.dtos.reserva.ReservaRegisterDTO;
import main.java.com.jana.dtos.reserva.ReservaResponseDTO;
import main.java.com.jana.dtos.reserva.ReservaUpdateDTO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.reserva.ReservaNaoEncontradaException;

import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.service.ReservaService;
import main.java.com.jana.service.UsuarioService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/reservas/*")
public class ReservaController extends HttpServlet {

    private final ReservaService reservaService = new ReservaService(new ReservaDAO());

    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)))
            .registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) ->
                    LocalDate.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE))
            .registerTypeAdapter(LocalTime.class, (JsonSerializer<LocalTime>) (src, typeOfSrc, context) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_TIME)))
            .registerTypeAdapter(LocalTime.class, (JsonDeserializer<LocalTime>) (json, typeOfT, context) ->
                    LocalTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_TIME))
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {

            String userEmail = (String) req.getAttribute("userEmail");

            UsuarioResponseDTO usuarioLogado = usuarioService.findByEmail(userEmail);

            Integer id = extrairIdDaUrl(req);

            if (id != null) {
                ReservaResponseDTO reserva = reservaService.getReserva(id);

                if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !reserva.userId().equals(usuarioLogado.id())) {
                    enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Você não tem permissão para ver esta reserva.");
                    return;
                }
                enviarSucesso(resp, reserva);
            } else {
                // Se for ADMIN vê tudo, se for COMUM vê só as suas
                List<ReservaResponseDTO> reservas;
                if (usuarioLogado.perfil() == Perfil.ADMINISTRADOR) {
                    reservas = reservaService.getAllReservas();
                } else {
                    reservas = reservaService.getReservasByUserId(usuarioLogado.id());
                }
                enviarSucesso(resp, reservas);
            }

        } catch (ReservaNaoEncontradaException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            String userEmail = (String) req.getAttribute("userEmail");
            UsuarioResponseDTO usuarioLogado = usuarioService.findByEmail(userEmail);

            ReservaRegisterDTO dto = gson.fromJson(req.getReader(), ReservaRegisterDTO.class);

            reservaService.createReserva(dto, usuarioLogado.id());

            enviarMensagem(resp, HttpServletResponse.SC_CREATED, "Reserva realizada com sucesso.");

        } catch (BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            String userEmail = (String) req.getAttribute("userEmail");
            UsuarioResponseDTO usuarioLogado = usuarioService.findByEmail(userEmail);

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID da reserva obrigatório.");
                return;
            }


            ReservaResponseDTO reservaExistente = reservaService.getReserva(id);
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !reservaExistente.userId().equals(usuarioLogado.id())) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Você só pode editar suas próprias reservas.");
                return;
            }

            ReservaUpdateDTO dto = gson.fromJson(req.getReader(), ReservaUpdateDTO.class);
            reservaService.updateReserva(id, dto);

            enviarMensagem(resp, HttpServletResponse.SC_OK, "Reserva atualizada com sucesso.");

        } catch (ReservaNaoEncontradaException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            String userEmail = (String) req.getAttribute("userEmail");
            UsuarioResponseDTO usuarioLogado = usuarioService.findByEmail(userEmail);

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID da reserva obrigatório.");
                return;
            }

            ReservaResponseDTO reservaExistente = reservaService.getReserva(id);
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !reservaExistente.userId().equals(usuarioLogado.id())) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Você só pode cancelar suas próprias reservas.");
                return;
            }

            reservaService.deleteReserva(id);
            enviarMensagem(resp, HttpServletResponse.SC_OK, "Reserva cancelada com sucesso.");

        } catch (ReservaNaoEncontradaException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }



    private void configurarResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

    }

    private Integer extrairIdDaUrl(HttpServletRequest req) {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) return null;
        try {
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void enviarSucesso(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(data));
    }

    private void enviarMensagem(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);

        resp.getWriter().write(gson.toJson(new MensagemResponse(true, msg)));
    }

    private void enviarErro(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(new MensagemResponse(false, msg)));
    }
}