package br.com.asv.service.ws;

import java.util.LinkedList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<D,E> {
	private D data;
	private E options;
	private List<String> errors = new LinkedList<>();
}

