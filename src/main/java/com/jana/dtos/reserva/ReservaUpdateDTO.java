package com.jana.dtos.reserva;

import com.jana.model.enums.Periodo;

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