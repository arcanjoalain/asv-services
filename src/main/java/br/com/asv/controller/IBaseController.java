package br.com.asv.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import br.com.asv.model.dtos.IBaseDto;
import br.com.asv.model.enums.StatusEntityEnum;

public interface IBaseController<D extends IBaseDto> {

	D findOne(Long id);

	Collection<D> findAll();

	D save(@Valid D dto);
	Collection<D> save(Collection<D> models);
	
	D update(D model);

	Collection<D> findAllByStatusEntity(StatusEntityEnum enabled);
	Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity);
	
	void delete(Long id);
	void delete(Collection<D> models);

	void recovery(Long id);
	void recovery(Collection<D> models);
}
