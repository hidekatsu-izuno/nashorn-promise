package net.arnx.nashorn.lib;

import static org.junit.Assert.*;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.junit.Test;

public class PromiseTest {

  @Test
  public void testPromise() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals(Boolean.TRUE, engine.eval(
        "var result = 'init';\n" +
        "new Promise(function(fulfill, reject) {\n" +
        "  fulfill('test');\n" +
        "}).then(function(value) {\n" +
        "  result = value\n" +
        "});\n" +
        "result === 'test';\n"));

    assertEquals(Boolean.TRUE, engine.eval(
        "var result = 'init';\n" +
        "new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).then(function(value) {\n" +
        "  result = value\n" +
        "});\n" +
        "result === 'init';\n"));

    assertEquals(Boolean.TRUE, engine.eval(
        "var result = 'init';\n" +
        "new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).catch(function(value) {\n" +
        "  result = value\n" +
        "});\n" +
        "result === 'test';\n"));
  }

}
