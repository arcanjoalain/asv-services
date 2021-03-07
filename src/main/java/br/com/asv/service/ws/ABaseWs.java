package br.com.asv.service.ws;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.controller.IBaseController;
import br.com.asv.model.enums.StatusEntityEnum;
import br.com.asv.model.exceptions.ObjectNotFoundException;
import br.com.asv.service.exception.IError;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseWs<D extends IBaseDto<I>, I> implements IBaseWs<D, I> {

	@Autowired
	@Getter
	private IError erroCapture;

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private IBaseController<D, I> service;

//	@Autowired
//	public ABaseWs(IBaseController<D, I> service) {
//		this.service = service;
//	}

	public Response<?, ?> initResponse() {
		return new Response<>();
	}
	
	public Response<D, ?> prepareResponse(D data) {
		@SuppressWarnings("unchecked")
		Response<D, ?> response = (Response<D, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<CountResponse, ?> prepareResponse(CountResponse data) {
		@SuppressWarnings("unchecked")
		Response<CountResponse, ?> response = (Response<CountResponse, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<List<D>, ?> prepareResponse(List<D> data) {
		@SuppressWarnings("unchecked")
		Response<List<D>, ?> response = (Response<List<D>, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<Collection<D>, ?> prepareResponse(Collection<D> data) {
		@SuppressWarnings("unchecked")
		Response<Collection<D>, ?> response = (Response<Collection<D>, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<Page<D>, ?> prepareResponse(Page<D> data) {
		@SuppressWarnings("unchecked")
		Response<Page<D>, ?> response = (Response<Page<D>, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<D, ?> prepareError(String strError) {
		Response<D, ?> response = new Response<>();
		response.setErrors(new LinkedList<String>());
		response.getErrors().add(strError);
		return response;
	}

	public Response<D, ?> prepareError(List<String> bindingResult) {
		Response<D, ?> response = new Response<>();
		response.setErrors(new LinkedList<String>());
		if (bindingResult != null) {
			for (int i = 0; i < bindingResult.size(); i++) {
				response.getErrors().add(bindingResult.get(i));
			}
		}
		return response;
	}

	public Response<D, ?> prepareError(BindingResult bindingResult) {
		Response<D, ?> response = new Response<>();
		if (bindingResult != null) {
			for (int i = 0; i < bindingResult.getAllErrors().size(); i++) {
				if (bindingResult.getAllErrors().get(i) != null) {
					response.getErrors().add(bindingResult.getAllErrors().get(i).getDefaultMessage());
				}
			}
		}
		return response;
	}

	public Page<D> convertToPage(List<D> listDto, Pageable pageable) throws IllegalArgumentException, Exception {
		int start = (int) pageable.getOffset();
		int end = (int) ((start + pageable.getPageSize()) > listDto.size() ? listDto.size()
				: (start + pageable.getPageSize()));

		return new PageImpl<>(listDto.subList(start, end), pageable, listDto.size());
	}

	@Override
	public ResponseEntity<?> findOne(I id) {
		try {
			return ResponseEntity.ok(prepareResponse(getService().findOne(id)));
		} catch (ObjectNotFoundException e) {
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAll(String search, HttpServletRequest request) {
		List<D> listResult = null;
		if (search == null) {
			listResult = getService().findAll();
		} else {
			listResult = getService().findAll(search);
		}
		if (listResult != null && listResult.size() > 0) {
			return ResponseEntity.ok(prepareResponse(listResult));
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<?> findAllEnabled() {
		try {
			Collection<D> result = getService().findAllByStatusEntity(StatusEntityEnum.ENABLED);
			if (result != null && result.size() > 0) {
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAll(String search, Pageable pageable, HttpServletRequest request) {
		try {
			List<D> listResult = null;
			if (search == null) {
				listResult = getService().findAll();
			} else {
				listResult = getService().findAll(search);
			}
			if (listResult != null && listResult.size() > 0) {
				Page<D> result = convertToPage(listResult, pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAllEnabled(Pageable pageable) {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.ENABLED);
//		Page<D> result = getService().findAllByStatusEntity(pageable, StatusEntityEnum.ENABLED);
			if (listResult != null && listResult.size() > 0) {
				Page<D> result = convertToPage(new ArrayList<>(listResult), pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAllDisabled(Pageable pageable) {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.DISABLED);
//		Page<D> result = getService().findAllByStatusEntity(pageable, StatusEntityEnum.DISABLED);

			if (listResult != null && listResult.size() > 0) {
				Page<D> result = convertToPage(new ArrayList<>(listResult), pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (

		ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAllDisabled() {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.DISABLED);
			if (listResult != null && listResult.size() > 0) {
				return ResponseEntity.ok(prepareResponse(listResult));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> saveImp(D dto, HttpServletRequest req, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(prepareError(result));
		}
		try {
			if (req.getHeader("Authorization") != null) {
				String token = req.getHeader("Authorization").replace("Bearer", "");
				System.out.println("#########################3");
				System.out.println(token);
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(dto)));
//		} catch (DuplicateKeyException e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	@Override
	public ResponseEntity<?> save(Collection<D> collection) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(collection)));
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> updateImp(D dto, HttpServletRequest req, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(prepareError(result));
		}
		try {
			if (req.getHeader("Authorization") != null) {
				String token = req.getHeader("Authorization").replace("Bearer", "");
				System.out.println("#########################3");
				System.out.println(token);
			}
		} catch (NullPointerException e) {
			// TODO: handle exception
		}
		try {
			return ResponseEntity.ok(prepareResponse(getService().update(dto)));
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> delete(Collection<D> collection) {
		try {
			getService().delete(collection);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> recovery(Collection<D> collection) {
		try {
			getService().recovery(collection);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> delete(I id) {
		try {
			getService().delete(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> recovery(I id) {
		try {
			getService().recovery(id);
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> findAllPage(String status, Pageable pageable) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(prepareResponse(getService().findAllByStatusEntity(pageable, statusEntityEnum)));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	@Override
	public ResponseEntity<?> findAllStatus(String status) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(prepareResponse(getService().findAllByStatusEntity(statusEntityEnum)));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	@Override
	public ResponseEntity<?> countAll(String search) {
		List<D> listResult = null;
		if (search == null) {
			listResult = getService().findAll();
		} else {
			listResult = getService().findAll(search);
		}
		CountResponse response;
		if (listResult != null) {
			response = new CountResponse(listResult.size());
		} else {
			response = new CountResponse(0);
		}
		return ResponseEntity.ok(prepareResponse(response));
	}

	@Override
	public ResponseEntity<?> remove(I id) {
		try {
			getService().remove(id);
			return ResponseEntity.noContent().build();
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(ex)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> remove(Collection<D> collection) {
		try {
			getService().remove(collection);
			return ResponseEntity.noContent().build();
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(ex)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<?> patchDto(I id, JsonPatch patch, HttpServletRequest req) {
		try {

			return ResponseEntity.ok(prepareResponse(getService().patch(id, patch)));
		} catch (JsonPatchException | JsonProcessingException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(ex)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}
	
	@Override
	public ResponseEntity<?> save(D dto, HttpServletRequest req, BindingResult result) {
		return saveImp(dto, req, result);
	}

	@Override
	public ResponseEntity<?> update(D dto, HttpServletRequest req, BindingResult result) {
		return updateImp(dto, req, result);
	}
}
