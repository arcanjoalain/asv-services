package br.com.asv.service.ws;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.client.ws.IResponse;
import br.com.asv.base.client.ws.Response;
import br.com.asv.base.model.enums.StatusEntityEnum;
import br.com.asv.base.model.exceptions.ObjectNotFoundException;
import br.com.asv.service.controller.IBaseController;
import br.com.asv.service.exception.ErroRest;
import br.com.asv.service.exception.IError;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseWs<D extends IBaseDto<I>, I extends Serializable> implements IBaseWs<D, I> {

//	@Autowired
	@Getter
	private IError erroCapture = new ErroRest();

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private IBaseController<D, I> service;

	public Response<?, ?, String> initResponse() {
		return new Response<>();
	}
	
	public Response<D, ?, String> prepareResponse(D data) {
		@SuppressWarnings("unchecked")
		Response<D, ?, String> response = (Response<D, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<CountResponse<?>, ?, String> prepareResponse(CountResponse<?> data) {
		@SuppressWarnings("unchecked")
		Response<CountResponse<?>, ?, String> response = (Response<CountResponse<?>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<List<D>, ?, String> prepareResponse(List<D> data) {
		@SuppressWarnings("unchecked")
		Response<List<D>, ?, String> response = (Response<List<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<Collection<D>, ?, String> prepareResponse(Collection<D> data) {
		@SuppressWarnings("unchecked")
		Response<Collection<D>, ?, String> response = (Response<Collection<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<Page<D>, ?, String> prepareResponse(Page<D> data) {
		@SuppressWarnings("unchecked")
		Response<Page<D>, ?, String> response = (Response<Page<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<D, ?, String> prepareError(String strError) {
		Response<D, ?, String> response = new Response<>();
		response.setErrors(new LinkedList<>());
		response.getErrors().add(strError);
		return response;
	}

	public Response<D, ?, String> prepareError(List<String> bindingResult) {
		Response<D, ?, String> response = new Response<>();
		response.setErrors(new LinkedList<>());
		if (bindingResult != null) {
			for (int i = 0; i < bindingResult.size(); i++) {
				response.getErrors().add(bindingResult.get(i));
			}
		}
		return response;
	}

	public Response<IBaseDto<I>, Serializable, String> prepareError(BindingResult bindingResult) {
		Response<IBaseDto<I>, Serializable, String> response = new Response<>();
		if (bindingResult != null) {
			for (int i = 0; i < bindingResult.getAllErrors().size(); i++) {
				if (bindingResult.getAllErrors().get(i) != null) {
					response.getErrors().add(bindingResult.getAllErrors().get(i).getDefaultMessage());
				}
			}
		}
		return response;
	}

	public Page<D> convertToPage(List<D> listDto, Pageable pageable) throws IllegalArgumentException {
		int start = (int) pageable.getOffset();
		int end =  start + pageable.getPageSize() > listDto.size() ? listDto.size()
				: start + pageable.getPageSize();

		return new PageImpl<>(listDto.subList(start, end), pageable, listDto.size());
	}

	@Override
	public ResponseEntity<IResponse> findOne(I id) {
		try {
			return ResponseEntity.ok(prepareResponse(getService().findOne(id)));
		} catch (ObjectNotFoundException e) {
			return ResponseEntity.noContent().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAll(String search, HttpServletRequest request) {
		List<D> listResult = null;
		if (search == null) {
			listResult = getService().findAll();
		} else {
			listResult = getService().findAll(search);
		}
		if (listResult != null && !listResult.isEmpty()) {
			return ResponseEntity.ok(prepareResponse(listResult));
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<IResponse> findAllEnabled() {
		try {
			Collection<D> result = getService().findAllByStatusEntity(StatusEntityEnum.ENABLED);
			if (result != null && !result.isEmpty()) {
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAll(String search, Pageable pageable, HttpServletRequest request) {
		try {
			List<D> listResult = null;
			if (search == null) {
				listResult = getService().findAll();
			} else {
				listResult = getService().findAll(search);
			}
			if (listResult != null && !listResult.isEmpty()) {
				Page<D> result = convertToPage(listResult, pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAllEnabled(Pageable pageable) {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.ENABLED);
			if (listResult != null && !listResult.isEmpty()) {
				Page<D> result = convertToPage(new ArrayList<>(listResult), pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAllDisabled(Pageable pageable) {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.DISABLED);

			if (listResult != null && !listResult.isEmpty()) {
				Page<D> result = convertToPage(new ArrayList<>(listResult), pageable);
				return ResponseEntity.ok(prepareResponse(result));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAllDisabled() {
		try {
			Collection<D> listResult = null;
			listResult = getService().findAllByStatusEntity(StatusEntityEnum.DISABLED);
			if (listResult != null && !listResult.isEmpty()) {
				return ResponseEntity.ok(prepareResponse(listResult));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

//	@Override
//	public ResponseEntity<IResponse> saveImp(D dto, HttpServletRequest req, BindingResult result) {
//		System.out.println("save###########");
//		try {
//			if (result.hasErrors()) {
//				return ResponseEntity.badRequest().body(prepareError(result));
//			}
//			
//			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(dto)));
//		} catch (DuplicateKeyException e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).build();
//		} catch (ConstraintViolationException e) {
//			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
//		}
//
//	}

	@Override
	public ResponseEntity<IResponse> save(Collection<D> collection) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(collection)));
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

//	@Override
//	public ResponseEntity<IResponse> updateImp(D dto, HttpServletRequest req, BindingResult result) {
//		
//		try {
//			if (result.hasErrors()) {
//				return ResponseEntity.badRequest().body(prepareError(result));
//			}
//			return ResponseEntity.ok(prepareResponse(getService().update(dto)));
//		} catch (ConstraintViolationException e) {
//			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
//		}
//	}

	@Override
	public ResponseEntity<IResponse> delete(Collection<D> collection) {
		try {
			getService().delete(collection);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> recovery(Collection<D> collection) {
		try {
			getService().recovery(collection);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> delete(I id) {
		try {
			getService().delete(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> recovery(I id) {
		try {
			getService().recovery(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> findAllPage(String status, Pageable pageable) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(prepareResponse(getService().findAllByStatusEntity(pageable, statusEntityEnum)));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	@Override
	public ResponseEntity<IResponse> findAllStatus(String status) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(prepareResponse(getService().findAllByStatusEntity(statusEntityEnum)));
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	@Override
	public ResponseEntity<IResponse> countAll(String search) {
		List<D> listResult = null;
		if (search == null) {
			listResult = getService().findAll();
		} else {
			listResult = getService().findAll(search);
		}
		CountResponse<Integer> response;
		if (listResult != null) {
			response = new CountResponse<>(listResult.size());
		} else {
			response = new CountResponse<>(0);
		}
		return ResponseEntity.ok(prepareResponse(response));
	}

	@Override
	public ResponseEntity<IResponse> remove(I id) {
		try {
			getService().remove(id);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(ex)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> remove(Collection<D> collection) {
		try {
			getService().remove(collection);
			return ResponseEntity.noContent().build();
		} catch (IllegalArgumentException ex) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(ex)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> patchDto(I id, JsonPatch patch, HttpServletRequest req) {
		try {

			return ResponseEntity.ok(prepareResponse(getService().patch(id, patch)));
		} catch (JsonPatchException | JsonProcessingException | IllegalArgumentException  e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}
	
	
	@Override
	public ResponseEntity<IResponse> save(D dto, HttpServletRequest req, BindingResult result) {
		try {
			if (result.hasErrors()) {
				return ResponseEntity.badRequest().body(prepareError(result));
			}
			
			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(dto)));
		} catch (DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> update(D dto, HttpServletRequest req, BindingResult result) {
		try {
			if (result.hasErrors()) {
				return ResponseEntity.badRequest().body(prepareError(result));
			}
			
			return ResponseEntity.status(HttpStatus.CREATED).body(prepareResponse(getService().save(dto)));
		} catch (DuplicateKeyException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}
	
	
	public ResponseEntity<IResponse> prepareListResult(Collection<D> listResult) {
		try {
			if (listResult != null && !listResult.isEmpty()) {
				return ResponseEntity.ok(prepareResponse(listResult));
			} else {
				return ResponseEntity.noContent().build();
			}
		} catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(prepareError(getErroCapture().exceptionString(e)));
		}
	}
}
