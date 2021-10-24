package utils;

import java.util.HashMap;
import java.util.Map;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ScriptableObject;

import com.yanan.utils.javascript.Bindings;
import com.yanan.utils.javascript.ScriptEngine;
import com.yanan.utils.javascript.ScriptEngineManager;
import com.yanan.utils.javascript.ScriptException;

public class ExpressTest {
	public static void main(String[] args) throws ScriptException, ClassNotFoundException {
		ContextFactory contextFactory = ContextFactory.getGlobal();
		Context context = contextFactory.enterContext();
		ScriptableObject scriptable = context.initStandardObjects();
		scriptable.put("ref", scriptable, "hello world");
		context.setOptimizationLevel(-1);
		Object res = context.evaluateString(scriptable, "ref != null", null, 0, null);
		System.err.println(res);
		Class.forName("com.yanan.utils.javascript.rhino.RhinoScriptEngineFactory");
		ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("Rhino");
		Bindings bindings = scriptEngine.createBindings();
		String exp = "ref != null";
		Map<String,Object> map = new HashMap();
		bindings.put("ref","ssss");
		Object result = scriptEngine.eval(exp,bindings);
		System.out.println(result);
		System.err.println(scriptEngine.eval("print(ref)",bindings));
		Map.Entry<K, V>
	}
}
