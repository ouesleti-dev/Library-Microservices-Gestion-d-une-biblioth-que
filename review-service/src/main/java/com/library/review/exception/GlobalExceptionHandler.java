package com.library.review.exception;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.LocalDateTime; import java.util.*; import java.util.stream.Collectors;
@RestControllerAdvice public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class) public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e){return build(HttpStatus.NOT_FOUND,e.getMessage());}
    @ExceptionHandler(MethodArgumentNotValidException.class) public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException e){return build(HttpStatus.BAD_REQUEST,e.getBindingResult().getFieldErrors().stream().map(x->x.getField()+" : "+x.getDefaultMessage()).collect(Collectors.joining(" ; ")));}
    private ResponseEntity<Map<String,Object>> build(HttpStatus s,String m){Map<String,Object>b=new LinkedHashMap<>();b.put("timestamp", LocalDateTime.now().toString());b.put("status",s.value());b.put("error",s.getReasonPhrase());b.put("message",m);return ResponseEntity.status(s).body(b);}
}
