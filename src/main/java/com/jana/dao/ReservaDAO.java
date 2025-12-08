package com.jana.dao;


import com.jana.exceptions.reserva.ReservaNaoEncontradaException;
import com.jana.model.Reserva;
import com.jana.model.enums.Periodo;
import com.jana.utils.Conexao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    private static final String FIND_BY_ID_SQL = "SELECT * FROM reserva WHERE reservaId = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM reserva";
    private static final String INSERT_SQL = "INSERT INTO reserva (userId, recursoId, localId, dataReservada, observacao, periodo, horaRetirada, horaEntrega) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE reserva SET userId = ?, recursoId = ?, localId = ?, dataReservada = ?, observacao = ?, periodo = ?, horaRetirada = ?, horaEntrega = ? WHERE reservaId = ?";
    private static final String DELETE_SQL = "DELETE FROM reserva WHERE reservaId = ?";
    private static final String FIND_BY_USER_ID_SQL = "SELECT * FROM reserva WHERE userId = ?";
    private static final String CHECK_CONFLICT_SQL = "SELECT COUNT(*) FROM reserva WHERE recursoId = ? AND dataReservada = ? AND periodo = ?";


    public void saveReserva(Reserva reserva) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, reserva.getUserId());
            preparedStatement.setInt(2, reserva.getRecursoId());
            preparedStatement.setInt(3, reserva.getLocalId());

            if (reserva.getDataReservada() != null) {
                preparedStatement.setDate(4, java.sql.Date.valueOf(reserva.getDataReservada()));
            } else {
                preparedStatement.setNull(4, java.sql.Types.DATE);
            }

            preparedStatement.setString(5, reserva.getObservacao());
            preparedStatement.setString(6, reserva.getPeriodo().toString());

            if (reserva.getHoraRetirada() != null) {
                preparedStatement.setTime(7, java.sql.Time.valueOf(reserva.getHoraRetirada()));
            } else {
                preparedStatement.setNull(7, java.sql.Types.TIME);
            }

            if (reserva.getHoraEntrega() != null) {
                preparedStatement.setTime(8, java.sql.Time.valueOf(reserva.getHoraEntrega()));
            } else {
                preparedStatement.setNull(8, java.sql.Types.TIME);
            }

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Falha ao criar reserva, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setReservaId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar reserva, ID não obtido.");
                }
            }
        }
    }

    public Reserva findReservaById(Integer reservaId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_ID_SQL)) {

            preparedStatement.setInt(1, reservaId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSetToReserva(resultSet);
                } else {
                    throw new ReservaNaoEncontradaException("Reserva com id: " + reservaId + " não encontrada!");
                }
            }
        }
    }
    public List<Reserva> findReservasByUserId(Integer userId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_USER_ID_SQL)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    reservas.add(resultSetToReserva(resultSet));
                }
            }
        }

        return reservas;
    }

    public boolean checkConflict(Integer recursoId, LocalDate dataReservada, Periodo periodo) throws SQLException {
        int count = 0;

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(CHECK_CONFLICT_SQL)) {

            preparedStatement.setInt(1, recursoId);
            preparedStatement.setDate(2, java.sql.Date.valueOf(dataReservada));
            preparedStatement.setString(3, periodo.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    count = resultSet.getInt(1);
                }
            }
        }
        return count > 0;
    }



    public List<Reserva> findAll() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                reservas.add(resultSetToReserva(resultSet));
            }
        }
        return reservas;
    }

    public void updateReserva(Reserva reserva) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_SQL)) {

            preparedStatement.setInt(1, reserva.getUserId());
            preparedStatement.setInt(2, reserva.getRecursoId());
            preparedStatement.setInt(3, reserva.getLocalId());

            if (reserva.getDataReservada() != null) {
                preparedStatement.setDate(4, java.sql.Date.valueOf(reserva.getDataReservada()));
            } else {
                preparedStatement.setNull(4, java.sql.Types.DATE);
            }

            preparedStatement.setString(5, reserva.getObservacao());
            preparedStatement.setString(6, reserva.getPeriodo().toString());

            if (reserva.getHoraRetirada() != null) {
                preparedStatement.setTime(7, java.sql.Time.valueOf(reserva.getHoraRetirada()));
            } else {
                preparedStatement.setNull(7, java.sql.Types.TIME);
            }

            // ✅ Corrigido: movido para ANTES do executeUpdate e removido duplicação
            if (reserva.getHoraEntrega() != null) {
                preparedStatement.setTime(8, java.sql.Time.valueOf(reserva.getHoraEntrega()));
            } else {
                preparedStatement.setNull(8, java.sql.Types.TIME);
            }

            preparedStatement.setInt(9, reserva.getReservaId());

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ReservaNaoEncontradaException("Reserva com id: " + reserva.getReservaId() + " não encontrada para atualização.");
            }
        }
    }

    public void deleteById(Integer reservaId) throws SQLException {
        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(DELETE_SQL)) {

            preparedStatement.setInt(1, reservaId);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ReservaNaoEncontradaException("Reserva com id: " + reservaId + " não encontrada para exclusão.");
            }
        }
    }

    private Reserva resultSetToReserva(ResultSet resultSet) throws SQLException {
        Integer reservaId = resultSet.getInt("reservaId");
        Integer userId = resultSet.getInt("userId");
        Integer recursoId = resultSet.getInt("recursoId");
        Integer localId = resultSet.getInt("localId");

        java.sql.Date sqlDate = resultSet.getDate("dataReservada");
        LocalDate dataReservada = (sqlDate != null) ? sqlDate.toLocalDate() : null;

        String observacao = resultSet.getString("observacao");

        String periodoString = resultSet.getString("periodo");
        Periodo periodo = (periodoString != null) ? Periodo.valueOf(periodoString.toUpperCase()) : null;


        Time sqlTimeRetirada = resultSet.getTime("horaRetirada");
        LocalTime horaRetirada = (sqlTimeRetirada != null) ? sqlTimeRetirada.toLocalTime() : null;

        Time sqlTimeEntrega = resultSet.getTime("horaEntrega");
        LocalTime horaEntrega = (sqlTimeEntrega != null) ? sqlTimeEntrega.toLocalTime() : null;


        Reserva reserva = new Reserva();
        reserva.setReservaId(reservaId);
        reserva.setUserId(userId);
        reserva.setRecursoId(recursoId);
        reserva.setLocalId(localId);
        reserva.setDataReservada(dataReservada);
        reserva.setObservacao(observacao);
        reserva.setPeriodo(periodo);
        reserva.setHoraRetirada(horaRetirada);
        reserva.setHoraEntrega(horaEntrega);

        return reserva;
    }
}