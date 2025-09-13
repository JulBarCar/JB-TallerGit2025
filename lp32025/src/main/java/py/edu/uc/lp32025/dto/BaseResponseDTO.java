package py.edu.uc.lp32025.dto;

public abstract class BaseResponseDTO {
    private int status;
    private String error;
    private String userError;

    public BaseResponseDTO() {
    }

    public BaseResponseDTO(int status, String error, String userError) {
        this.status = status;
        this.error = error;
        this.userError = userError;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getUserError() {
        return userError;
    }

    public void setUserError(String userError) {
        this.userError = userError;
    }
}