package br.com.asv.service.ws;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.asv.client.dto.IBaseDto;
import br.com.asv.controller.IBaseController;
import br.com.asv.model.enums.StatusEntityEnum;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseWs<D extends IBaseDto<I>, I> implements IBaseWs<D, I> {

	@Getter(AccessLevel.PROTECTED)
	private final IBaseController<D, I> service;

	@Autowired
	public ABaseWs(IBaseController<D, I> service) {
		this.service = service;
	}

	@Override
//	@GetMapping(path = "/{id}")
	public ResponseEntity<D> findOne(@PathVariable("id") I id) {
		return ResponseEntity.ok(getService().findOne(id));
	}

	@Override
//	@ApiResponse
//	@GetMapping
	public ResponseEntity<Collection<D>> findAll() {
		return ResponseEntity.ok(getService().findAll());
	}

	@Override
//	@ApiResponse
//	@GetMapping(path = "/page")
	public ResponseEntity<Collection<D>> findAll(Pageable pageable) {
		return ResponseEntity.ok(getService().findAll());
	}

	@Override
//	@GetMapping(path = "/enabled")
	public ResponseEntity<Collection<D>> findAllEnabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.ENABLED));
	}

	@Override
//	@GetMapping(path = "/enabled/page")
	public ResponseEntity<?> findAllEnabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.ENABLED));
	}

	@Override
//	@GetMapping(path = "/disabled")
	public ResponseEntity<Collection<D>> findAllDisabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.DISABLED));
	}

	@Override
//	@GetMapping(path = "/disabled/page")
	public ResponseEntity<?> findAllDisabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.DISABLED));
	}

	public ResponseEntity<D> saveImp(D dto, HttpServletRequest req) {
		String token = req.getHeader("Authorization").replace("Bearer", "");
		System.out.println("#########################3");
		System.out.println(token);
		return ResponseEntity.ok(getService().save(dto));
	}

	public ResponseEntity<D> updateImp(@RequestBody @Valid D dto, HttpServletRequest req) {
		String token = req.getHeader("Authorization").replace("Bearer", "");
		System.out.println("#########################2");
		System.out.println(token);
		return ResponseEntity.ok(getService().update(dto));
	}

	@Override
//	@PostMapping(path = "/collection")
	public ResponseEntity<Collection<D>> save(@RequestBody @Valid Collection<D> collection) {
		return ResponseEntity.ok(getService().save(collection));
	}

	@Override
//	@DeleteMapping(path = "/collection/disabled")
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection) {
		getService().delete(collection);
		return ResponseEntity.noContent().build();
	}

	@Override
//	@DeleteMapping(path = "/collection/enabled")
	public ResponseEntity<Collection<D>> recovery(@RequestBody @Valid Collection<D> collection) {
		getService().recovery(collection);
		return ResponseEntity.noContent().build();
	}

	@Override
//	@DeleteMapping(path = "/disabled/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") I id) {
		getService().delete(id);
		return ResponseEntity.noContent().build();
	}

	@Override
//	@DeleteMapping(path = "/enabled/{id}")
	public ResponseEntity<?> recovery(@PathVariable("id") I id) {
		getService().recovery(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> findAllPage(String status, Pageable pageable) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, statusEntityEnum));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}
	}

	@Override
	public ResponseEntity<Collection<D>> findAllStatus(String status) {
		try {
			StatusEntityEnum statusEntityEnum = StatusEntityEnum.valueOf(status);
			return ResponseEntity.ok(getService().findAllByStatusEntity(statusEntityEnum));
		} catch (java.lang.IllegalArgumentException ex) {
			return ResponseEntity.badRequest().build();
		}
		
	}

}
