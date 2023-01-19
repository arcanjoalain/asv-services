package br.com.asv.service.ws;

import java.io.Serializable;
import java.util.Collection;




import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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

import br.com.asv.base.client.dto.IBaseDto;
import br.com.asv.base.client.ws.IResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

/**
 * 
 * @author alain.vieira
 *
 * @param <D> object Dto.
 * @param <I> index for pid.
 */
public interface IBaseWs<D extends IBaseDto<I>, I extends Serializable> {

	@ApiResponse
	@GetMapping
	@ResponseBody
	ResponseEntity<IResponse> findAll(HttpServletRequest request);

	@ApiResponse
	@GetMapping(path = "/search")
	@ResponseBody
	ResponseEntity<IResponse> findAll(@RequestParam(value = "search", required = false) String search,
			HttpServletRequest request);

	@ApiResponse
	@GetMapping(path = "/{id}")
	@Transactional
	@ResponseBody
	ResponseEntity<IResponse> findOne(@PathVariable("id") I id);

	@ApiResponse
	@GetMapping(path = "/page")
	@ResponseBody
	ResponseEntity<IResponse> findAll(Pageable pageable, HttpServletRequest request);

	@ApiResponse
	@GetMapping(path = "/page/search")
	@ResponseBody
	ResponseEntity<IResponse> findAll(@RequestParam(value = "search", required = false) String search,
			Pageable pageable, HttpServletRequest request);

	@ApiResponse
	@GetMapping(path = "/page/{status}")
	@ResponseBody
	ResponseEntity<IResponse> findAllPage(@PathVariable("status") String status, Pageable pageable);

	@ApiResponse
	@GetMapping(path = "/status/{status}")
	@ResponseBody
	ResponseEntity<IResponse> findAllStatus(@PathVariable("status") String status);

	@ApiResponse
	@GetMapping(path = "/enabled")
	@ResponseBody
	ResponseEntity<IResponse> findAllEnabled();

	@ApiResponse
	@GetMapping(path = "/enabled/page")
	@ResponseBody
	ResponseEntity<IResponse> findAllEnabled(Pageable pageable);

	@ApiResponse
	@GetMapping(path = "/disabled")
	@ResponseBody
	ResponseEntity<IResponse> findAllDisabled();

	@ApiResponse
	@GetMapping(path = "/disabled/page")
	@ResponseBody
	ResponseEntity<IResponse> findAllDisabled(Pageable pageable);

	@GetMapping(path = "/count")
	@ResponseBody
	ResponseEntity<IResponse> countAll();

	@GetMapping(path = "/count/search")
	@ResponseBody
	ResponseEntity<IResponse> countAll(@RequestParam(value = "search", required = false) String search);

	@ApiResponse
	@PostMapping(path = "/collection")
	@ResponseStatus(HttpStatus.CREATED)
	@ResponseBody
	ResponseEntity<IResponse> save(@RequestBody @Valid Collection<D> collection);

	@PostMapping
	@ApiResponses
	@Transactional
	@ResponseStatus(HttpStatus.CREATED)
	ResponseEntity<IResponse> save(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto,
			HttpServletRequest req, BindingResult result);

	@ApiResponse
	@DeleteMapping(path = "/collection/disabled")
	@ResponseBody
	ResponseEntity<IResponse> delete(@RequestBody @Valid Collection<D> collection);

	@ApiResponse
	@DeleteMapping(path = "/disabled/{id}")
	@ResponseBody
	ResponseEntity<IResponse> delete(@PathVariable("id") I id);

	@DeleteMapping(path = "/remove/{id}")
	@ResponseStatus(HttpStatus.OK)
	ResponseEntity<IResponse> remove(@PathVariable("id") I id);

	@ApiResponse
	@DeleteMapping(path = "/remove")
	@ResponseBody
	ResponseEntity<IResponse> remove(@RequestBody Collection<D> collection);

	@ApiResponse
	@PatchMapping(path = "/collection/enabled")
	@ResponseBody
	ResponseEntity<IResponse> recovery(@RequestBody @Valid Collection<D> collection);

	@ApiResponse
	@PatchMapping(path = "/enabled/{id}")
	@ResponseBody
	ResponseEntity<IResponse> recovery(@PathVariable("id") I id);

	@PutMapping
	@ApiResponses
	@Transactional
	ResponseEntity<IResponse> update(
			@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto, HttpServletRequest req,
			BindingResult result);

	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
	@Transactional
	ResponseEntity<IResponse> patchDto(@PathVariable("id") I id,
			@Parameter(description = "dto to save.", required = true) @RequestBody JsonPatch patchDto,
			HttpServletRequest req);
}
