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

    assertEquals("fulfilled: test", engine.eval(
        "var result = 'init';\n" +
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  fulfill('test');\n" +
        "}).then(function(value) {\n" +
        "  result = 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  result = 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get();\n" +
        "result;\n"));

    assertEquals("rejected: test", engine.eval(
        "var result = 'init';\n" +
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).then(function(value) {\n" +
        "  result = 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  result = 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get();\n" +
        "result;\n"));

    assertEquals("fulfilled: rejected: test", engine.eval(
        "var result = 'init';\n" +
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "}).then(function(value) {\n" +
        "  result = 'fulfilled: ' + value\n" +
        "});\n" +
        "promise._future.get();\n" +
        "result;\n"));
  }
}
