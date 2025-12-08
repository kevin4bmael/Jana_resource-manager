package com.jana.service;

import com.jana.dao.LocalDAO;
import com.jana.dtos.local.LocalRegisterDTO;
import com.jana.dtos.local.LocalResponseDTO;
import com.jana.dtos.local.LocalUpdateDTO;
import com.jana.exceptions.BusinessException;
import com.jana.model.Local;
import com.jana.model.enums.TipoLocal;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class LocalService {
    private final LocalDAO localDAO;

    public LocalService(LocalDAO localDAO) {
        this.localDAO = localDAO;
    }

    public LocalResponseDTO getLocal(Integer id) throws SQLException {
        Local local = localDAO.findLocalById(id);
        return mapToLocalResponseDTO(local);
    }

    public List<LocalResponseDTO> getAllLocais() throws SQLException {
        return localDAO.findAll().stream()
                .map(this::mapToLocalResponseDTO)
                .collect(Collectors.toList());
    }

    public LocalResponseDTO createLocal(LocalRegisterDTO dto, Integer userId) throws SQLException {
        if (dto.local() == null) {
            throw new BusinessException("O tipo de local é obrigatório");
        }

        if (dto.local() == TipoLocal.SALA_DE_AULA) {
            if (dto.ano() == null || dto.turma() == null) {
                throw new BusinessException("Ano e turma são obrigatórios para salas de aula");
            }
        }

        Local novoLocal = new Local();
        novoLocal.setUserId(userId);
        novoLocal.setLocal(dto.local());
        novoLocal.setAno(dto.ano());
        novoLocal.setTurma(dto.turma());

        if (novoLocal.getLocal() != TipoLocal.SALA_DE_AULA) {
            novoLocal.setAno(null);
            novoLocal.setTurma(null);
        }

        localDAO.saveLocal(novoLocal);

        return mapToLocalResponseDTO(novoLocal);
    }

    public LocalResponseDTO updateLocal(Integer id, LocalUpdateDTO dto) throws SQLException {
        Local local = localDAO.findLocalById(id);

        if (dto.local() != null) {
            local.setLocal(dto.local());
        }
        if (dto.ano() != null) {
            local.setAno(dto.ano());
        }
        if (dto.turma() != null) {
            local.setTurma(dto.turma());
        }

        if (local.getLocal() != TipoLocal.SALA_DE_AULA) {
            local.setAno(null);
            local.setTurma(null);
        }

        localDAO.updateLocal(local);

        return mapToLocalResponseDTO(local);
    }

    public void deleteLocal(Integer id) throws SQLException {
        localDAO.deleteById(id);
    }

    private LocalResponseDTO mapToLocalResponseDTO(Local local) {
        return new LocalResponseDTO(
                local.getLocalId(),
                local.getUserId(),
                local.getLocal(),
                local.getAno(),
                local.getTurma()
        );
    }
}