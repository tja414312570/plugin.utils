package com.yanan.utils.javascript.rhino;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.ScriptableObject;

import com.yanan.utils.javascript.Bindings;

public class RhinoBindings extends ScriptableObject implements Bindings{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4505297646865051666L;

	@Override
	public boolean containsValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<String> keySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Collection<Object> values() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object put(String name, Object value) {
		Object oldValue = super.get(name);
		super.put(name, this, value);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> toMerge) {
		toMerge.forEach((key,value)->{
			super.put(key, this, value);
		});
	}

	@Override
	public boolean containsKey(String key) {
		return super.has((String) key, this);
	}

	@Override
	public Object remove(String key) {
		Object value = super.get(key);
		super.delete((String)key);
		return value;
	}

	@Override
	public String getClassName() {
		return this.getClass().getSimpleName();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.containsKey((String)key);
	}

	@Override
	public Object get(String key) {
		return super.get(key);
	}

	@Override
	public Object remove(Object key) {
		return this.remove((String)key);
	}

	


}
