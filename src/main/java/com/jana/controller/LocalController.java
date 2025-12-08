package main.java.com.jana.controller;

import com.google.gson.Gson;
import main.java.com.jana.dao.LocalDAO;
import main.java.com.jana.dao.UsuarioDAO;
import main.java.com.jana.dtos.local.LocalRegisterDTO;
import main.java.com.jana.dtos.local.LocalResponseDTO;
import main.java.com.jana.dtos.local.LocalUpdateDTO;
import main.java.com.jana.dtos.local.MensagemResponse;
import main.java.com.jana.dtos.usuario.UsuarioResponseDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.local.LocalNaoEncontradoException;
import main.java.com.jana.exceptions.usuario.UsuarioNaoEncontradoException;
import main.java.com.jana.model.enums.Perfil;
import main.java.com.jana.service.LocalService;
import main.java.com.jana.service.UsuarioService;
import main.java.com.jana.utils.TokenUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/locais/*") 

public class LocalController extends HttpServlet {
    private final LocalService localService = new LocalService(new LocalDAO());
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

                List<LocalResponseDTO> locais = localService.getAllLocais();
                enviarSucesso(resp, locais);
            } else {

                LocalResponseDTO local = localService.getLocal(id);
                enviarSucesso(resp, local);
            }

        } catch (LocalNaoEncontradoException e) {
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


            if (usuarioLogado.perfil() != Perfil.ADMINISTRADOR) {
                enviarErro(resp, HttpServletResponse.SC_FORBIDDEN,
                        "Acesso negado. Requer perfil de Administrador.");
                return;
            }


            LocalRegisterDTO dto = gson.fromJson(req.getReader(), LocalRegisterDTO.class);


            LocalResponseDTO novoLocal = localService.createLocal(dto, userId);


            enviarSucesso(resp, HttpServletResponse.SC_CREATED, novoLocal);

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
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do local ausente na URL.");
                return;
            }

            LocalUpdateDTO dto = gson.fromJson(req.getReader(), LocalUpdateDTO.class);


            LocalResponseDTO localAtualizado = localService.updateLocal(id, dto);


            enviarSucesso(resp, localAtualizado);

        } catch (LocalNaoEncontradoException e) {
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
                enviarErro(resp, HttpServletResponse.SC_BAD_REQUEST, "ID do local ausente na URL.");
                return;
            }

            localService.deleteLocal(id);


            enviarMensagem(resp, HttpServletResponse.SC_OK, "Local deletado com sucesso");

        } catch (LocalNaoEncontradoException | BusinessException e) {
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


     // Envia resposta de sucesso com status 200

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