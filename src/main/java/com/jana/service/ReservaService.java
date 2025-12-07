package main.java.com.jana.service;

import main.java.com.jana.dao.ReservaDAO;
import main.java.com.jana.dtos.reserva.ReservaRegisterDTO;
import main.java.com.jana.dtos.reserva.ReservaResponseDTO;
import main.java.com.jana.dtos.reserva.ReservaUpdateDTO;
import main.java.com.jana.exceptions.BusinessException;
import main.java.com.jana.exceptions.reserva.ReservaNaoEncontradaException;
import main.java.com.jana.model.Reserva;
import main.java.com.jana.model.enums.Periodo;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ReservaService {

    private final ReservaDAO reservaDAO;

    public ReservaService(ReservaDAO reservaDAO) {
        this.reservaDAO = reservaDAO;
    }

    public ReservaResponseDTO getReserva(Integer id) throws SQLException {
        Reserva reserva = reservaDAO.findReservaById(id);
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

    public void createReserva(ReservaRegisterDTO dto, Integer userId) throws BusinessException, SQLException {
        if (dto.dataReservada() == null || dto.recursoId() == null || dto.localId() == null || dto.periodo() == null) {
            throw new BusinessException("Os campos Recurso, Local, Data e Período são obrigatórios para a reserva.");
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

    public void updateReserva(Integer id, ReservaUpdateDTO dto) throws SQLException, ReservaNaoEncontradaException, BusinessException {
        Reserva reserva = reservaDAO.findReservaById(id);

        if (dto.recursoId() != null || dto.dataReservada() != null || dto.periodo() != null) {
            Integer novoRecurso = (dto.recursoId() != null) ? dto.recursoId() : reserva.getRecursoId();
            LocalDate novaData = (dto.dataReservada() != null) ? dto.dataReservada() : reserva.getDataReservada();
            Periodo novoPeriodo = (dto.periodo() != null) ? dto.periodo() : reserva.getPeriodo();

            boolean dadosMudaram = !novaData.equals(reserva.getDataReservada()) ||
                    !novoPeriodo.equals(reserva.getPeriodo()) ||
                    !novoRecurso.equals(reserva.getRecursoId());

            if (dadosMudaram) {
                boolean conflito = reservaDAO.checkConflict(novoRecurso, novaData, novoPeriodo);
                if (conflito) {
                    throw new BusinessException("Conflito: Já existe reserva para estes novos dados.");
                }
            }
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
        reservaDAO.findReservaById(id);

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