package br.com.asv.service.exception;

import java.util.List;

import jakarta.validation.ConstraintViolationException;

public interface IError {
	
	List<String> contraintViolationString(ConstraintViolationException e);

	List<String> exceptionString(Exception e);
}
