package com.codingshuttle.linkedin.notification_service.advice;


import com.codingshuttle.linkedin.notification_service.exceptions.BadRequestException;
import com.codingshuttle.linkedin.notification_service.exceptions.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleInternalServerError(Exception e) {
        ApiError apiError = ApiError.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFound(ResourceNotFoundException e) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.NOT_FOUND)
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException e) {

        ApiError apiError = ApiError.builder()
                .status(HttpStatus.BAD_REQUEST)
                .message(e.getMessage())
                .build();
        return buildErrorResponseEntity(apiError);
    }

//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException e) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.UNAUTHORIZED)
//                .message(e.getMessage())
//                .build();
//        return buildErrorResponseEntity(apiError);
//    }

//    @ExceptionHandler(JwtException.class)
//    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException e) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.UNAUTHORIZED)
//                .message(e.getMessage())
//                .build();
//        return buildErrorResponseEntity(apiError);
//    }

//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException e) {
//        ApiError apiError = ApiError.builder()
//                .status(HttpStatus.FORBIDDEN)
//                .message(e.getMessage())
//                .build();
//        return buildErrorResponseEntity(apiError);
//    }


    public ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError), apiError.getStatus());
    }
}
