package com.jana.exceptions.recurso;

public class RecursoNaoEncontradoException extends RuntimeException {    
    public RecursoNaoEncontradoException(String message) {
        super(message);
    }
}