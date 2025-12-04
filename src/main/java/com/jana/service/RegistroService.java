package main.java.com.jana.service;

import main.java.com.jana.dao.RegistroDAO;
import main.java.com.jana.dtos.registro.RegistroDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.model.Registro;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RegistroService {

    private final RegistroDAO registroDAO;

    public RegistroService() {
        this.registroDAO = new RegistroDAO();
    }

    
    public void registrarRetirada(int reserveId, int userId, int resourceId)
            throws BusinessException, SQLException {

        if (userId <= 0 || resourceId <= 0) {
            throw new BusinessException("Usuário e recurso são obrigatórios para retirada.");
        }

        Registro r = new Registro();
        r.setReserveId(reserveId);      
        r.setUserId(userId);
        r.setResourceId(resourceId);
        r.setDataRetirada(new Date());
        r.setDataDevolucao(null);

        registroDAO.criar(r);
    }

    public void registrarDevolucao(int registroId) throws BusinessException, SQLException {

        Registro r = registroDAO.buscarPorId(registroId);
        if (r == null) {
            throw new BusinessException("Registro não encontrado.");
        }

        if (r.getDataDevolucao() != null) {
            throw new BusinessException("Este registro já está finalizado (já possui devolução).");
        }

        r.setDataDevolucao(new Date());
        registroDAO.atualizar(r);
    }

    public List<RegistroDTO> listarTodosDTO() throws SQLException {
        List<Registro> registros = registroDAO.listarTodos();
        return toDTOList(registros);
    }

    public List<RegistroDTO> listarPorUsuarioDTO(int userId) throws SQLException {
        List<Registro> registros = registroDAO.listarPorUsuario(userId);
        return toDTOList(registros);
    }

    private List<RegistroDTO> toDTOList(List<Registro> registros) {
        List<RegistroDTO> dtos = new ArrayList<>();

        for (Registro r : registros) {
            RegistroDTO dto = new RegistroDTO();
            dto.setRegistroId(r.getRegistroId());
            dto.setReserveId(r.getReserveId());
            dto.setUserId(r.getUserId());
            dto.setResourceId(r.getResourceId());
            dto.setDataRetirada(r.getDataRetirada());
            dto.setDataDevolucao(r.getDataDevolucao());

            if (r.getDataDevolucao() == null) {
                dto.setStatusMovimentacao("EM_USO");
            } else {
                dto.setStatusMovimentacao("FINALIZADO");
            }

            dtos.add(dto);
        }

        return dtos;
    }
}
