package com.jana.dtos.reserva;


import com.jana.model.enums.Periodo;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaResponseDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        LocalDate dataReservada,
        String observacao,
        Periodo periodo,
        LocalTime horaRetirada,
        LocalTime horaEntrega
) { }