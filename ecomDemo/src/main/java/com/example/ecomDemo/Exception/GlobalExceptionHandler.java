package com.example.ecomDemo.Exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> notBlankException(MethodArgumentNotValidException exception){
        Map<String, String> map=new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(err->{
            String fieldName= ((FieldError)err).getField();
            String message=err.getDefaultMessage();
            map.put(fieldName,message);
        });
        return new ResponseEntity<>(map,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<?> apiException(APIException e){
        String message= e.getMessage();
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException e){
        String message=e.getMessage();
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }
}
