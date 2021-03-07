package br.com.asv.service.ws;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountResponse implements Serializable{
	private static final long serialVersionUID = 1L;
	private int count;
	public CountResponse(int count) {
		super();
		this.count = count;
	}
}
