package main.java.com.jana.dtos.local;

public class MensagemResponse {
    private final boolean success;
    private final String message;

    public MensagemResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    @SuppressWarnings("unused")
    public boolean isSuccess() {
        return success;
    }

    @SuppressWarnings("unused")
    public String getMessage() {
        return message;
    }
}

