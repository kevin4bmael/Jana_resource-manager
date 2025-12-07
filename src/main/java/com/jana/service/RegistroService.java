package main.java.com.jana.service;

import main.java.com.jana.dao.RegistroDAO;
import main.java.com.jana.dtos.registro.RegistroHistoricoDTO;
import main.java.com.jana.dtos.registro.RegistroInsertDTO;
import main.java.com.jana.dtos.registro.RegistroPendenteDto;
import main.java.com.jana.dtos.registro.RegistroResponseDTO;
import main.java.com.jana.dtos.registro.RegistroUpdateDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.model.Registro;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RegistroService {

    private final RegistroDAO registroDAO;

    public RegistroService() {
        this.registroDAO = new RegistroDAO();
    }

    public void registrarRetirada(RegistroInsertDTO dto) throws BusinessException, SQLException {
        if (dto.userId() <= 0 || dto.recursoId() <= 0 || dto.localId() <= 0) {
            throw new BusinessException("Usuário, recurso e local são obrigatórios para a retirada.");
        }

        Registro r = new Registro();
        r.setReservaId(dto.reservaId());
        r.setUserId(dto.userId());
        r.setResourceId(dto.recursoId());
        r.setLocalId(dto.localId());
        r.setMovimentacaoId(dto.movimentacaoId());

        r.setNome(dto.nome());
        r.setItem(dto.item());
        r.setNumero(dto.numero());
        r.setAno(dto.ano());
        r.setTurma(dto.turma());
        r.setPeriodo(dto.periodo());

        r.setMomentoRetirada(LocalDateTime.now());
        r.setMomentoDevolucao(null);
        r.setStatusRecurso(dto.statusRecurso() != null ? dto.statusRecurso() : "Ocupado");
        r.setStatusEntrega(dto.statusEntrega() != null ? dto.statusEntrega() : "Ausente");

        registroDAO.criar(r);
    }

    public void registrarDevolucao(RegistroUpdateDTO dto) throws BusinessException, SQLException {
        Registro r = registroDAO.buscarPorId(dto.registroId());

        if (r == null) {
            throw new BusinessException("Registro não encontrado.");
        }

        if (r.getMomentoDevolucao() != null) {
            throw new BusinessException("Este registro já está finalizado.");
        }

        String statusFinal = (dto.statusEntrega() != null && !dto.statusEntrega().isEmpty())
                ? dto.statusEntrega()
                : "Entregue";

        registroDAO.registrarDevolucao(dto.registroId(), statusFinal);
    }

    public List<RegistroResponseDTO> listarTodosDTO() throws SQLException {
        List<Registro> registros = registroDAO.listarTodos();
        return toDTOList(registros);
    }

    public List<RegistroResponseDTO> listarPorUsuarioDTO(int userId) throws SQLException {
        List<Registro> registros = registroDAO.listarPorUsuario(userId);
        return toDTOList(registros);
    }

    public List<RegistroPendenteDto> listarPendentes() throws SQLException {
        return registroDAO.listarPendentes();
    }

    public List<RegistroHistoricoDTO> listarHistoricoCompleto() throws SQLException {
        return registroDAO.listarHistorico();
    }

    private List<RegistroResponseDTO> toDTOList(List<Registro> registros) {
        List<RegistroResponseDTO> dtos = new ArrayList<>();

        for (Registro r : registros) {
            RegistroResponseDTO dto = new RegistroResponseDTO(
                    r.getRegistroId(),
                    r.getReservaId(),
                    r.getUserId(),
                    r.getResourceId(),
                    r.getLocalId(),
                    r.getMovimentacaoId(),
                    r.getNome(),
                    r.getItem(),
                    r.getNumero(),
                    r.getAno(),
                    r.getTurma(),
                    r.getPeriodo(),
                    r.getMomentoRetirada(),
                    r.getMomentoDevolucao(),
                    r.getStatusRecurso(),
                    r.getStatusEntrega()
            );
            dtos.add(dto);
        }
        return dtos;
    }
}