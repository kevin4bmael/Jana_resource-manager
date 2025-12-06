package main.java.com.jana.dtos.reserva;

import main.java.com.jana.model.enums.Periodo;
import java.sql.Date;
import java.sql.Time;

public record ReservaRegisterDTO(
        Integer recursoId,
        Integer localId,
        Date dataReservada,
        String observacao,
        Periodo periodo,
        Time horaRetirada
) { }