package main.java.com.jana.service;

import main.java.com.jana.dao.RecursoDAO;
import main.java.com.jana.dtos.recurso.RecursoRegisterDTO;
import main.java.com.jana.dtos.recurso.RecursoResponseDTO;
import main.java.com.jana.dtos.recurso.RecursoUpdateDTO;
import main.java.com.jana.exceptions.RecursoNaoEncontradoException;
import main.java.com.jana.model.Recurso;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class RecursoService {   
    private final RecursoDAO recursoDAO;

    public RecursoService(RecursoDAO recursoDAO) {
        this.recursoDAO = recursoDAO;
    }

    public RecursoResponseDTO getRecurso(Integer id) throws SQLException {
        Recurso recurso = recursoDAO.findRecursoById(id);
        return mapToRecursoResponseDTO(recurso);
    }

    public List<RecursoResponseDTO> getAllRecursos() throws SQLException {
        return recursoDAO.findAll().stream()
                .map(this::mapToRecursoResponseDTO)
                .collect(Collectors.toList());
    }

      @param dto 
      @param userId
     
    public void createRecurso(RecursoRegisterDTO dto, Integer userId) throws SQLException {
        Recurso novoRecurso = new Recurso(
                userId,
                dto.codPatrimonio(),
                dto.item(),
                dto.numero(),
                dto.funcional(),
                dto.observacao()
        );
        recursoDAO.saveRecurso(novoRecurso);
    }

     @param id 
     @param dto 
     
    public void updateRecurso(Integer id, RecursoUpdateDTO dto) throws SQLException {

        Recurso recurso = recursoDAO.findRecursoById(id);

        if (dto.codPatrimonio() != null) {
            recurso.setCodPatrimonio(dto.codPatrimonio());
        }
        if (dto.item() != null) {
            recurso.setItem(dto.item());
        }
        if (dto.numero() != null) {
            recurso.setNumero(dto.numero());
        }
        if (dto.funcional() != null) {
            recurso.setFuncional(dto.funcional());
        }
        if (dto.observacao() != null) {
            recurso.setObservacao(dto.observacao());
        }

        recursoDAO.updateRecurso(recurso);
    }

    public void deleteRecurso(Integer id) throws SQLException {

        recursoDAO.findRecursoById(id);
        recursoDAO.deleteById(id);
    }

    private RecursoResponseDTO mapToRecursoResponseDTO(Recurso recurso) {
        return new RecursoResponseDTO(
                recurso.getRecursoId(),
                recurso.getUserId(),
                recurso.getCodPatrimonio(),
                recurso.getItem(),
                recurso.getNumero(),
                recurso.getFuncional(),
                recurso.getObservacao()
        );
    }
}