package com.jana.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.registro.*;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.exceptions.BusinessException;
import com.jana.model.enums.Perfil;
import com.jana.service.RegistroService;
import com.jana.service.UsuarioService;
import com.jana.utils.TokenUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/registro")
public class RegistroController extends HttpServlet {

    private final RegistroService registroService;
    private final UsuarioService usuarioService;
    private final Gson gson;

    public RegistroController() {
        this.registroService = new RegistroService();
        this.usuarioService = new UsuarioService(new UsuarioDAO());
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
                    @Override
                    public void write(JsonWriter out, LocalDateTime value) throws IOException {
                        if (value != null) {
                            out.value(value.toString());
                        } else {
                            out.nullValue();
                        }
                    }
                    @Override
                    public LocalDateTime read(JsonReader in) throws IOException {
                        return LocalDateTime.parse(in.nextString());
                    }
                })
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");
        if (acao == null) acao = "listar";

        try {
            Integer userId = TokenUtils.extrairUserId(request);
            if (userId == null) {
                enviarErro(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            switch (acao) {
                case "listar":
                    listarTodos(request, response, usuarioLogado);
                    break;
                case "listarPorUsuario":
                    listarPorUsuario(request, response, usuarioLogado);
                    break;
                case "listarPendentes":
                    listarPendentes(request, response, usuarioLogado);
                    break;
                case "listarHistorico":
                    listarHistorico(request, response, usuarioLogado);
                    break;
                default:
                    enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");
        if (acao == null) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "Ação obrigatória");
            return;
        }

        try {

            Integer userId = TokenUtils.extrairUserId(request);
            if (userId == null) {
                enviarErro(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            switch (acao) {
                case "retirar":
                    registrarRetirada(request, response, usuarioLogado);
                    break;
                case "devolver":
                    registrarDevolucao(request, response, usuarioLogado);
                    break;
                default:
                    enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }


    private void listarTodos(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException {

        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
            return;
        }
        List<RegistroResponseDTO> lista = registroService.listarTodosDTO();
        enviarJSON(response, lista);
    }

    private void listarPorUsuario(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException, BusinessException {

        String idParam = request.getParameter("userId");
        if (idParam == null) throw new BusinessException("userId obrigatório");

        Integer idSolicitado = Integer.parseInt(idParam);


        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !usuarioLogado.id().equals(idSolicitado)) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Você não pode visualizar registros de outro usuário.");
            return;
        }

        List<RegistroResponseDTO> lista = registroService.listarPorUsuarioDTO(idSolicitado);
        enviarJSON(response, lista);
    }

    private void listarPendentes(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException {
        // Geralmente apenas Admin ou setor responsável vê pendências gerais
        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
            return;
        }
        List<RegistroPendenteDto> lista = registroService.listarPendentes();
        enviarJSON(response, lista);
    }

    private void listarHistorico(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException {

        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
            return;
        }
        List<RegistroHistoricoDTO> lista = registroService.listarHistoricoCompleto();
        enviarJSON(response, lista);
    }

    private void registrarRetirada(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, BusinessException, SQLException {

        String json = lerBody(request);
        RegistroInsertDTO dto = gson.fromJson(json, RegistroInsertDTO.class);

        if (dto == null) throw new BusinessException("JSON inválido");



        registroService.registrarRetirada(dto);
        enviarSucesso(response, "Retirada realizada com sucesso");
    }

    private void registrarDevolucao(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, BusinessException, SQLException {

        String json = lerBody(request);
        RegistroUpdateDTO dto = gson.fromJson(json, RegistroUpdateDTO.class);

        if (dto == null) throw new BusinessException("JSON inválido");

        registroService.registrarDevolucao(dto);
        enviarSucesso(response, "Devolução realizada com sucesso");
    }


    private String lerBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    private void enviarJSON(HttpServletResponse response, Object objeto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(objeto));
    }

    private void enviarSucesso(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"success\": true, \"message\": \"%s\"}", msg));
    }

    private void enviarErro(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"success\": false, \"error\": \"%s\"}", msg.replace("\"", "'")));
    }
}