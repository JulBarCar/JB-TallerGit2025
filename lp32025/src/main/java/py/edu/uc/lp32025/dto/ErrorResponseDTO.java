package py.edu.uc.lp32025.dto;

public class ErrorResponseDTO extends AbstractResponseDTO {

    public ErrorResponseDTO() {
        super(400, "Bad request", "An error occurred");
    }

    public ErrorResponseDTO(int status, String error, String userError) {
        super(status, error, userError);
    }
}