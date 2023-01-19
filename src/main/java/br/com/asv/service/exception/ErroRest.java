package br.com.asv.service.exception;

import java.util.LinkedList;
import java.util.List;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolationException;

@Controller
@Service
public class ErroRest implements IError {

	private static final Logger LOGGER = LogManager.getLogger(ErroRest.class);

	@Override
	public List<String> contraintViolationString(ConstraintViolationException e) {
		Object[] s = e.getConstraintViolations().toArray();
		List<String> result = new LinkedList<>();
		for (int i = 0; i < s.length; i++) {
			result.add(s[i].toString());
		}
		if (e.toString().contains("already exists")) {
			LOGGER.error("already exists");
		} else if (e.toString().contains("duplicate")) {
			LOGGER.error("value duplicate");
		} else {
			LOGGER.error(e.getMessage());
		}
		return result;
	}

	@Override
	public List<String> exceptionString(Exception e) {
		Throwable rootException = e;
		List<String> result = new LinkedList<>();
		if (rootException.getCause() != null) {
			while (rootException.getCause() != null) {
				LOGGER.error(rootException);
				rootException = rootException.getCause();
				result.add(rootException.getLocalizedMessage());
				if (rootException.getLocalizedMessage().contains("duplicate")) {
					LOGGER.error("value_duplicate");
				}
			}
		} else {
			result.add(rootException.getMessage());
		}

		return result;
	}

}