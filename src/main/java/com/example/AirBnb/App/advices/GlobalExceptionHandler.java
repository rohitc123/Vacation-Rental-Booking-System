package com.example.AirBnb.App.advices;
import com.example.AirBnb.App.exception.ResourceNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException exception){
        ApiError apiError=ApiError.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();
        return BuildErrorResponseEntity(apiError);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception exception){
        ApiError apiError=ApiError.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
        return BuildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputNotValidException(MethodArgumentNotValidException exception){
        List<String> errors=exception
                .getBindingResult()
                .getAllErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        ApiError apiError=ApiError
                .builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .subErrors(errors)
                .build();
        return BuildErrorResponseEntity(apiError);
    }


    private ResponseEntity<ApiResponse<?>> BuildErrorResponseEntity(ApiError apiError) {
        return  new ResponseEntity<>(new ApiResponse<>(apiError),apiError.getHttpStatus());
    }
}

