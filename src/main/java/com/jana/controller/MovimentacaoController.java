package main.java.com.jana.controller;

import com.google.gson.*;
import main.java.com.jana.dao.MovimentacaoDAO;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.movimentacao.MovimentacaoRegisterDTO;
import main.java.com.jana.dtos.movimentacao.MovimentacaoResponseDTO;
import main.java.com.jana.dtos.movimentacao.MovimentacaoUpdateDTO;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.BusinessException;

import main.java.com.jana.exceptions.recurso.RecursoNaoEncontradoException;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.service.MovimentacaoService;
import main.java.com.jana.service.UsuarioService;
import main.java.com.jana.utils.TokenUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/movimentacoes/*")
public class MovimentacaoController extends HttpServlet {

    private final MovimentacaoService movimentacaoService = new MovimentacaoService(new MovimentacaoDAO());
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                }
            })
            .create();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            Integer id = extrairIdDaUrl(req);

            if (id != null) {
                MovimentacaoResponseDTO mov = movimentacaoService.getById(id);

                if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR && !mov.userId().equals(userId)) {
                    enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return;
                }
                enviarSucesso(resp, mov);
            } else {
                if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                    enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                    return;
                }
                List<MovimentacaoResponseDTO> lista = movimentacaoService.getAll();
                enviarSucesso(resp, lista);
            }

        } catch (RecursoNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }

            MovimentacaoRegisterDTO dto = gson.fromJson(req.getReader(), MovimentacaoRegisterDTO.class);
            MovimentacaoResponseDTO novaMov = movimentacaoService.create(dto, userId);

            enviarSucesso(resp, HttpServletResponse.SC_CREATED, novaMov);

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
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                return;
            }

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID obrigatório na URL");
                return;
            }

            MovimentacaoUpdateDTO dto = gson.fromJson(req.getReader(), MovimentacaoUpdateDTO.class);
            movimentacaoService.update(id, dto);

            enviarMensagem(resp, HttpServletResponse.SC_OK, "Movimentação atualizada com sucesso.");

        } catch (RecursoNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
                return;
            }
            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN, "Acesso negado.");
                return;
            }

            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID obrigatório na URL");
                return;
            }

            movimentacaoService.delete(id);
            enviarMensagem(resp, HttpServletResponse.SC_OK, "Movimentação deletada.");

        } catch (RecursoNaoEncontradoException | BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro interno: " + e.getMessage());
        }
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

    private void configurarResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private void enviarSucesso(HttpServletResponse resp, Object data) throws IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(data));
    }

    private void enviarSucesso(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(data));
    }

    private void enviarMensagem(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(String.format("{\"success\": true, \"message\": \"%s\"}", msg));
    }

    private void enviarErro(HttpServletResponse resp, int status, String msg) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(String.format("{\"success\": false, \"error\": \"%s\"}", msg));
    }
}