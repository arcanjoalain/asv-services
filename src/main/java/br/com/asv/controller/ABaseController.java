package br.com.asv.controller;

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

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.model.daos.IBaseDao;
import br.com.asv.model.entities.IBaseEntity;
import br.com.asv.model.entities.history.IBaseHistoryEntity;
import br.com.asv.model.entities.history.IBaseHistoryListEntity;
import br.com.asv.model.enums.StatusEntityEnum;
import br.com.asv.model.parse.IBaseParse;
import lombok.AccessLevel;
import lombok.Getter;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public abstract class ABaseController<
		E extends IBaseEntity<I>, 
		R extends IBaseDao<E, I>, 
		D extends IBaseDto<I>, 
		I>
		implements IBaseController<D, I> {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private R dao;

	@Getter(AccessLevel.PROTECTED)
	private final String className;

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private IBaseParse<E, D, I> parseEntity;
	
	@Getter(AccessLevel.PROTECTED)
	private Class<E> clazzE;

//	@SuppressWarnings("unchecked")
//	@Autowired
//	public ABaseController(R dao, IBaseParse<E, D, I> parseEntity) {
//		this.dao = dao;
//		this.parseEntity = parseEntity;
//		this.className = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
//				.getActualTypeArguments()[0]).getSimpleName().toLowerCase();
//		 if (clazzE == null) {
//				clazzE = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
//						.getActualTypeArguments()[0];
//			}
//	}
	
	@SuppressWarnings("unchecked")
	@Autowired
	public ABaseController() {
		this.className = ((Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]).getSimpleName().toLowerCase();
		 if (clazzE == null) {
				clazzE = (Class<E>) ((ParameterizedType) getClass().getGenericSuperclass())
						.getActualTypeArguments()[0];
			}
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
		D result = null;
		if(entity!=null) {
			result = parseEntity.toDTO(entity,Boolean.TRUE);
		}
		return result;
	}

	public List<D> findAll() {
		return (List<D>) StreamSupport.stream(getDao().findAll().spliterator(), Boolean.FALSE)
				.map(e->parseFindAll(e))
				.collect(Collectors.toList());
	}
	
	public List<D> findAll(String search) {
		return (List<D>) StreamSupport.stream(getDao().findAll(search).spliterator(), Boolean.FALSE)
				.map(e->parseFindAll(e))
				.collect(Collectors.toList());
	}
	
	public D parseFindAll(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public D save(@Valid D dto) {
		return parseSave(getDao().save(parseEntity.toModel(dto,Boolean.TRUE)));
	}
	
	public D parseSave(E entity) {
		return parseUnique(entity) ;
	}

	@Override
	public Collection<D> save(Collection<D> models) {
		Collection<E> entitys = (Collection<E>) models.stream()
				.map(dto->parseEntity.toModel(dto)).collect(Collectors.toList());
		return (Collection<D>) StreamSupport.stream(getDao().save(entitys).spliterator(), false)
				.map(e-> parseSave(e))
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
		Collection<E> result = getDao()
				.findAllByStatusEntitySortByPid(statusEntity);
		return new PageImpl<D>((List<D>) result.stream().map(e -> parseAllByStatusEntityPage(e))
				.collect(Collectors.toList()),pageable,pageable.getPageSize());
	}
	
	public D parseAllByStatusEntityPage(E entity) {
		return parseUnique(entity) ;
	}
	
	@Override
	public void remove(I id) {
		 getDao().remove(id);

	}

	@Override
	public void remove(Collection<D> models) {
		models.forEach(item -> remove(item.getPid()));

	}
	
	@Override
	public D patch(I id, JsonPatch patch) throws JsonPatchException, JsonProcessingException{
		E entity = getDao().patch(id, patch);
		return parsePatch(entity);
	}
	
	public D parsePatch(E entity) {
		return parseUnique(entity) ;
	}

}
