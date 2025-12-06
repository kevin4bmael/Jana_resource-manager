package main.java.com.jana.dtos.reserva;

import main.java.com.jana.model.enums.Periodo;
import java.sql.Date;
import java.sql.Time;

public record ReservaResponseDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        Date dataReservada,
        String observacao,
        Periodo periodo,
        Time horaRetirada,
        Time horaEntrega
) { }