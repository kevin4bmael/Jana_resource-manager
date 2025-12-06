package main.java.com.jana.dao;

import main.java.com.jana.exceptions.ReservaNaoEncontradaException;
import main.java.com.jana.model.Reserva;
import main.java.com.jana.model.enums.Periodo;
import main.java.com.jana.utils.Conexao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {

    public Reserva findReservaById(Integer reservaId) throws SQLException, ReservaNaoEncontradaException {
        String sql = "SELECT * FROM reserva WHERE reservaId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

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

    public List<Reserva> findAll() throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                reservas.add(resultSetToReserva(resultSet));
            }
        }
        return reservas;
    }

    public List<Reserva> findReservasByUserId(Integer userId) throws SQLException {
        List<Reserva> reservas = new ArrayList<>();
        String sql = "SELECT * FROM reserva WHERE userId = ? ORDER BY dataReservada DESC, horaRetirada DESC";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, userId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    reservas.add(resultSetToReserva(resultSet));
                }
            }
        }
        return reservas;
    }

    public void saveReserva(Reserva reserva) throws SQLException {
        String sql = "INSERT INTO reserva (userId, recursoId, localId, dataReservada, observacao, periodo, horaRetirada, horaEntrega) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = Conexao.getConnection();

             PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setInt(1, reserva.getUserId());
            preparedStatement.setInt(2, reserva.getRecursoId());
            preparedStatement.setInt(3, reserva.getLocalId());
            preparedStatement.setDate(4, reserva.getDataReservada());
            preparedStatement.setString(6, reserva.getPeriodo().toString());
            preparedStatement.setTime(7, reserva.getHoraRetirada());

            if (reserva.getObservacao() != null) {
                preparedStatement.setString(5, reserva.getObservacao());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (reserva.getHoraEntrega() != null) {
                preparedStatement.setTime(8, reserva.getHoraEntrega());
            } else {
                preparedStatement.setNull(8, Types.TIME);
            }

            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reserva.setReservaId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateReserva(Reserva reserva) throws SQLException, ReservaNaoEncontradaException {
        String sql = "UPDATE reserva SET userId = ?, recursoId = ?, localId = ?, dataReservada = ?, observacao = ?, periodo = ?, horaRetirada = ?, horaEntrega = ? WHERE reservaId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, reserva.getUserId());
            preparedStatement.setInt(2, reserva.getRecursoId());
            preparedStatement.setInt(3, reserva.getLocalId());
            preparedStatement.setDate(4, reserva.getDataReservada());
            preparedStatement.setString(6, reserva.getPeriodo().toString());
            preparedStatement.setTime(7, reserva.getHoraRetirada());

            if (reserva.getObservacao() != null) {
                preparedStatement.setString(5, reserva.getObservacao());
            } else {
                preparedStatement.setNull(5, Types.VARCHAR);
            }
            if (reserva.getHoraEntrega() != null) {
                preparedStatement.setTime(8, reserva.getHoraEntrega());
            } else {
                preparedStatement.setNull(8, Types.TIME);
            }

            preparedStatement.setInt(9, reserva.getReservaId()); // WHERE clause

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                throw new ReservaNaoEncontradaException("Reserva com id: " + reserva.getReservaId() + " não encontrada para atualização.");
            }
        }
    }

    public void deleteById(Integer id) throws SQLException {
        String sql = "DELETE FROM reserva WHERE reservaId = ?";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    public boolean checkConflict(Integer recursoId, java.sql.Date dataReservada, Periodo periodo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM reserva " +
                "WHERE recursoId = ? AND dataReservada = ? AND periodo = ? AND horaEntrega IS NULL";

        try (Connection connection = Conexao.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, recursoId);
            preparedStatement.setDate(2, dataReservada);
            preparedStatement.setString(3, periodo.toString());

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    private Reserva resultSetToReserva(ResultSet resultSet) throws SQLException {
        // Assume-se que a classe Reserva tem um construtor ou setters apropriados
        int reservaId = resultSet.getInt("reservaId");
        int userId = resultSet.getInt("userId");
        int recursoId = resultSet.getInt("recursoId");
        int localId = resultSet.getInt("localId");

        java.sql.Date dataReservada = resultSet.getDate("dataReservada");
        String observacao = resultSet.getString("observacao");

        Periodo periodo = Periodo.valueOf(resultSet.getString("periodo"));

        java.sql.Time horaRetirada = resultSet.getTime("horaRetirada");
        java.sql.Time horaEntrega = resultSet.getTime("horaEntrega"); // Pode ser null

        return new Reserva(
                reservaId,
                userId,
                recursoId,
                localId,
                dataReservada,
                observacao,
                periodo,
                horaRetirada,
                horaEntrega
        );
    }
}