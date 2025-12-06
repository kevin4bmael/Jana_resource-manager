package main.java.com.jana.dtos.reserva;

import main.java.com.jana.model.enums.Periodo;
import java.sql.Date;
import java.sql.Time;

public record ReservaUpdateDTO(
        Integer recursoId,
        Integer localId,
        Date dataReservada, // java.sql.Date
        String observacao,
        Periodo periodo, // Enum: Manhã, Tarde
        Time horaRetirada, // java.sql.Time
        Time horaEntrega // java.sql.Time (Usado para finalizar/devolver a reserva)
) { }