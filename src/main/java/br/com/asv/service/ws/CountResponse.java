package br.com.asv.service.ws;

import java.io.Serializable;

import br.com.asv.base.client.ws.ICountResponse;
import lombok.Getter;
import lombok.Setter;
/**
 * Class to count response
 * @author alain.vieira
 *
 * @param <I> type to storage count response.
 * 
 */
@Getter
@Setter
public class CountResponse<I extends Serializable> implements ICountResponse,Serializable{
	private static final long serialVersionUID = 1L;
	private I count;
	public CountResponse(I count) {
		super();
		this.count = count;
	}
}
