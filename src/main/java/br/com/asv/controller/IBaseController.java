package br.com.asv.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.model.enums.StatusEntityEnum;

public interface IBaseController<D extends IBaseDto<I>,I> {

	D findOne(I id);

	Collection<D> findAll();

	D save(@Valid D dto);
	Collection<D> save(Collection<D> models);
	
	D update(D model);

	Collection<D> findAllByStatusEntity(StatusEntityEnum enabled);
	Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity);
	
	void delete(I id);
	void delete(Collection<D> models);

	void recovery(I id);
	void recovery(Collection<D> models);
}
