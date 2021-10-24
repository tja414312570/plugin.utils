package com.yanan.utils.javascript;

import java.util.HashMap;
import java.util.Map;

public class ScriptEngineManager {
	private static Map<String,ScriptEngineFactory> engineNameMapping = new HashMap<>();
	private Bindings globalBindings = new SimpleBindings();
	public  ScriptEngine getEngineByName(String name) {
		ScriptEngineFactory scriptEngineFactory = engineNameMapping.get(name);
		if(scriptEngineFactory != null) {
			ScriptEngine scriptEngine = scriptEngineFactory.getScriptEngine();
			scriptEngine.setBindings(globalBindings);
			return scriptEngine;
		}
		throw new RuntimeException("cant found engine for ["+name+"]");
	}
	public static void register(String name, ScriptEngineFactory scriptEngineFactory) {
		engineNameMapping.put(name, scriptEngineFactory);
	}
	public static ScriptEngineFactory getScriptEngineFactory(String name) {
		return engineNameMapping.get(name);
	}

}
