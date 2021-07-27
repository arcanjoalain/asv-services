package br.com.asv.service.controller;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.model.daos.IBasePatchDao;
import br.com.asv.base.model.entities.IBaseEntity;
import br.com.asv.base.model.enums.StatusEntityEnum;
import br.com.asv.base.model.parse.IBaseParse;
import lombok.AccessLevel;
import lombok.Getter;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABaseController<
	E extends IBaseEntity<I>, 
	D extends IBaseDto<O>, 
	I extends Serializable, 
	O extends Serializable>
		implements 
			IBaseController<D, O>, 
			ICoreController<E, D, I, O> {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private IBasePatchDao<E, I> dao;

	@Getter(AccessLevel.PROTECTED)
	private final String className;

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private IBaseParse<E, D, I, O> parse;

	@Getter(AccessLevel.PROTECTED)
	private Class<E> clazzE;

	@SuppressWarnings("unchecked")
	@Autowired
	public ABaseController() {
		this.className = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]).getSimpleName().toLowerCase();
		if (clazzE == null) {
			clazzE = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		}
	}

	@Override
	public D parseUnique(E entity) {
		D result = null;
		if (entity != null) {
			result = getParse().convert(entity);
		}
		return result;
	}

	@Override
	public D findOne(O id) throws IllegalArgumentException {
		return parseFindOne(getDao().findOne(getParse().convertPidEntity(id)));
	}

	@Override
	public D parseFindOne(E entity) {
		D result = null;
		if (entity != null) {
			result = getParse().convert(entity, Boolean.TRUE);
		}
		return result;
	}

	@Override
	public List<D> findAll() {
		return (List<D>) StreamSupport.stream(getDao().findAll().spliterator(), Boolean.FALSE).map(e -> parseFindAll(e))
				.collect(Collectors.toList());
	}

	@Override
	public List<D> findAll(String search) throws IllegalArgumentException {
		return (List<D>) StreamSupport.stream(getDao().findAll(search).spliterator(), Boolean.FALSE)
				.map(e -> parseFindAll(e)).collect(Collectors.toList());
	}

	@Override
	public D parseFindAll(E entity) {
		return parseUnique(entity);
	}

	@Override
	public D save(@Valid D dto) {
		return parseSave(getDao().save(getParse().convert(dto, Boolean.TRUE)));
	}

	@Override
	public D parseSave(E entity) {
		return parseUnique(entity);
	}

	@Override
	public Collection<D> save(Collection<D> models) {
		Collection<E> entitys = (Collection<E>) models.stream().map(dto -> getParse().convert(dto))
				.collect(Collectors.toList());
		return (Collection<D>) StreamSupport.stream(getDao().save(entitys).spliterator(), false).map(e -> parseSave(e))
				.collect(Collectors.toList());
	}

	@Override
	public D update(D model) throws IllegalArgumentException {

		E entity = parse.convert(model);
		E entityLast = getDao().findOne(entity.getPid());
		entity.setCreateUserPid(entityLast.getCreateUserPid());
		entity.setCreatedAt(entityLast.getCreatedAt());
		return parseUpdate(getDao().update(entity));
	}

	@Override
	public D parseUpdate(E entity)  {
		return parseUnique(entity);
	}

	@Override
	public Collection<D> findAllByStatusEntity(StatusEntityEnum statusEntity) throws IllegalArgumentException {
		return (Collection<D>) getDao().findAllByStatusEntity(statusEntity).stream().map(e -> parseAllByStatusEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public D parseAllByStatusEntity(E entity)  {
		return parseUnique(entity);
	}

	@Override
	public void delete(O id) throws IllegalArgumentException {
		getDao().delete(getParse().convertPidEntity(id));

	}

	@Override
	public void delete(Collection<D> models) throws IllegalArgumentException {
		models.forEach(item -> delete(item.getPid()));

	}

	@Override
	public void recovery(O id) throws IllegalArgumentException {
		getDao().recovery(getParse().convertPidEntity(id));

	}

	@Override
	public void recovery(Collection<D> models) throws IllegalArgumentException {
		models.forEach(item -> recovery(item.getPid()));

	}

	@Override
	public Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity) {
		Collection<E> result = getDao().findAllByStatusEntitySortByPid(statusEntity);
		return new PageImpl<>(result.stream().map(e -> parseAllByStatusEntityPage(e)).collect(Collectors.toList()),
				pageable, pageable.getPageSize());
	}

	@Override
	public D parseAllByStatusEntityPage(E entity) {
		return parseUnique(entity);
	}

	@Override
	public void remove(O id) throws IllegalArgumentException {
		getDao().remove(getParse().convertPidEntity(id));

	}

	@Override
	public void remove(Collection<D> models) throws IllegalArgumentException {
		models.forEach(item -> remove(item.getPid()));

	}

	@Override
	public D patch(O id, JsonPatch patch) throws JsonPatchException, JsonProcessingException, IllegalArgumentException {
		E entity = getDao().patch(getParse().convertPidEntity(id), patch);
		return parsePatch(entity);
	}

	@Override
	public D parsePatch(E entity) {
		return parseUnique(entity);
	}

	@Override
	public List<D> parse(List<E> listE) {
		return listE.stream().map(e -> parseAllByStatusEntity(e)).collect(Collectors.toList());
	}

}
