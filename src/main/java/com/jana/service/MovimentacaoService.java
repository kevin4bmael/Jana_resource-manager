package com.jana.service;


import com.jana.dao.MovimentacaoDAO;
import com.jana.dtos.movimentacao.MovimentacaoRegisterDTO;
import com.jana.dtos.movimentacao.MovimentacaoResponseDTO;
import com.jana.dtos.movimentacao.MovimentacaoUpdateDTO;
import com.jana.exceptions.BusinessException;
import com.jana.exceptions.recurso.RecursoNaoEncontradoException;

import java.sql.SQLException;
import java.util.List;

public class MovimentacaoService {

    private final MovimentacaoDAO dao;

    public MovimentacaoService(MovimentacaoDAO dao) {
        this.dao = dao;
    }

    public MovimentacaoResponseDTO create(MovimentacaoRegisterDTO dto, Integer userId) throws SQLException {
        return dao.create(dto, userId);
    }

    public List<MovimentacaoResponseDTO> getAll() throws SQLException {
        return dao.findAll();
    }

    public MovimentacaoResponseDTO getById(Integer id) throws SQLException {
        return dao.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação não encontrada"));
    }

    public void update(Integer id, MovimentacaoUpdateDTO dto) throws SQLException {
        if (dao.findById(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Movimentação não encontrada");
        }
        dao.update(id, dto);
    }

    public void delete(Integer id) throws SQLException {
        if (dao.findById(id).isEmpty()) {
            throw new RecursoNaoEncontradoException("Movimentação não encontrada");
        }
        try {
            dao.delete(id);
        } catch (SQLException e) {
            throw new BusinessException("Não é possível deletar esta movimentação pois existem registros vinculados.");
        }
    }
}