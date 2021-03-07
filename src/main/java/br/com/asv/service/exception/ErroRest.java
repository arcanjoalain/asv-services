package br.com.asv.service.exception;

import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.stereotype.Controller;

@Controller
public class ErroRest extends AError {

//	@Override
//	public ResponseEntity<?> contraintViolation(ConstraintViolationException e) {
//		Object[] s = e.getConstraintViolations().toArray();
//		for (int i = 0; i < s.length; i++) {
//			System.out.println("ERROR:"+ s[i].toString());
//		}
//		if (e.toString().contains("already exists")) {
//			return ResponseEntity.status(500).body("already exists");
//		} else if (e.toString().contains("duplicate")) {
//			return ResponseEntity.status(500).body("value_duplicate");
//		} else {
//			e.printStackTrace();
//			return ResponseEntity.status(500).build();
//		}
//	}
//
//	@Override
//	public ResponseEntity<?> exception(Exception e) {
//		ResponseEntity<?> result = null;
//		Throwable rootException = e;
//		while (rootException.getCause() != null) {
//			rootException = rootException.getCause();
//			System.out.println(rootException.getLocalizedMessage());
//			if (rootException.getLocalizedMessage().contains("duplicate")) {
//				result = ResponseEntity.status(500).body("value_duplicate");
//			}
//		}
//
//		if (result == null) {
//			result = ResponseEntity.status(500).build();
//		}
//		return result;
//	}

	@Override
	public List<String> contraintViolationString(ConstraintViolationException e) {
		Object[] s = e.getConstraintViolations().toArray();
		List<String> result = new LinkedList<>();
		for (int i = 0; i < s.length; i++) {
			result.add(s[i].toString());
		}
//		if (e.toString().contains("already exists")) {
////			System.out.println("already exists");
//		} else if (e.toString().contains("duplicate")) {
////			System.out.println("value duplicate");
//		} else {
//			e.printStackTrace();
//		}
		return result;
	}

	@Override
	public List<String> exceptionString(Exception e) {
		Throwable rootException = e;
		List<String> result = new LinkedList<>();
		if (rootException.getCause() != null) {
			rootException = e;
			while (rootException.getCause() != null) {
				System.out.println(rootException);
				rootException = rootException.getCause();
				result.add(rootException.getLocalizedMessage());
//				if (rootException.getLocalizedMessage().contains("duplicate")) {
//				System.out.println("value_duplicate");
//				}
			}
		} else {
			result.add(rootException.getMessage().toString());
		}

//		if (result == null) {
//			result = ResponseEntity.status(500).build();
//		}
		return result;
	}

}