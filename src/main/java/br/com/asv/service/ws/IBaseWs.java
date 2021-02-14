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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.fge.jsonpatch.JsonPatch;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface IBaseWs<D,I> {
	
	public ResponseEntity<?> saveImp(@RequestBody @Valid D dto, HttpServletRequest req, BindingResult result);
	
	public ResponseEntity<?> updateImp(@RequestBody @Valid D dto, HttpServletRequest req, BindingResult result);
	
	@ApiResponse
	@GetMapping
	@ResponseBody
	public ResponseEntity<?> findAll(@RequestParam(value = "search", required = false) String search,HttpServletRequest request) ;
	
	@ApiResponse
	@GetMapping(path = "/{id}")
	@Transactional
	@ResponseBody
	public ResponseEntity<?> findOne(@PathVariable("id") I id);
	
	@ApiResponse
	@GetMapping(path = "/page")
	@ResponseBody
	public ResponseEntity<?> findAll(@RequestParam(value = "search", required = false) String search, Pageable pageable,HttpServletRequest request);
	
	@ApiResponse
	@GetMapping(path = "/page/{status}")
	@ResponseBody
	public ResponseEntity<?> findAllPage(@PathVariable("status") String status,Pageable pageable);
	
	@ApiResponse
	@GetMapping(path = "/status/{status}")
	@ResponseBody
	public ResponseEntity<?> findAllStatus(@PathVariable("status") String status);
	
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
	public ResponseEntity<?> findAllDisabled();
	
	@ApiResponse
	@GetMapping(path = "/disabled/page")
	@ResponseBody
	public ResponseEntity<?> findAllDisabled( Pageable pageable);
	
	@GetMapping(path = "/count")
	public @ResponseBody ResponseEntity<?> countAll(@RequestParam(value = "search", required = false) String search);
	
	@ApiResponse
	@PostMapping(path = "/collection")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	public ResponseEntity<?> save(@RequestBody @Valid Collection<D> collection);
	
	@PostMapping
	@ApiResponses
	@Transactional
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseEntity<?> save(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto , HttpServletRequest req, BindingResult result) ;
	
	@ApiResponse
	@DeleteMapping(path = "/collection/disabled")
	@ResponseBody
	public ResponseEntity<?> delete(@RequestBody @Valid Collection<D> collection);
	
	@ApiResponse
	@DeleteMapping(path = "/disabled/{id}")
	@ResponseBody
	public ResponseEntity<?> delete(@PathVariable("id") I id);
	
	@DeleteMapping(path = "/remove/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> remove(@PathVariable("id") I id);

	@ApiResponse
	@DeleteMapping(path = "/remove")
	@ResponseBody
	public ResponseEntity<?> remove(@RequestBody Collection<D> collection);
	
	@ApiResponse
	@PutMapping(path = "/collection/enabled")
	@ResponseBody
	public ResponseEntity<?> recovery(@RequestBody @Valid Collection<D> collection);
	
	@ApiResponse
	@PutMapping(path = "/enabled/{id}")
	@ResponseBody
	public ResponseEntity<?> recovery(@PathVariable("id") I id);
	
	@PutMapping
	@ApiResponses
	@Transactional
	public ResponseEntity<?> update(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto, HttpServletRequest req, BindingResult result);
	
	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
	@Transactional
	public ResponseEntity<?> patchDto(@PathVariable("id") I id, @Parameter(description = "dto to save.", required = true) @RequestBody JsonPatch patchDto, HttpServletRequest req);
}
