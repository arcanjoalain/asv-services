package br.com.asv.service.ws;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface IBaseWs<D,I> {
	
	@GetMapping(path = "/{id}")
	public ResponseEntity<D> findOne(@PathVariable("id") I id);
	
	@ApiResponse
	@GetMapping
	public ResponseEntity<Collection<D>> findAll() ;
	
	@ApiResponse
	@GetMapping(path = "/page")
	public ResponseEntity<Collection<D>> findAll(Pageable pageable);
	
	@ApiResponse
	@GetMapping(path = "/page/{status}")
	public ResponseEntity<?> findAllPage(@PathVariable("status") String status, Pageable pageable);
	
	@GetMapping(path = "/status/{status}")
	public ResponseEntity<Collection<D>> findAllStatus(@PathVariable("status") String status);
	
	@GetMapping(path = "/enabled")
	public ResponseEntity<Collection<D>> findAllEnabled();
	
	@GetMapping(path = "/enabled/page")
	public ResponseEntity<?> findAllEnabled(Pageable pageable);
	
	@GetMapping(path = "/disabled")
	public ResponseEntity<Collection<D>> findAllDisabled();
	
	@GetMapping(path = "/disabled/page")
	public ResponseEntity<?> findAllDisabled(Pageable pageable);
	
	@PostMapping(path = "/collection")
	public ResponseEntity<Collection<D>> save(@RequestBody @Valid Collection<D> collection);
	
	@DeleteMapping(path = "/collection/disabled")
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection);
	
	@DeleteMapping(path = "/collection/enabled")
	public ResponseEntity<Collection<D>> recovery(@RequestBody @Valid Collection<D> collection);
	
	@DeleteMapping(path = "/disabled/{id}")
	public ResponseEntity<?> delete(@PathVariable("id") I id);
	
	@DeleteMapping(path = "/enabled/{id}")
	public ResponseEntity<?> recovery(@PathVariable("id") I id);
	
	@PostMapping
	@ApiResponses
	@Transactional
	ResponseEntity<D> save(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto , HttpServletRequest req) ;
	
	@PutMapping
	@Transactional
	ResponseEntity<D> update(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto, HttpServletRequest req);

}
