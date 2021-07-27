package br.com.asv.service.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.model.enums.StatusEntityEnum;

public interface IBaseController<D extends IBaseDto<I>, I extends Serializable> {

	D findOne(I id) throws IllegalArgumentException;

	List<D> findAll() throws IllegalArgumentException;

	List<D> findAll(String search) throws IllegalArgumentException;

	D save(@Valid D dto);

	Collection<D> save(Collection<D> models);

	D update(D model) throws IllegalArgumentException;

	Collection<D> findAllByStatusEntity(StatusEntityEnum enabled) throws IllegalArgumentException;

	Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity);

	void delete(I id) throws IllegalArgumentException;

	void delete(Collection<D> models) throws IllegalArgumentException;

	void recovery(I id) throws IllegalArgumentException;

	void recovery(Collection<D> models) throws IllegalArgumentException;

	void remove(I pid) throws IllegalArgumentException;

	void remove(Collection<D> models) throws IllegalArgumentException;

	D patch(I id, JsonPatch patch) throws JsonPatchException, JsonProcessingException, IllegalArgumentException;

}
