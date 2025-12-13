package com.jana.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.movimentacao.MovimentacaoResponseDTO;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.exceptions.BusinessException;
import com.jana.model.enums.Perfil;
import com.jana.service.MovimentacaoService;
import com.jana.service.UsuarioService;
import com.jana.utils.TokenUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/movimentacao")
public class MovimentacaoController extends HttpServlet {

    private final MovimentacaoService movimentacaoService;
    private final UsuarioService usuarioService;
    private final Gson gson;

    public MovimentacaoController() {
        this.movimentacaoService = new MovimentacaoService();
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
                        String dateStr = in.nextString();
                        return dateStr != null ? LocalDateTime.parse(dateStr) : null;
                    }
                })
                .create();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String acao = request.getParameter("acao");
        if (acao == null) acao = "listarPendentes";

        try {
            Integer userId = TokenUtils.extrairUserId(request);
            if (userId == null) {
                enviarErro(response, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            switch (acao) {
                case "listarPendentes":
                    listarItensAusentes(request, response, usuarioLogado);
                    break;
                case "listarHistorico":
                    listarHistoricoGeral(request, response, usuarioLogado);
                    break;
                case "listarPorUsuario":
                    listarHistoricoDoUsuario(request, response, usuarioLogado);
                    break;
                default:
                    enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (BusinessException e) {
            enviarErro(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void listarItensAusentes(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException {

        List<MovimentacaoResponseDTO> lista = movimentacaoService.listarItensAusentes();
        enviarJSON(response, lista);
    }

    private void listarHistoricoGeral(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException {

        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
            return;
        }
        List<MovimentacaoResponseDTO> lista = movimentacaoService.listarHistoricoGeral();
        enviarJSON(response, lista);
    }

    private void listarHistoricoDoUsuario(HttpServletRequest request, HttpServletResponse response, UsuarioResponseDTO usuarioLogado)
            throws IOException, SQLException, BusinessException {

        String idParam = request.getParameter("userId");
        if (idParam == null) throw new BusinessException("userId obrigatório");

        Integer idSolicitado = Integer.parseInt(idParam);

        if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !usuarioLogado.id().equals(idSolicitado)) {
            enviarErro(response, HttpServletResponse.SC_FORBIDDEN, "Você não pode visualizar movimentações de outro usuário.");
            return;
        }

        List<MovimentacaoResponseDTO> lista = movimentacaoService.listarHistoricoDoUsuario(idSolicitado);
        enviarJSON(response, lista);
    }

    private void enviarJSON(HttpServletResponse response, Object objeto) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(gson.toJson(objeto));
    }

    private void enviarErro(HttpServletResponse response, int status, String msg) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"success\": false, \"error\": \"%s\"}", msg.replace("\"", "'")));
    }
}