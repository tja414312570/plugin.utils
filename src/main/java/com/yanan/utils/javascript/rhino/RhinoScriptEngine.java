package com.yanan.utils.javascript.rhino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ScriptableObject;

import com.yanan.utils.javascript.Bindings;
import com.yanan.utils.javascript.ScriptEngine;
import com.yanan.utils.javascript.ScriptEngineFactory;
import com.yanan.utils.javascript.ScriptEngineManager;
import com.yanan.utils.javascript.ScriptException;
import com.yanan.utils.javascript.SimpleBindings;

public class RhinoScriptEngine implements ScriptEngine{
	ContextFactory contextFactory ;
	Context context;
	Bindings bindings;
	ScriptableObject scriptable;
	RhinoScriptEngine(){
		contextFactory= ContextFactory.getGlobal();
		context = contextFactory.enterContext();
		scriptable =context.initStandardObjects();
	}

	@Override
	public Object eval(String script) throws ScriptException {
		return eval(script,bindings);
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, Bindings bindings) throws ScriptException {
		for(Entry<String, Object> entry :bindings.entrySet())
			scriptable.put(entry.getKey(), scriptable, entry.getValue());
		try {
			return context.evaluateString(scriptable, script, null, 0, null);
		}catch (EcmaError e) {
			throw new ScriptException(e.getMessage(),e.getLineSource(),e.getLineNumber(), e.getColumnNumber());
		}
		
	}

	@Override
	public Object eval(Reader reader, Bindings n) throws ScriptException {
        BufferedReader bufferedReader = new BufferedReader(reader);
        StringBuffer sb = new StringBuffer();
        String temp = null;
        try {
			while ((temp = bufferedReader.readLine()) != null) {
			    sb.append(temp);
			}
			bufferedReader.close();
		} catch (IOException e) {
			throw new ScriptException("failed to read script",e);
		}
        return eval(sb.toString(),n);
	}

	@Override
	public void put(String key, Object value) {
		scriptable.put(key, scriptable, value);
	}

	@Override
	public Object get(String key) {
		return this.scriptable.get(key);
	}

	@Override
	public Bindings getBindings() {
		return bindings;
	}

	@Override
	public void setBindings(Bindings bindings) {
		this.bindings = bindings;
	}

	@Override
	public Bindings createBindings() {
		return new SimpleBindings();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return ScriptEngineManager.getScriptEngineFactory("Rhino");
	}

}
