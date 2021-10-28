package br.com.asv.service.ws;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.client.ws.IResponse;
import br.com.asv.base.client.ws.Response;
import br.com.asv.service.controller.ICoreWs;
import br.com.asv.service.controller.IDelegateListWs;
import br.com.asv.service.exception.ErroRest;
import br.com.asv.service.exception.IError;
import lombok.Getter;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ACoreWs<D, I extends Serializable> implements ICoreWs<D, I> {

	@Getter
	private IError erroCapture = new ErroRest();

	@Override
	public Response<?, ?, String> initResponse() {
		return new Response<>();
	}

	@Override
	public Response<D, ?, String> prepareResponse(D data) {
		@SuppressWarnings("unchecked")
		Response<D, ?, String> response = (Response<D, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	@Override
	public Response<CountResponse<?>, ?, String> prepareResponse(CountResponse<?> data) {
		@SuppressWarnings("unchecked")
		Response<CountResponse<?>, ?, String> response = (Response<CountResponse<?>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	@Override
	public Response<List<D>, ?, String> prepareResponse(List<D> data) {
		@SuppressWarnings("unchecked")
		Response<List<D>, ?, String> response = (Response<List<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	@Override
	public Response<Collection<D>, ?, String> prepareResponse(Collection<D> data) {
		@SuppressWarnings("unchecked")
		Response<Collection<D>, ?, String> response = (Response<Collection<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	@Override
	public Response<Page<D>, ?, String> prepareResponse(Page<D> data) {
		@SuppressWarnings("unchecked")
		Response<Page<D>, ?, String> response = (Response<Page<D>, ?, String>) initResponse();
		response.setData(data);
		return response;
	}

	@Override
	public Response<D, ?, String> prepareError(String strError) {
		Response<D, ?, String> response = new Response<>();
		response.setErrors(new LinkedList<>());
		response.getErrors().add(strError);
		return response;
	}

	@Override
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

	@Override
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

	@Override
	public Page<D> convertToPage(List<D> listDto, Pageable pageable) throws IllegalArgumentException {
		int start = (int) pageable.getOffset();
		int end = start + pageable.getPageSize() > listDto.size() ? listDto.size() : start + pageable.getPageSize();

		return new PageImpl<>(listDto.subList(start, end), pageable, listDto.size());
	}

	@Override
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(prepareError(getErroCapture().exceptionString(e)));
		}
	}

	@Override
	public ResponseEntity<IResponse> prepareListResult(IDelegateListWs<D> iDelegate) {
//		try {
			List<D> listResult = iDelegate.generateList();
			return prepareListResult(listResult);
			
//		} catch (ConstraintViolationException e) {
//			return ResponseEntity.badRequest().body(prepareError(getErroCapture().contraintViolationString(e)));
//		} catch (IllegalArgumentException e) {
//			return ResponseEntity.badRequest().body(prepareError(getErroCapture().exceptionString(e)));
//		} catch (Exception e) {
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//					.body(prepareError(getErroCapture().exceptionString(e)));
//		}
	}
}
