package main.java.com.jana.service;

import main.java.com.jana.dao.ReservaDAO;
import main.java.com.jana.dtos.reserva.ReservaRegisterDTO;
import main.java.com.jana.dtos.reserva.ReservaResponseDTO;
import main.java.com.jana.dtos.reserva.ReservaUpdateDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.ReservaNaoEncontradaException;
import main.java.com.jana.model.Reserva;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {
    private final ReservaDAO reservaDAO;


    public ReservaService(ReservaDAO reservaDAO) {
        this.reservaDAO = reservaDAO;
    }

    public ReservaResponseDTO getReserva(Integer id) throws SQLException, ReservaNaoEncontradaException {
        Reserva reserva = reservaDAO.findReservaById(id);
        if (reserva == null) {
            throw new ReservaNaoEncontradaException("Reserva com id: " + id + " não encontrada!");
        }
        return mapToReservaResponseDTO(reserva);
    }

    public List<ReservaResponseDTO> getAllReservas() throws SQLException {
        return reservaDAO.findAll().stream()
                .map(this::mapToReservaResponseDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaResponseDTO> getReservasByUserId(Integer userId) throws SQLException {
        return reservaDAO.findReservasByUserId(userId).stream()
                .map(this::mapToReservaResponseDTO)
                .collect(Collectors.toList());
    }

    public void createReserva(ReservaRegisterDTO dto, Integer userId) throws SQLException, BusinessException {
        if (dto.dataReservada() == null || dto.recursoId() == null || dto.localId() == null) {
            throw new BusinessException("Os campos Recurso, Local e Data são obrigatórios para a reserva.");
        }

        boolean conflito = reservaDAO.checkConflict(
                dto.recursoId(),
                dto.dataReservada(),
                dto.periodo()
        );

        if (conflito) {
            throw new BusinessException("O recurso já está reservado para a data e período selecionados.");
        }

        Reserva novaReserva = new Reserva(
                null,
                userId,
                dto.recursoId(),
                dto.localId(),
                dto.dataReservada(),
                dto.observacao(),
                dto.periodo(),
                dto.horaRetirada(),
                null
        );

        reservaDAO.saveReserva(novaReserva);
    }

    public void updateReserva(Integer id, ReservaUpdateDTO dto) throws SQLException, ReservaNaoEncontradaException {
        Reserva reserva = reservaDAO.findReservaById(id);

        if (reserva == null) {
            throw new ReservaNaoEncontradaException("Reserva com id: " + id + " não encontrada para atualização!");
        }

        if (dto.recursoId() != null) reserva.setRecursoId(dto.recursoId());
        if (dto.localId() != null) reserva.setLocalId(dto.localId());
        if (dto.dataReservada() != null) reserva.setDataReservada(dto.dataReservada());
        if (dto.observacao() != null) reserva.setObservacao(dto.observacao());
        if (dto.periodo() != null) reserva.setPeriodo(dto.periodo());
        if (dto.horaRetirada() != null) reserva.setHoraRetirada(dto.horaRetirada());

        if (dto.horaEntrega() != null) reserva.setHoraEntrega(dto.horaEntrega());

        reservaDAO.updateReserva(reserva);
    }

    public void deleteReserva(Integer id) throws SQLException, ReservaNaoEncontradaException {
        if (reservaDAO.findReservaById(id) == null) {
            throw new ReservaNaoEncontradaException("Reserva com id: " + id + " não encontrada para exclusão!");
        }
        reservaDAO.deleteById(id);
    }

    private ReservaResponseDTO mapToReservaResponseDTO(Reserva reserva) {
        return new ReservaResponseDTO(
                reserva.getReservaId(),
                reserva.getUserId(),
                reserva.getRecursoId(),
                reserva.getLocalId(),
                reserva.getDataReservada(),
                reserva.getObservacao(),
                reserva.getPeriodo(),
                reserva.getHoraRetirada(),
                reserva.getHoraEntrega()
        );
    }
}