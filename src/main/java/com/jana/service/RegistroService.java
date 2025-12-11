package com.jana.service;

import com.jana.dao.RegistroDAO;
import com.jana.dtos.registro.*;
import com.jana.exceptions.BusinessException;
import com.jana.model.Registro;
import com.jana.model.enums.StatusEntrega;
import com.jana.model.enums.StatusRecurso;

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
        r.setRecursoId(dto.recursoId());
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
        r.setStatusRecurso(dto.statusRecurso() != null ? dto.statusRecurso() : StatusRecurso.OCUPADO);
        r.setStatusEntrega(dto.statusEntrega() != null ? dto.statusEntrega() : StatusEntrega.AUSENTE);

        registroDAO.saveRegistro(r);
    }

    public void registrarDevolucao(RegistroUpdateDTO dto) throws BusinessException, SQLException {

        Registro r = registroDAO.findRegistroById(dto.registroId());

        if (r == null) {
            throw new BusinessException("Registro não encontrado.");
        }

        if (r.getMomentoDevolucao() != null) {
            throw new BusinessException("Este registro já está finalizado.");
        }

        if (dto.statusRecurso() != null) {
            r.setStatusRecurso(dto.statusRecurso());
        } else {
            r.setStatusRecurso(StatusRecurso.OCUPADO);
        }

        String statusFinal;
        if (dto.statusEntrega() != null) {
            r.setStatusEntrega(dto.statusEntrega());
            statusFinal = dto.statusEntrega().name();
        } else {
            r.setStatusEntrega(StatusEntrega.AUSENTE);
            statusFinal = StatusEntrega.AUSENTE.name();
        }

        registroDAO.registrarDevolucao(dto.registroId(), statusFinal);
    }

    public List<RegistroResponseDTO> listarTodosDTO() throws SQLException {

        List<Registro> registros = registroDAO.findAll();
        return toDTOList(registros);
    }

    public List<RegistroResponseDTO> listarPorUsuarioDTO(int userId) throws SQLException {

        List<Registro> registros = registroDAO.findByUserId(userId);
        return toDTOList(registros);
    }

    public List<RegistroPendenteDto> listarPendentes() throws SQLException {

        return registroDAO.findPendentes();
    }

    public List<RegistroHistoricoDTO> listarHistoricoCompleto() throws SQLException {

        return registroDAO.findHistorico();
    }

    private List<RegistroResponseDTO> toDTOList(List<Registro> registros) {
        List<RegistroResponseDTO> dtos = new ArrayList<>();

        for (Registro r : registros) {
            RegistroResponseDTO dto = new RegistroResponseDTO(
                    r.getRegistroId(),
                    r.getReservaId(),
                    r.getUserId(),
                    r.getRecursoId(),
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