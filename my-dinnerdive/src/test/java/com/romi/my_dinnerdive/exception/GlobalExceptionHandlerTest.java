package com.romi.my_dinnerdive.exception;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SuppressWarnings({"null", "unused"})
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldReturnStatusAndErrorMessageWhenHandleResponseStatusException() {
        ResponseStatusException ex = new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");

        ResponseEntity<Map<String, String>> response = handler.handleResponseStatus(ex);
        Map<String, String> body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("帳號已存在", body.get("error"));
    }

    @Test
    void shouldReturnBadRequestAndFieldErrorsWhenHandleValidationException() throws Exception {
        SampleRequest request = new SampleRequest();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(request, "sampleRequest");
        bindingResult.addError(new FieldError("sampleRequest", "username", "使用者名稱不可空白"));
        bindingResult.addError(new FieldError("sampleRequest", "userPassword", "密碼不可空白"));

        HandlerMethod handlerMethod = new HandlerMethod(
                new DummyController(),
                DummyController.class.getMethod("dummyApi", SampleRequest.class));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                handlerMethod.getMethodParameters()[0],
                bindingResult);

        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);
        Map<String, String> body = response.getBody();
        assertNotNull(body);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("使用者名稱不可空白", body.get("username"));
        assertEquals("密碼不可空白", body.get("userPassword"));
    }

    @RestController
    private static class DummyController {
        public void dummyApi(@RequestBody SampleRequest request) {
        }
    }

    private static class SampleRequest {
    }
}
