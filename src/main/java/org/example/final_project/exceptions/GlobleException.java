package org.example.final_project.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobleException {

	@ExceptionHandler(UserException.class)
	public ResponseEntity<ErrorDetails> UserExceptionHandler(UserException ue, WebRequest req){

		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(PostException.class)
	public ResponseEntity<ErrorDetails> PostExceptionHandler(PostException ue, WebRequest req){

		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);

	}

	@ExceptionHandler(CommentException.class)
	public ResponseEntity<ErrorDetails> CommentsExceptionHandler(CommentException ue, WebRequest req){

		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);

	}
	@ExceptionHandler(StoryException.class)
	public ResponseEntity<ErrorDetails> StoryExceptionHandler(StoryException ue, WebRequest req){

		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);

	}
//	@ExceptionHandler(ReeelException.class)
//	public ResponseEntity<ErrorDetails> ReelExceptionHandler(ReeelException ue, WebRequest req){
//
//		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());
//
//		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
//
//	}

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorDetails> badCredentialsExceptionHandler(BadCredentialsException ex, WebRequest req){
		ErrorDetails err = new ErrorDetails("Sai email hoặc mật khẩu", req.getDescription(false), LocalDateTime.now());
		return new ResponseEntity<ErrorDetails>(err, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorDetails> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException me){
		ErrorDetails err=new ErrorDetails(me.getBindingResult().getFieldError().getDefaultMessage(),"validation error",LocalDateTime.now());
		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorDetails> OtherExceptionHandler(Exception ue, WebRequest req){

		ErrorDetails err= new ErrorDetails(ue.getMessage(),req.getDescription(false),LocalDateTime.now());

		return new ResponseEntity<ErrorDetails>(err,HttpStatus.BAD_REQUEST);

	}


}
