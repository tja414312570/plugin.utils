package com.yanan.utils.javascript.rhino;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.mozilla.javascript.Scriptable;

import com.yanan.utils.javascript.Bindings;
import com.yanan.utils.javascript.SimpleBindings;

public class RhinoBindings extends SimpleBindings implements Bindings,Scriptable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public Object get(String name, Scriptable start) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(int index, Scriptable start) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean has(String name, Scriptable start) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean has(int index, Scriptable start) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void put(String name, Scriptable start, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void put(int index, Scriptable start, Object value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scriptable getPrototype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPrototype(Scriptable prototype) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Scriptable getParentScope() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setParentScope(Scriptable parent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object[] getIds() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getDefaultValue(Class<?> hint) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasInstance(Scriptable instance) {
		// TODO Auto-generated method stub
		return false;
	}


}
