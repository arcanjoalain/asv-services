package br.com.asv.service.ws;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

import com.github.fge.jsonpatch.JsonPatch;

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.client.ws.IWSClient;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseClientWs<C extends IWSClient<D, I>,D extends IBaseDto<I>, I> implements IBaseWs<D, I> {

	@Autowired
	@Getter(AccessLevel.PROTECTED)
	private C service;
	
	@Override
	public ResponseEntity<?> findOne(I id) {
		return getService().findOne(id);
	}

	@Override
	public ResponseEntity<?> findAll(String search, HttpServletRequest request) {
		return getService().findAll(search);
	}

	@Override
	public ResponseEntity<?> findAllEnabled() {
		return getService().findAllEnabled();
	}

	@Override
	public ResponseEntity<?> findAll(String search, Pageable pageable, HttpServletRequest request) {
		return getService().findAll(search, pageable);
	}

	@Override
	public ResponseEntity<?> findAllEnabled(Pageable pageable) {
		return getService().findAllEnabled(pageable);
	}

	@Override
	public ResponseEntity<?> findAllDisabled(Pageable pageable) {
		return getService().findAllDisabled(pageable);
	}

	@Override
	public ResponseEntity<?> findAllDisabled() {
		return getService().findAllDisabled();
	}
	
	@Override
	public ResponseEntity<?> saveImp(D dto, HttpServletRequest req, BindingResult result) {
		return getService().save(dto);
	}

	@Override
	public ResponseEntity<?> save(Collection<D> collection) {
		return getService().save(collection);
	}
	
	@Override
	public ResponseEntity<?> updateImp(D dto, HttpServletRequest req, BindingResult result) {
		return getService().update(dto);
	}

	@Override
	public ResponseEntity<?> delete(Collection<D> collection) {
		return getService().delete(collection);
	}

	@Override
	public ResponseEntity<?> recovery(Collection<D> collection) {
		return getService().recovery(collection);
	}

	@Override
	public ResponseEntity<?> delete(I id) {
		return getService().delete(id);
	}

	@Override
	public ResponseEntity<?> recovery(I id) {
		return getService().recovery(id);
	}

	@Override
	public ResponseEntity<?> findAllPage(String status, Pageable pageable) {
		return getService().findAllPage(status, pageable);

	}

	@Override
	public ResponseEntity<?> findAllStatus(String status) {
		return getService().findAllStatus(status);

	}

	@Override
	public ResponseEntity<?> countAll(String search) {
		return getService().countAll(search);
	}

	@Override
	public ResponseEntity<?> remove(I id) {
		return getService().remove(id);
	}

	@Override
	public ResponseEntity<?> remove(Collection<D> collection) {
		return getService().remove(collection);
	}

	@Override
	public ResponseEntity<?> patchDto(I id, JsonPatch patch, HttpServletRequest req) {
		return getService().patchDto(id, patch);
	}
	
	@Override
	public ResponseEntity<?> save(D dto, HttpServletRequest req, BindingResult result) {
		return saveImp(dto, req, result);
	}

	@Override
	public ResponseEntity<?> update(D dto, HttpServletRequest req, BindingResult result) {
		return updateImp(dto, req, result);
	}
}
