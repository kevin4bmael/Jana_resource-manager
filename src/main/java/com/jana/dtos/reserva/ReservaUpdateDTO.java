package main.java.com.jana.dtos.reserva;

import main.java.com.jana.model.enums.Periodo;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaUpdateDTO(
        Integer recursoId,
        Integer localId,
        LocalDate dataReservada,
        String observacao,
        Periodo periodo,
        LocalTime horaRetirada,
        LocalTime horaEntrega // (Usado para finalizar/devolver a reserva)
) { }