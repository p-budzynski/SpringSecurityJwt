package pl.kurs.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.kurs.dto.ExceptionResponseDto;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalHandlerException {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        String errorMessages = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .map(err -> {
                    if (err instanceof FieldError fieldError) {
                        return fieldError.getDefaultMessage();
                    }
                    return err.getDefaultMessage();
                })
                .collect(Collectors.joining("; "));

        ExceptionResponseDto response = new ExceptionResponseDto(errorMessages, HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(RoleIsExistsException.class)
    public ResponseEntity<ExceptionResponseDto> handleRoleIsExistsException(RoleIsExistsException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ExceptionResponseDto> handleUserNotFoundException(UserNotFoundException exception) {
        ExceptionResponseDto response = new ExceptionResponseDto(exception.getMessage(), HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(response);
    }
}
