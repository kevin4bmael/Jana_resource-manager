package com.jana.controller;

import com.google.gson.Gson;
import com.jana.dao.RecursoDAO;
import com.jana.dao.UsuarioDAO;
import com.jana.dtos.local.MensagemResponse;
import com.jana.dtos.recurso.RecursoRegisterDTO;
import com.jana.dtos.recurso.RecursoResponseDTO;
import com.jana.dtos.recurso.RecursoUpdateDTO;
import com.jana.dtos.usuario.UsuarioResponseDTO;
import com.jana.exceptions.BusinessException;
import com.jana.exceptions.recurso.RecursoNaoEncontradoException;
import com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import com.jana.model.enums.Perfil;
import com.jana.service.RecursoService;
import com.jana.service.UsuarioService;
import com.jana.utils.TokenUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/recursos/*")
public class RecursoController extends HttpServlet {
    private final RecursoService recursoService = new RecursoService(new RecursoDAO());
    private final UsuarioService usuarioService = new UsuarioService(new UsuarioDAO());
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {
            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }

            Integer id = extrairIdDaUrl(req);

            if (id == null) {

                List<RecursoResponseDTO> recursos = recursoService.getAllRecursos();
                enviarSucesso(resp, recursos);
            } else {

                RecursoResponseDTO recurso = recursoService.getRecurso(id);
                enviarSucesso(resp, recurso);
            }

        } catch (RecursoNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {

            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }

            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);

            // Verificar se é administrador
            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado. Requer perfil de Administrador.");
                return;
            }


            RecursoRegisterDTO dto = gson.fromJson(req.getReader(), RecursoRegisterDTO.class);


            RecursoResponseDTO novoRecurso = recursoService.createRecurso(dto, userId);


            enviarSucesso(resp, HttpServletResponse.SC_CREATED, novoRecurso);

        } catch (UsuarioNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Usuário do token inválido.");
        } catch (BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {

            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }

            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);


            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado. Requer perfil de Administrador.");
                return;
            }


            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do recurso ausente na URL.");
                return;
            }


            RecursoUpdateDTO dto = gson.fromJson(req.getReader(), RecursoUpdateDTO.class);

            RecursoResponseDTO recursoAtualizado = recursoService.updateRecurso(id, dto);

            enviarSucesso(resp, recursoAtualizado);

        } catch (RecursoNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (UsuarioNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Usuário do token inválido.");
        } catch (BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        configurarResponse(resp);

        try {

            Integer userId = TokenUtils.extrairUserId(req);
            if (userId == null) {
                enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Token ausente ou inválido");
                return;
            }

            UsuarioResponseDTO usuarioLogado = usuarioService.getUsuario(userId);


            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado. Requer perfil de Administrador.");
                return;
            }


            Integer id = extrairIdDaUrl(req);
            if (id == null) {
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do recurso ausente na URL.");
                return;
            }


            recursoService.deleteRecurso(id);

            enviarMensagem(resp, HttpServletResponse.SC_OK, "Recurso deletado com sucesso");

        } catch (RecursoNaoEncontradoException | BusinessException e) {
            enviarErro(resp, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (UsuarioNaoEncontradoException e) {
            enviarErro(resp, HttpServletResponse.SC_UNAUTHORIZED, "Usuário do token inválido.");
        } catch (Exception e) {
            enviarErro(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro no servidor: " + e.getMessage());
            e.printStackTrace();
        }
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

    private void enviarMensagem(HttpServletResponse resp, int status, String mensagem) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(new MensagemResponse(true, mensagem)));
    }

    private void enviarErro(HttpServletResponse resp, int status, String mensagem) throws IOException {
        resp.setStatus(status);
        resp.getWriter().write(gson.toJson(new MensagemResponse(false, mensagem)));
    }
}