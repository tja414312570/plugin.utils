package com.yanan.utils.javascript.rhino;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map.Entry;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.yanan.utils.IOUtils;
import com.yanan.utils.javascript.Bindings;
import com.yanan.utils.javascript.ScriptEngine;
import com.yanan.utils.javascript.ScriptEngineFactory;
import com.yanan.utils.javascript.ScriptEngineManager;
import com.yanan.utils.javascript.ScriptException;
import com.yanan.utils.javascript.SimpleBindings;

public class RhinoScriptEngine implements ScriptEngine {
	ContextFactory contextFactory;
	Context context;
	Bindings bindings;
	ScriptableObject scriptable;

	RhinoScriptEngine() {
		contextFactory = ContextFactory.getGlobal();
		context = contextFactory.enterContext();
		scriptable = context.initStandardObjects();
		bindings = createBindings();
		Scriptable superProto = ScriptableObject.getObjectPrototype(scriptable);
		Scriptable newScriptable = new RhinoBindings();
		newScriptable.setParentScope(scriptable);
		Constructor<?> ctorMember;
		try {

			ctorMember = ScriptFunction.class.getConstructor();
			FunctionObject ctor = new FunctionObject("test", ctorMember, newScriptable);
			ctor.addAsConstructor(newScriptable, ctor);
			String name = "log";
			Method method = ScriptFunction.class.getMethod("jsFunction_" + name, Scriptable.class);
			FunctionObject f = new FunctionObject(name, method, ctor);
			ScriptableObject.defineProperty(ctor, name, f, ScriptableObject.DONTENUM);

			ScriptableObject.defineProperty(scriptable, "test", ctor, ScriptableObject.DONTENUM);
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		try {
//			ScriptFunction.defineClass(scriptable,ScriptFunction.class);
//		} catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		Scriptable ext = context.newObject(scriptable, "test");
		this.bindings.put("test", ext);
//		scriptable.put("test", scriptable, ext);
	}

	@Override
	public Object eval(String script) throws ScriptException {
		return eval(script, bindings);
	}

	@Override
	public Object eval(Reader reader) throws ScriptException {
		return eval(IOUtils.toString(reader));
	}

	@Override
	public Object eval(String script, Bindings bindings) throws ScriptException {
		for (Entry<String, Object> entry : bindings.entrySet())
			scriptable.put(entry.getKey(), scriptable, entry.getValue());
		try {
			return context.evaluateString(scriptable, script, null, 0, null);
		} catch (EcmaError e) {
			throw new ScriptException(e.getMessage(), e.getLineSource(), e.getLineNumber(), e.getColumnNumber(), e);
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
			throw new ScriptException("failed to read script", e);
		}
		return eval(sb.toString(), n);
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
		return new RhinoBindings();
	}

	@Override
	public ScriptEngineFactory getFactory() {
		return ScriptEngineManager.getScriptEngineFactory("Rhino");
	}

}
