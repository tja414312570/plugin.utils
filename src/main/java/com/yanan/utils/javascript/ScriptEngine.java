package com.yanan.utils.javascript;

import java.io.Reader;

/**
 * <code>ScriptEngine</code> is the fundamental interface whose methods must be
 * fully functional in every implementation of this specification.
 * <br><br>
 * These methods provide basic scripting functionality.  Applications written to this
 * simple interface are expected to work with minimal modifications in every implementation.
 * It includes methods that execute scripts, and ones that set and get values.
 * <br><br>
 * The values are key/value pairs of two types.  The first type of pairs consists of
 * those whose keys are reserved and defined in this specification or  by individual
 * implementations.  The values in the pairs with reserved keys have specified meanings.
 * <br><br>
 * The other type of pairs consists of those that create Java language Bindings, the values are
 * usually represented in scripts by the corresponding keys or by decorated forms of them.
 *
 * @author Mike Grogan
 * @since 1.6
 */

public interface ScriptEngine  {
    public Object eval(String script) throws ScriptException;
    public Object eval(Reader reader) throws ScriptException;
    public Object eval(String script, Bindings n) throws ScriptException;
    public Object eval(Reader reader , Bindings n) throws ScriptException;
    public void put(String key, Object value);
    public Object get(String key);
    public Bindings getBindings();
    public void setBindings(Bindings bindings);
    public Bindings createBindings();
    public ScriptEngineFactory getFactory();
}
