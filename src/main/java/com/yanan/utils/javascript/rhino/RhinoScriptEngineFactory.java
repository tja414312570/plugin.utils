package com.yanan.utils.javascript.rhino;

import com.yanan.utils.javascript.ScriptEngine;
import com.yanan.utils.javascript.ScriptEngineFactory;
import com.yanan.utils.javascript.ScriptEngineManager;

public class RhinoScriptEngineFactory implements ScriptEngineFactory {
	
	static {
		ScriptEngineManager.register("Rhino",new RhinoScriptEngineFactory());
	}
	@Override
	public ScriptEngine getScriptEngine() {
		return new RhinoScriptEngine();
	}

}
