package br.com.asv.controller;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import br.com.asv.model.daos.IBaseDao;
import br.com.asv.model.dtos.IBaseDto;
import br.com.asv.model.entities.IBaseEntity;
import br.com.asv.model.enums.StatusEntityEnum;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseController<E extends IBaseEntity, R extends IBaseDao<E>, D extends IBaseDto>
		implements IBaseController<D> {
	@Getter(AccessLevel.PROTECTED)
	private final R dao;

	@Getter(AccessLevel.PROTECTED)
	private final String className;

	@SuppressWarnings("unchecked")
	@Autowired
	public ABaseController(R dao) {
		this.dao = dao;
		this.className = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]).getSimpleName().toLowerCase();
	}
	
	@SuppressWarnings("unchecked")
	public D findOne(Long id) {
		return (D) getDao().findOne(id).toDTO();
	}
	
	@SuppressWarnings("unchecked")
	public List<D> findAll(){
		return  (List<D>) StreamSupport
				.stream(getDao().findAll().spliterator(), false).map(E::toDTO).collect(Collectors.toList());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public D save(@Valid D dto) {
		return  (D) getDao().save((E) dto.toModel()).toDTO();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection<D> save(Collection<D> models) {
		Collection<E> entitys = (Collection<E>) models.stream().map(D::toModel).collect(Collectors.toList());
		return (Collection<D>) StreamSupport.stream(getDao().save(entitys).spliterator(), false).map(E::toDTO).collect(Collectors.toList());
	}

	@SuppressWarnings("unchecked")
	@Override
	public D update(D model) {
		return (D) getDao().update((E) model.toModel()).toDTO();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection<D> findAllByStatusEntity(StatusEntityEnum statusEntity) {
		return (Collection<D>) getDao().findAllByStatusEntity(statusEntity).stream().map(E::toDTO).collect(Collectors.toList());
	}

	@Override
	public void delete(Long id) {
		getDao().delete(id);
		
	}

	@Override
	public void delete(Collection<D> models) {
		models.forEach(item -> delete(item.getPid()));
		
	}

	@Override
	public void recovery(Long id) {
		getDao().recovery(id);
		
	}

	@Override
	public void recovery(Collection<D> models) {
		 models.forEach(item -> recovery(item.getPid()));
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity) {
		return new PageImpl<D>((List<D>) getDao().findAllByStatusEntity(pageable, statusEntity).stream().map(E::toDTO).collect(Collectors.toList()));
	}
}
