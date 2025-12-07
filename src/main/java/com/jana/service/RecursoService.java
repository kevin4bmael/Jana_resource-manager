package main.java.com.jana.service;

import main.java.com.jana.dao.RecursoDAO;
import main.java.com.jana.dtos.recurso.RecursoRegisterDTO;
import main.java.com.jana.dtos.recurso.RecursoResponseDTO;
import main.java.com.jana.dtos.recurso.RecursoUpdateDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.recurso.RecursoNaoEncontradoException;
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

    public RecursoResponseDTO createRecurso(RecursoRegisterDTO dto, Integer userId) throws SQLException {

        if (dto.item() == null || dto.item().trim().isEmpty()) {
            throw new BusinessException("O campo 'item' é obrigatório");
        }
        if (dto.funcional() == null) {
            throw new BusinessException("O campo 'funcional' é obrigatório");
        }

        if (dto.codPatrimonio() != null && dto.codPatrimonio().length() > 7) {
            throw new BusinessException("O código patrimonial deve ter no máximo 7 caracteres");
        }


        if (dto.observacao() != null && dto.observacao().length() > 100) {
            throw new BusinessException("A observação deve ter no máximo 100 caracteres");
        }

        Recurso novoRecurso = new Recurso(
                userId,
                dto.codPatrimonio(),
                dto.item().trim(),
                dto.numero(),
                dto.funcional(),
                dto.observacao()
        );

        recursoDAO.saveRecurso(novoRecurso);

        return mapToRecursoResponseDTO(novoRecurso);
    }

    public RecursoResponseDTO updateRecurso(Integer id, RecursoUpdateDTO dto) throws SQLException {

        Recurso recurso = recursoDAO.findRecursoById(id);


        if (dto.codPatrimonio() == null && dto.item() == null && dto.numero() == null
                && dto.funcional() == null && dto.observacao() == null) {
            throw new BusinessException("Nenhum campo foi enviado para atualização");
        }


        if (dto.codPatrimonio() != null) {
            if (dto.codPatrimonio().length() > 7) {
                throw new BusinessException("O código patrimonial deve ter no máximo 7 caracteres");
            }
            recurso.setCodPatrimonio(dto.codPatrimonio());
        }

        if (dto.item() != null) {
            if (dto.item().trim().isEmpty()) {
                throw new BusinessException("O campo 'item' não pode ser vazio");
            }
            recurso.setItem(dto.item().trim());
        }

        if (dto.numero() != null) {
            recurso.setNumero(dto.numero());
        }

        if (dto.funcional() != null) {
            recurso.setFuncional(dto.funcional());
        }

        if (dto.observacao() != null) {
            if (dto.observacao().length() > 100) {
                throw new BusinessException("A observação deve ter no máximo 100 caracteres");
            }
            recurso.setObservacao(dto.observacao());
        }

        recursoDAO.updateRecurso(recurso);

        return mapToRecursoResponseDTO(recurso);
    }

    public void deleteRecurso(Integer id) throws SQLException {
        recursoDAO.findRecursoById(id);
        boolean deletado = recursoDAO.deleteById(id);
        if (!deletado) {
            throw new RecursoNaoEncontradoException("Falha ao deletar o recurso com id: " + id);
        }
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