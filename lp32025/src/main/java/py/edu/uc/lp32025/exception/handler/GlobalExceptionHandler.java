package py.edu.uc.lp32025.exception.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import py.edu.uc.lp32025.dto.ErrorResponseDTO;
import py.edu.uc.lp32025.exception.EmpleadoNoEncontradoException;
import py.edu.uc.lp32025.exception.PermisoDenegadoException;

import java.time.format.DateTimeFormatter;

/**
 * Manejador global de excepciones con respuestas estandarizadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private ErrorResponseDTO buildError(HttpStatus status, String message, WebRequest request) {
        return new ErrorResponseDTO(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getDescription(false).replace("uri=", "")
        );
    }

    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> handleEmpleadoNoEncontrado(
            EmpleadoNoEncontradoException ex, WebRequest request) {
        ErrorResponseDTO error = buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PermisoDenegadoException.class)
    public ResponseEntity<ErrorResponseDTO> handlePermisoDenegado(
            PermisoDenegadoException ex, WebRequest request) {
        ErrorResponseDTO error = buildError(HttpStatus.FORBIDDEN, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex, WebRequest request) {
        ErrorResponseDTO error = buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponseDTO error = buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor.",
                request
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}