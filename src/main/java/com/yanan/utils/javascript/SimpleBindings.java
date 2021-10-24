package com.yanan.utils.javascript;

import java.util.HashMap;
import java.util.Map;

public class SimpleBindings extends HashMap<String,Object> implements Bindings{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7368705414904394605L;

	@Override
	public Object put(String name, Object value) {
		return super.put(name, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		super.putAll(toMerge);
	}

}
