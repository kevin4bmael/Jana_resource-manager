package com.jana.service;

import com.jana.dao.MovimentacaoDAO;
import com.jana.dtos.movimentacao.*;
import com.jana.model.Movimentacao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MovimentacaoService {

    private final MovimentacaoDAO movimentacaoDAO;

    public MovimentacaoService() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public List<MovimentacaoResponseDTO> listarItensAusentes() throws SQLException {
        List<Movimentacao> pendentes = movimentacaoDAO.findPendentes();
        return toDTOList(pendentes);
    }

    public List<MovimentacaoResponseDTO> listarHistoricoGeral() throws SQLException {
        List<Movimentacao> lista = movimentacaoDAO.findAll();
        return toDTOList(lista);
    }

    public List<MovimentacaoResponseDTO> listarHistoricoDoUsuario(int userId) throws SQLException {
        List<Movimentacao> lista = movimentacaoDAO.findByUserId(userId);
        return toDTOList(lista);
    }

    private List<MovimentacaoResponseDTO> toDTOList(List<Movimentacao> lista) {
        List<MovimentacaoResponseDTO> dtos = new ArrayList<>();
        for (Movimentacao m : lista) {
            dtos.add(new MovimentacaoResponseDTO(
                    m.getMovimentacaoId(),
                    m.getUserId(),
                    m.getRecursoId(),
                    m.getPeriodo(),
                    m.getMomentoRetirada(),
                    m.getMomentoDevolucao(),
                    m.getStatusRecurso(),
                    m.getStatusEntrega()
            ));
        }
        return dtos;
    }
}