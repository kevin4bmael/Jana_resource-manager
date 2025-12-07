package main.java.com.jana.service;

import main.java.com.jana.dao.LocalDAO;
import main.java.com.jana.dtos.local.LocalRegisterDTO;
import main.java.com.jana.dtos.local.LocalResponseDTO;
import main.java.com.jana.dtos.local.LocalUpdateDTO;
import main.java.com.jana.model.Local;
import main.java.com.jana.model.enums.TipoLocal;

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

     @param dto 
     @param userId
     
    public void createLocal(LocalRegisterDTO dto, Integer userId) throws SQLException {
        Local novoLocal = new Local(
                userId,
                dto.local(),
                dto.ano(),
                dto.turma()
        );

        if (novoLocal.getLocal() != TipoLocal.SALA_DE_AULA) {
            novoLocal.setAno(null);
            novoLocal.setTurma(null);
        }

        localDAO.saveLocal(novoLocal);
    }

     @param id 
     @param dto 
  
    public void updateLocal(Integer id, LocalUpdateDTO dto) throws SQLException {
     
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
    }

    public void deleteLocal(Integer id) throws SQLException {
        localDAO.findLocalById(id);

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