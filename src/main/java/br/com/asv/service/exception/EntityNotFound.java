package br.com.asv.service.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@SuppressWarnings("serial")
public class EntityNotFound extends RuntimeException{

	private static final Logger LOGGER = LogManager.getLogger(EntityNotFound.class);
	public EntityNotFound() {
		LOGGER.error("Entity not found");
	}
}
