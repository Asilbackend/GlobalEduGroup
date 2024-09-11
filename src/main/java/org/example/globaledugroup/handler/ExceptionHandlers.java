package org.example.globaledugroup.handler;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.CodecException;
import lombok.RequiredArgsConstructor;
import org.example.globaledugroup.handler.exceptions.AlreadyExist;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlers {

    //private final Logger logger;

    @ExceptionHandler(UsernameNotFoundException.class)
    public HttpEntity<?> handle(UsernameNotFoundException e) {
        //logger.warn("UsernameNotFoundException: " + e.getMessage());
        return ResponseEntity.status(400).body("username topilmadi");
    }

    @ExceptionHandler(BadCredentialsException.class)
    public HttpEntity<?> handle(BadCredentialsException e) {
        //logger.warn("BadCredentialsException: " + e.getMessage());
        return ResponseEntity.status(401).body(e.getMessage());
    }

    @ExceptionHandler(JwtException.class)
    public HttpEntity<?> handle(JwtException e) {
        //logger.warn("JwtException: " + e.getMessage());
        return ResponseEntity.status(401).body("tokenda xatolik");
    }

    @ExceptionHandler(CodecException.class)
    public HttpEntity<?> handle(CodecException e) {
        //logger.warn("JwtException: " + e.getMessage());
        return ResponseEntity.status(401).body("tokenda xatolik");
    }

//    @ExceptionHandler(MailException.class)
//    public HttpEntity<?> handle(MailException e) {
//        //logger.warn("mailException: " + e.getMessage());
//        return ResponseEntity.status(500).body(e.getMessage());
//    }
//
//    @ExceptionHandler(PasswordMismatchException.class)
//    public HttpEntity<?> handle(PasswordMismatchException e) {
//        //logger.warn("password exception: " + e.getMessage());
//        return ResponseEntity.status(401).body("tokenda xatolik");
//    }


    @ExceptionHandler(Exception.class)
    public HttpEntity<?> handle(Exception e) {
        //logger.error("Exception: " + e.getMessage());
        return ResponseEntity.status(500).body(
                "Serverda xatolik yuz berdi iltimos qayta urunib ko`ring"
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handle(DataIntegrityViolationException e) {
        //logger.warn(e.getMessage());
        return ResponseEntity.status(400).body(
                "Bunaqa ma`lumot bazada mavjud"
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public HttpEntity<?> handle(AccessDeniedException e) {
        //logger.warn(e.getMessage());
        return ResponseEntity.status(403).body(
                "Sizda ruxsat yo`q"
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public HttpEntity<?> handle(NoSuchElementException e) {
        return ResponseEntity.status(400).body(
                e.getMessage()
        );
    }

    @ExceptionHandler(RuntimeException.class)
    public HttpEntity<?> handle(RuntimeException e) {
        return ResponseEntity.status(400).body(
                e.getMessage()
        );
    }
    @ExceptionHandler(AlreadyExist.class)
    public HttpEntity<?> handle(AlreadyExist e) {
        System.out.println("Already exist ga tushdi");
        return ResponseEntity.status(400).body(
                e.getMessage()
        );
    }
}
