package br.com.asv.service.exception;

import java.util.List;

import javax.validation.ConstraintViolationException;

public interface IError {
	
//	ResponseEntity<?> contraintViolation(ConstraintViolationException e);
//
//	ResponseEntity<?> exception(Exception e);
	
	List<String> contraintViolationString(ConstraintViolationException e);

	List<String> exceptionString(Exception e);
}
