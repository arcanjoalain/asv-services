package br.com.asv.controller;

import java.util.Collection;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.model.enums.StatusEntityEnum;

public interface IBaseController<D extends IBaseDto<I>,I> {

	D findOne(I id);

	List<D> findAll();
	List<D> findAll(String search);

	D save(@Valid D dto);
	Collection<D> save(Collection<D> models);
	
	D update(D model);

	Collection<D> findAllByStatusEntity(StatusEntityEnum enabled);
	Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity);
	
	void delete(I id);
	void delete(Collection<D> models);

	void recovery(I id);
	void recovery(Collection<D> models);
	
	void remove(I pid);
	void remove(Collection<D> models);
	
	public D patch(I id, JsonPatch patch) throws JsonPatchException, JsonProcessingException ;
}
