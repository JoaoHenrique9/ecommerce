package com.example.ec.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class ResourceExceptionHandlerTest {

    private ResourceExceptionHandler exceptionHandler;

    @Mock
    private HttpServletRequest request;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        exceptionHandler = new ResourceExceptionHandler();
    }

    @Test
    void shouldMethodArgumentNotValidException() {

        String field1 = "field1";
        String field2 = "field2";
        String defaultMessage1 = "Default message 1";
        String defaultMessage2 = "Default message 2";
        List<FieldMessage> expectedErrors = new ArrayList<>();
        expectedErrors.add(new FieldMessage(field1, defaultMessage1));
        expectedErrors.add(new FieldMessage(field2, defaultMessage2));
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError(field1, defaultMessage1),
                        fieldError(field2, defaultMessage2)));

        ResponseEntity<ValidationError> response = exceptionHandler.validation(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(ValidationError::getTimestamp)
                .isInstanceOf(Long.class)
                .isNotEqualTo(0L)
                .isNotEqualTo("");

        assertThat(response.getBody())
                .isNotNull()
                .isNotEqualTo("")
                .extracting(
                        ValidationError::getStatus,
                        ValidationError::getError,
                        ValidationError::getMessage,
                        ValidationError::getPath)
                .containsExactly(UNPROCESSABLE_ENTITY.value(),
                        "Validation error",
                        "Formulário inválido",
                        request.getRequestURI());

        assertThat(response.getBody())
                .isNotNull()
                .extracting(ValidationError::getErrors)
                .usingRecursiveComparison().isEqualTo(expectedErrors);

        ValidationError error = response.getBody();

        if (error != null) {
            List<FieldMessage> errors = error.getErrors();
            assertThat(errors.get(0).getFieldName()).isNotNull();
            assertThat(errors.get(0).getFieldName()).isNotEmpty();
            assertThat(errors.get(0).getMessage()).isNotNull();
            assertThat(errors.get(0).getMessage()).isNotEmpty();

            assertThat(errors.get(1).getFieldName()).isNotNull();
            assertThat(errors.get(1).getFieldName()).isNotEmpty();
            assertThat(errors.get(1).getMessage()).isNotNull();
            assertThat(errors.get(1).getMessage()).isNotEmpty();

            assertThat(errors.get(0).getFieldName()).isEqualTo(expectedErrors.get(0).getFieldName());
            assertThat(errors.get(0).getMessage()).isEqualTo(expectedErrors.get(0).getMessage());

            assertThat(errors.get(1).getFieldName()).isEqualTo(expectedErrors.get(1).getFieldName());
            assertThat(errors.get(1).getMessage()).isEqualTo(expectedErrors.get(1).getMessage());
        }
    }

    private FieldError fieldError(String field, String defaultMessage) {
        return new FieldError("objectName", field, "", false, null, null,
                defaultMessage);
    }

    @Test
    void shouldObjectNotFoundException() {

        String errorMessage = "Object not found";
        ObjectNotFoundException exception = new ObjectNotFoundException(errorMessage);

        ResponseEntity<StandardError> response = exceptionHandler.objectNotFound(exception, request);

        assertEquals(NOT_FOUND, response.getStatusCode());
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);

        assertThat(response.getBody())
                .isNotNull()
                .extracting(StandardError::getTimestamp)
                .isInstanceOf(Long.class)
                .isNotEqualTo(0L)
                .isNotEqualTo("");

        assertThat(response.getBody())
                .isNotNull()
                .isNotEqualTo("")
                .extracting(StandardError::getStatus, StandardError::getError,
                        StandardError::getMessage,
                        StandardError::getPath)
                .containsExactly(NOT_FOUND.value(), "Not found", "Object not found",
                        request.getRequestURI());

    }

    @Test
    public void shouldEmailException() {
        EmailException exception = new EmailException("Invalid email");

        ResponseEntity<StandardError> response = exceptionHandler.EmailException(exception, request);

        assertEquals(BAD_REQUEST, response.getStatusCode());

        assertThat(response.getBody())
                .isNotNull()
                .extracting(StandardError::getTimestamp)
                .isInstanceOf(Long.class)
                .isNotEqualTo(0L)
                .isNotEqualTo("");

        assertThat(response.getBody())
                .isNotNull()
                .isNotEqualTo("")
                .extracting(StandardError::getStatus, StandardError::getError,
                        StandardError::getMessage,
                        StandardError::getPath)
                .containsExactly(BAD_REQUEST.value(), "Bad Request", "Invalid email",
                        request.getRequestURI());
    }

    @Test
    public void shouldPasswordException() {
        PasswordException exception = new PasswordException("Invalid password");

        ResponseEntity<StandardError> response = exceptionHandler.PasswordException(exception, request);

        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);

        assertThat(response.getBody())
                .isNotNull()
                .isNotEqualTo("")
                .extracting(StandardError::getTimestamp)
                .isInstanceOf(Long.class)
                .isNotEqualTo(0L);

        assertThat(response.getBody())
                .isNotNull()
                .isNotEqualTo("")
                .extracting(
                        StandardError::getStatus, StandardError::getError,
                        StandardError::getMessage,
                        StandardError::getPath)
                .containsExactly(BAD_REQUEST.value(), "Bad Request", "Invalid password",
                        request.getRequestURI());
    }

}
