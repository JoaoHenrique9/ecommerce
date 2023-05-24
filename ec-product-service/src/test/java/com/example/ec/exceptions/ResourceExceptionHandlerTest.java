package com.example.ec.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
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
    void validation_ShouldHandleMethodArgumentNotValidException() {

        String field1 = "field1";
        String field2 = "field2";
        String defaultMessage1 = "Default message 1";
        String defaultMessage2 = "Default message 2";
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors())
                .thenReturn(List.of(fieldError(field1, defaultMessage1), fieldError(field2, defaultMessage2)));

        ResponseEntity<StandardError> response = exceptionHandler.validation(exception, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        StandardError error = response.getBody();
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), error.getStatus());
        assertEquals("Validation error", error.getError());
        assertEquals("Formulário inválido", error.getMessage());
        assertEquals(request.getRequestURI(), error.getPath());
        Map<String, String> expectedErrors = new HashMap<>();
        expectedErrors.put(field1, defaultMessage1);
        expectedErrors.put(field2, defaultMessage2);
        // assertEquals(expectedErrors, error.getErrors());
    }

    @Test
    void objectNotFound_ShouldHandleObjectNotFoundException() {

        String errorMessage = "Object not found";
        ObjectNotFoundException exception = new ObjectNotFoundException(errorMessage);

        ResponseEntity<StandardError> response = exceptionHandler.objectNotFound(exception, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        StandardError error = response.getBody();
        assertEquals(HttpStatus.NOT_FOUND.value(), error.getStatus());
        assertEquals("Not found", error.getError());
        assertEquals(errorMessage, error.getMessage());
        assertEquals(request.getRequestURI(), error.getPath());
    }

    private FieldError fieldError(String field, String defaultMessage) {
        return new FieldError("objectName", field, "", false, null, null, defaultMessage);
    }

}
