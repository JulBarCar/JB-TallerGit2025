package py.edu.uc.lp32025.dto;

public class GreetingDTO extends BaseResponseDTO {
    private String message;

    public GreetingDTO() {
        super(200, null, null); // Default success status
    }

    public GreetingDTO(String message) {
        super(200, null, null); // Default success status
        this.message = message;
    }

    public GreetingDTO(int status, String error, String userError, String message) {
        super(status, error, userError);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}