package br.com.asv.service.controller;

import java.io.Serializable;
import java.util.List;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.model.entities.IBaseEntity;

public interface ICoreController<E extends IBaseEntity<I>, 
D extends IBaseDto<O>, 
I extends Serializable, 
O extends Serializable> {

	D parseUnique(E entity) ;
	D parseFindOne(E entity);
	D parseFindAll(E entity);
	D parseSave(E entity);
	D parseUpdate(E entity) ;
	D parseAllByStatusEntity(E entity) ;
	D parseAllByStatusEntityPage(E entity) ;
	D parsePatch(E entity) ;
	List<D> parse(List<E> listE);
}
