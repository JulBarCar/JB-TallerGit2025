package py.edu.uc.lp32025.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DTO para respuestas de error estandarizadas.
 */
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseDTO extends AbstractResponseDTO {

    private String timestamp;
    private String path;

    public ErrorResponseDTO() {
        super(400, "Bad request", "An error occurred");
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ErrorResponseDTO(int status, String error, String userError) {
        super(status, error, userError);
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    public ErrorResponseDTO(int status, String error, String userError, String path) {
        this(status, error, userError);
        this.path = path;
    }

}