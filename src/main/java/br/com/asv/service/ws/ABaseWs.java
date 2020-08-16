package br.com.asv.service.ws;

import java.util.Collection;

import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import br.com.asv.controller.IBaseController;
import br.com.asv.model.dtos.IBaseDto;
import br.com.asv.model.enums.StatusEntityEnum;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class ABaseWs<D extends IBaseDto> implements IBaseWs<D> {

	@Getter(AccessLevel.PROTECTED)
	private final IBaseController<D> service;

	public ABaseWs(IBaseController<D> service) {
		this.service = service;
	}

	@GetMapping(path = "/{id}")
	public ResponseEntity<D> findOne(@PathVariable("id") Long id) {
		ResponseEntity<D> ok = ResponseEntity.ok(getService().findOne(id));
		return ok;
	}

	@ApiResponse
	@GetMapping
	public ResponseEntity<Collection<D>> findAll() {
		ResponseEntity<Collection<D>> ok = ResponseEntity.ok(getService().findAll());
		return ok;
	}

	@ApiResponse
	@GetMapping(path = "/page")
	public ResponseEntity<Collection<D>> findAll(Pageable pageable) {
		return ResponseEntity.ok(getService().findAll());
	}


	@GetMapping(path = "/enabled")
	public ResponseEntity<Collection<D>> findAllEnabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.ENABLED));
	}

	@GetMapping(path = "/enabled/page")
	public ResponseEntity<?> findAllEnabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.ENABLED));
	}

	@GetMapping(path = "/disabled")
	public ResponseEntity<Collection<D>> findAllDisabled() {
		return ResponseEntity.ok(getService().findAllByStatusEntity(StatusEntityEnum.DISABLED));
	}

	@GetMapping(path = "/disabled/page")
	public ResponseEntity<?> findAllDisabled(Pageable pageable) {
		return ResponseEntity.ok(getService().findAllByStatusEntity(pageable, StatusEntityEnum.DISABLED));
	}

	public ResponseEntity<D> saveImp(D dto) {
		ResponseEntity<D> ok = ResponseEntity.ok(getService().save(dto));
		return ok;
	}
	
	public ResponseEntity<D> updateImp(@RequestBody @Valid D dto) {
		return ResponseEntity.ok(getService().update(dto));
	}

	@PostMapping(path = "/collection")
	public ResponseEntity<Collection<D>> save(@RequestBody @Valid Collection<D> collection) {
		return ResponseEntity.ok(getService().save(collection));
	}

	@DeleteMapping(path = "/collection/disabled")
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection) {
		getService().delete(collection);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "/collection/enabled")
	public ResponseEntity<Collection<D>> recovery(@RequestBody @Valid Collection<D> collection) {
		getService().recovery(collection);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "/disabled/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") Long id) {
		getService().delete(id);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping(path = "/enabled/{id}")
	public ResponseEntity<?> recovery(@PathVariable("id") Long id) {
		getService().recovery(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
