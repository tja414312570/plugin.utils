package com.yanan.utils.resource;

import java.io.IOException;

public class ResourceNotFoundException extends RuntimeException {
	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(String msg, IOException e) {
		super(msg,e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5394334265086142282L;

}
