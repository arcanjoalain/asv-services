package br.com.asv.service.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.client.ws.IResponse;
import br.com.asv.base.client.ws.Response;
import br.com.asv.service.ws.CountResponse;

public interface ICoreWs<D,I extends Serializable> {

	Response<D, ?, String> prepareResponse(D data);

	Response<CountResponse<?>, ?, String> prepareResponse(CountResponse<?> data);

	Response<?, ?, String> initResponse();

	ResponseEntity<IResponse> prepareListResult(IDelegateListWs<D> iDelegate);

	ResponseEntity<IResponse> prepareListResult(Collection<D> listResult);

	Page<D> convertToPage(List<D> listDto, Pageable pageable) throws IllegalArgumentException;

	Response<IBaseDto<I>, Serializable, String> prepareError(BindingResult bindingResult);

	Response<D, ?, String> prepareError(List<String> bindingResult);

	Response<D, ?, String> prepareError(String strError);

	Response<Page<D>, ?, String> prepareResponse(Page<D> data);

	Response<Collection<D>, ?, String> prepareResponse(Collection<D> data);

	Response<List<D>, ?, String> prepareResponse(List<D> data);

}
