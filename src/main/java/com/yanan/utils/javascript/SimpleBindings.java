package com.yanan.utils.javascript;

import java.util.LinkedHashMap;
import java.util.Map;

public class SimpleBindings extends LinkedHashMap<String,Object> implements Bindings{

	/**
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

	@Override
	public boolean containsKey(String key) {
		return super.containsKey(key);
	}

	@Override
	public Object get(String key) {
		return super.get(key);
	}

	@Override
	public Object remove(String key) {
		return super.remove(key);
	}

}
