package br.com.asv.service.ws;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.service.controller.IBaseController;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ACBaseWs<C extends IBaseController<D, I>, D extends IBaseDto<I>, I extends Serializable>
		extends ABaseWs<D, I> {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private C service;
}
