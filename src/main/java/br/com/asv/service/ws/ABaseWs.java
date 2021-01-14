package br.com.asv.service.ws;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.controller.IBaseController;
import br.com.asv.model.enums.StatusEntityEnum;
import br.com.asv.service.exception.IError;
import lombok.AccessLevel;
import lombok.Getter;


public abstract class ABaseWs<D extends IBaseDto<I>, I> implements IBaseWs<D, I> {
	
	@Autowired
	@Getter
	private IError erroCapture;

	@Getter(AccessLevel.PROTECTED)
	private final IBaseController<D, I> service;

	@Autowired
	public ABaseWs(IBaseController<D, I> service) {
		this.service = service;
	}

	@Override
	public ResponseEntity<D> findOne(@PathVariable("id") I id) {
		return ResponseEntity.ok(getService().findOne(id));
	}

	@Override
	public ResponseEntity<Collection<D>> findAll(@RequestParam(value = "search", required = false) String search,
			HttpServletRequest request) {
		return ResponseEntity.ok(getService().findAll());
	}

	@Override
	public ResponseEntity<?> findAll(@RequestParam(value = "search", required = false) String search, Pageable pageable,
			HttpServletRequest request) {
		int size = 10;
		if (pageable != null) {
			size = pageable.getPageSize();
		}
		List<D> listResult = null;
		if (search == null) {
			listResult = getService().findAll();
		} else {
			listResult = getService().findAll(search);
		}
		if (listResult != null) {
			Page<D> result = new PageImpl<>(listResult, pageable, size);
			return ResponseEntity.ok(prepareResponse(result));
		} else {
			return ResponseEntity.noContent().build();
		}
	}

	@Override
	public ResponseEntity<Collection<D>> findAllEnabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.ENABLED));
	}

	@Override
	public ResponseEntity<?> findAllEnabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.ENABLED));
	}

	@Override
	public ResponseEntity<Collection<D>> findAllDisabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.DISABLED));
	}

	@Override
	public ResponseEntity<?> findAllDisabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.DISABLED));
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

	public Response<?, ?> initResponse() {
		return new Response<>();
	}

	public Response<?, ?> prepareResponse(D data) {
		@SuppressWarnings("unchecked")
		Response<D, ?> response = (Response<D, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<List<D>, ?> prepareResponse(List<D> data) {
		@SuppressWarnings("unchecked")
		Response<List<D>, ?> response = (Response<List<D>, ?>) initResponse();
		response.setData(data);
		return response;
	}

	public Response<Page<D>, ?> prepareResponse(Page<D> data) {
		@SuppressWarnings("unchecked")
		Response<Page<D>, ?> response = (Response<Page<D>, ?>) initResponse();
		response.setData(data);
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

	public ResponseEntity<?> saveImp(@RequestBody @Valid D dto, HttpServletRequest req, BindingResult result) {
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
			return ResponseEntity.status(HttpStatus.CREATED).body(getService().save(dto));
//		} catch (DuplicateKeyException e) {
//			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}catch (ConstraintViolationException e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
		} catch (Exception e) {
			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
		}

	}

	public ResponseEntity<?> updateImp(@RequestBody @Valid D dto, HttpServletRequest req, BindingResult result) {
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
		return ResponseEntity.ok(getService().update(dto));
	}

	@Override
	public ResponseEntity<Collection<D>> save(@RequestBody @Valid Collection<D> collection) {
		return ResponseEntity.ok(getService().save(collection));
	}

	@Override
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection) {
		getService().delete(collection);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<Collection<D>> recovery(@RequestBody @Valid Collection<D> collection) {
		getService().recovery(collection);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<?> delete(@PathVariable("id") I id) {
		getService().delete(id);
		return ResponseEntity.noContent().build();
	}

	@Override
	public ResponseEntity<?> recovery(@PathVariable("id") I id) {
		getService().recovery(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> findAllPage(String status, Pageable pageable) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, statusEntityEnum));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
	public ResponseEntity<Collection<D>> findAllStatus(String status) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(getService().findAllByStatusEntity(statusEntityEnum));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}

	}

	@Override
	public ResponseEntity<?> countAll(@RequestParam(value = "search", required = false) String search) {
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
		return ResponseEntity.ok(response);
	}

	@Override
	public void remove(@PathVariable("id") I id) {
		getService().remove(id);

	}
	
	@Override
	public void remove(@RequestBody Collection<D> collection) {
		getService().remove(collection);

	}

}
