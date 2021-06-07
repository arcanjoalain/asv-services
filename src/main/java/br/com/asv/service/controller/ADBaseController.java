package br.com.asv.service.controller;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Autowired;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.model.daos.IBasePatchDao;
import br.com.asv.base.model.entities.IBaseEntity;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ADBaseController<
	M extends IBasePatchDao<E,I>,
	E extends IBaseEntity<I>, 
	D extends IBaseDto<O>, 
	I extends Serializable, 
	O extends Serializable>
		extends ABaseController<E, D, I, O> {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private M dao;
}
