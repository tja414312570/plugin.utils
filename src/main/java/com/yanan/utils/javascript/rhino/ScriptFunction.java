package com.yanan.utils.javascript.rhino;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class ScriptFunction extends ScriptableObject{

	@Override
	public String getClassName() {
		// TODO Auto-generated method stub
		return "test";
	}
	public void jsFunction_log(Scriptable host) {
		System.err.println(host);
	}

}
