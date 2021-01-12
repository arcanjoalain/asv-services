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

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.model.daos.IBaseDao;
import br.com.asv.model.entities.IBaseEntity;
import br.com.asv.model.entities.history.IBaseHistoryEntity;
import br.com.asv.model.entities.history.IBaseHistoryListEntity;
import br.com.asv.model.enums.StatusEntityEnum;
import br.com.asv.model.parse.IParseEntity;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseController<E extends IBaseEntity<I>, R extends IBaseDao<E, I>, D extends IBaseDto<I>, I>
		implements IBaseController<D, I> {

	@Getter(AccessLevel.PROTECTED)
	private final R dao;

	@Getter(AccessLevel.PROTECTED)
	private final String className;

	@Getter(AccessLevel.PROTECTED)
	private IParseEntity<E, D, I> parseEntity;

	@SuppressWarnings("unchecked")
	@Autowired
	public ABaseController(R dao, IParseEntity<E, D, I> parseEntity) {
		this.dao = dao;
		this.parseEntity = parseEntity;
		this.className = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]).getSimpleName().toLowerCase();
	}

	public D parseUnique(E entity) {
		D result = null;
		if(entity!=null) {
			result = parseEntity.toDTO(entity);
		}
		return result;
	}
	
	public D findOne(I id) {
		return parseFindOne(getDao().findOne(id));
	}	
	
	public D parseFindOne(E entity) {
		return parseUnique(entity) ;
	}

	public List<D> findAll() {
		return (List<D>) StreamSupport.stream(getDao().findAll().spliterator(), false).map(e->parseFindAll(e))
				.collect(Collectors.toList());
	}
	
	public D parseFindAll(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public D save(@Valid D dto) {
		return parseSave(getDao().save(parseEntity.toModel(dto)));
	}
	
	public D parseSave(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public Collection<D> save(Collection<D> models) {
		Collection<E> entitys = (Collection<E>) models.stream().map(dto->parseEntity.toModel(dto)).collect(Collectors.toList());
		return (Collection<D>) StreamSupport.stream(getDao().save(entitys).spliterator(), false).map(e-> parseSave(e))
				.collect(Collectors.toList());
	}

	@Override
	public D update(D model) {

		E entity = parseEntity.toModel(model);
		E entityLast = getDao().findOne(entity.getPid());
		entity.setCreateUserPid(entityLast.getCreateUserPid());
		entity.setCreatedAt(entityLast.getCreatedAt());
		if (entity instanceof IBaseHistoryEntity) {
		}

		if (entity instanceof IBaseHistoryListEntity) {
//			((IBaseHistoryListEntity)entity).setHistories(((IBaseHistoryListEntity)entityLast).getHistories());
		}

		return parseUpdate(getDao().update(entity));
	}
	
	public D parseUpdate(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public Collection<D> findAllByStatusEntity(StatusEntityEnum statusEntity) {
		return (Collection<D>) getDao().findAllByStatusEntity(statusEntity).stream().map(e -> parseAllByStatusEntity(e) )
				.collect(Collectors.toList());
	}
	
	public D parseAllByStatusEntity(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public void delete(I id) {
		getDao().delete(id);

	}

	@Override
	public void delete(Collection<D> models) {
		models.forEach(item -> delete(item.getPid()));

	}

	@Override
	public void recovery(I id) {
		getDao().recovery(id);

	}

	@Override
	public void recovery(Collection<D> models) {
		models.forEach(item -> recovery(item.getPid()));

	}

	@Override
	public Page<D> findAllByStatusEntity(Pageable pageable, StatusEntityEnum statusEntity) {
		return new PageImpl<D>((List<D>) getDao().findAllByStatusEntity(pageable, statusEntity).stream().map(e -> parseAllByStatusEntityPage(e))
				.collect(Collectors.toList()));
	}
	
	public D parseAllByStatusEntityPage(E entity) {
		return parseUnique(entity) ;
	}
}
