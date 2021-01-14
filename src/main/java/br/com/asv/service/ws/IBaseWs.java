package br.com.asv.service.ws;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface IBaseWs<D,I> {
	
	@ApiResponse
	@GetMapping(path = "/{id}")
	@Transactional
	@ResponseBody
	public ResponseEntity<D> findOne(@PathVariable("id") I id);
	
	@ApiResponse
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> findAll(@RequestParam(value = "search", required = false) String search,HttpServletRequest request) ;
	
	@ApiResponse
	@GetMapping(path = "/page")
	@ResponseBody
	public ResponseEntity<?> findAll(@RequestParam(value = "search", required = false) String search,Pageable pageable,HttpServletRequest request);
	
	@ApiResponse
	@GetMapping(path = "/page/{status}")
	@ResponseBody
	public ResponseEntity<?> findAllPage(@PathVariable("status") String status, Pageable pageable);
	
	@ApiResponse
	@GetMapping(path = "/status/{status}")
	@ResponseBody
	public ResponseEntity<Collection<D>> findAllStatus(@PathVariable("status") String status);
	
	@ApiResponse
	@GetMapping(path = "/enabled")
	@ResponseBody
	public ResponseEntity<?> findAllEnabled();
	
	@ApiResponse
	@GetMapping(path = "/enabled/page")
	@ResponseBody
	public ResponseEntity<?> findAllEnabled(Pageable pageable);
	
	@ApiResponse
	@GetMapping(path = "/disabled")
	@ResponseBody
	public ResponseEntity<Collection<D>> findAllDisabled();
	
	@ApiResponse
	@GetMapping(path = "/disabled/page")
	@ResponseBody
	public ResponseEntity<?> findAllDisabled(Pageable pageable);
	
	@ApiResponse
	@PostMapping(path = "/collection")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseEntity<Collection<D>> save(@RequestBody @Valid Collection<D> collection);
	
	@ApiResponse
	@DeleteMapping(path = "/collection/disabled")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection);
	
	@ApiResponse
	@PutMapping(path = "/collection/enabled")
	@ResponseBody
	public ResponseEntity<Collection<D>> recovery(@RequestBody @Valid Collection<D> collection);
	
	@ApiResponse
	@DeleteMapping(path = "/disabled/{id}")
	@ResponseBody
	public ResponseEntity<?> delete(@PathVariable("id") I id);
	
	@DeleteMapping(path = "/remove/{id")
	@ResponseStatus(HttpStatus.OK)
	public void remove(@PathVariable("id") I id);
	
	@ApiResponse
	@DeleteMapping(path = "/collection/remove")
	@ResponseBody
	public void remove(@RequestBody Collection<D> collection);
	
	@ApiResponse
	@PutMapping(path = "/enabled/{id}")
	@ResponseBody
	public ResponseEntity<?> recovery(@PathVariable("id") I id);
	
	@PostMapping
	@ApiResponses
	@Transactional
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> save(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto , HttpServletRequest req, BindingResult result) ;
	
	@PutMapping
	@Transactional
	public ResponseEntity<?> update(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto, HttpServletRequest req, BindingResult result);

	@GetMapping(path = "/count")
	public @ResponseBody ResponseEntity<?> countAll(@RequestParam(value = "search", required = false) String search);
}
