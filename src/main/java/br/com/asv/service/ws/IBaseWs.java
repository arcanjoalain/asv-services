package br.com.asv.service.ws;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface IBaseWs<D> {
	
	@PostMapping
	@ApiResponses
	ResponseEntity<D> save(@Parameter(description = "dto to save.", required = true) @RequestBody @Valid D dto) ;
	
	@PutMapping
	ResponseEntity<D> update(@RequestBody @Valid D dto);

}
