package main.java.com.jana.controller;

import main.java.com.jana.dtos.registro.RegistroDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.service.RegistroService;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/registro")
public class RegistroController extends HttpServlet {

    private RegistroService registroService = new RegistroService();
    private Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");
        if (acao == null) {
            acao = "listar";
        }

        try {
            if ("listar".equals(acao)) {
                listar(request, response);
            } else if ("listarPorUsuario".equals(acao)) {
                listarPorUsuario(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de banco de dados.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");

        try {
            if ("devolver".equals(acao)) {
                devolver(request, response);
            } else if ("retirar".equals(acao)) {
                retirar(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de banco de dados.");
        }
    }

    private void listar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        List<RegistroDTO> registros = registroService.listarTodosDTO();
        String json = gson.toJson(registros);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    private void listarPorUsuario(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        int userId = Integer.parseInt(request.getParameter("userId"));

        List<RegistroDTO> registros = registroService.listarPorUsuarioDTO(userId);
        String json = gson.toJson(registros);

        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    private void retirar(HttpServletRequest request, HttpServletResponse response)
            throws IOException, BusinessException, SQLException {

        int reserveId = Integer.parseInt(request.getParameter("reserveId"));   // pode ser 0 se não usar reserva
        int userId = Integer.parseInt(request.getParameter("userId"));
        int resourceId = Integer.parseInt(request.getParameter("resourceId"));

        registroService.registrarRetirada(reserveId, userId, resourceId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Retirada registrada com sucesso.");
    }

    private void devolver(HttpServletRequest request, HttpServletResponse response)
            throws IOException, BusinessException, SQLException {

        int registroId = Integer.parseInt(request.getParameter("registroId"));

        registroService.registrarDevolucao(registroId);

        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write("Devolução registrada com sucesso.");
    }
}
