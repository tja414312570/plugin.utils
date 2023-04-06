package utils;

import java.lang.reflect.InvocationTargetException;

import org.mozilla.javascript.ScriptableObject;

import com.yanan.utils.javascript.ScriptEngine;
import com.yanan.utils.javascript.ScriptEngineManager;
import com.yanan.utils.javascript.ScriptException;
import com.yanan.utils.javascript.rhino.RhinoScriptEngineFactory;

public class ScriptTest {
	
	public static void main(String[] args) throws ScriptException, ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {
		Class.forName("com.yanan.utils.javascript.rhino.RhinoScriptEngineFactory");
		ScriptEngine scriptEngine = ScriptEngineManager.getEngineByName("Rhino");
		scriptEngine.getBindings().put("a", 111);
		scriptEngine.getBindings().put("b", 2222);
		scriptEngine.eval("test.log(a + b)");
	}
}
