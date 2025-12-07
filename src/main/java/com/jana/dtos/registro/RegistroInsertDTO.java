package main.java.com.jana.dtos.registro;

public record RegistroInsertDTO(
        Integer reservaId,
        Integer userId,
        Integer recursoId,
        Integer localId,
        Integer movimentacaoId,
        String nome,
        String item,
        Integer numero,
        String ano,
        String turma,
        String periodo,
        String statusRecurso,
        String statusEntrega
) {}
