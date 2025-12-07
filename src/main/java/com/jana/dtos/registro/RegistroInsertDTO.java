package main.java.com.jana.dtos.registro;

public record RegistroInsertDTO(
        Integer reservaId,
        int userId,
        int recursoId,
        int localId,
        int movimentacaoId,
        String nome,
        String item,
        Integer numero,
        String ano,
        String turma,
        String periodo,
        String statusRecurso,
        String statusEntrega
) {}
