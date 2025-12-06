package main.java.com.jana.dtos.reserva;

import main.java.com.jana.model.enums.Periodo;

public record ReservaResponseDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        java.util.Date dataReservada,
        String observacao,
        Periodo periodo,
        java.time.LocalTime horaRetirada,
        java.sql.Time horaEntrega
) { }