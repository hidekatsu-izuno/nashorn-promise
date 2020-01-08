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
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  fulfill('test');\n" +
        "}).then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));

    assertEquals("rejected: test", engine.eval(
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));

    assertEquals("fulfilled: rejected: test", engine.eval(
        "var result = 'init';\n" +
        "var promise = new Promise(function(fulfill, reject) {\n" +
        "  reject('test');\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "}).then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));
  }

  @Test
  public void testPromiseAll() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("fulfilled: 6", engine.eval(
        "var promise = Promise.all([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return 'fulfilled: ' + value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return 'rejected: ' + reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));

    assertEquals("rejected: 2", engine.eval(
        "var promise = Promise.all([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return 'fulfilled: ' + value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return 'rejected: ' + reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));
  }

  @Test
  public void testPromiseRace() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("fulfilled: 1", engine.eval(
        "var promise = Promise.race([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return 'fulfilled: ' + value;\n" +
        "}).catch(function(reason) {\n" +
        "  return 'rejected: ' + reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));

    assertEquals("rejected: 1", engine.eval(
        "var promise = Promise.race([new Promise(function(fulfill, reject) {\n" +
        "  reject(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(3);\n" +
        "})]).then(function(value) {\n" +
        "  return 'fulfilled: ' + value;\n" +
        "}).catch(function(reason) {\n" +
        "  return 'rejected: ' + reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));
  }

  @Test
  public void testPromiseResolve() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js')");

    assertEquals("fulfilled: test", engine.eval(
        "var promise = Promise.resolve(new Promise(function(fulfill, reject) {\n" +
        "  fulfill('test');\n" +
        "})).then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));

    assertEquals("fulfilled: test", engine.eval(
        "var promise = Promise.resolve({\n" +
        "  then: function(fulfill, reject) {\n" +
        "    fulfill('test');\n" +
        "  }\n" +
        "}).then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));

    assertEquals("fulfilled: test", engine.eval(
        "var promise = Promise.resolve('test')" +
        ".then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));
  }

  @Test
  public void testPromiseReject() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("rejected: test", engine.eval(
        "var promise = Promise.reject('test')" +
        ".then(function(value) {\n" +
        "  return 'fulfilled: ' + value\n" +
        "}).catch(function(value) {\n" +
        "  return 'rejected: ' + value\n" +
        "});\n" +
        "promise._future.get().result;\n"));
  }

  @Test
  public void testPromiseReturn() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("fulfilled: test", engine.eval(
        "var promise = Promise.resolve('test')" +
        ".then(function(value) {\n" +
        "  return Promise.resolve('fulfilled: ' + value);\n" +
        "}).then(function(value) {" +
        "  return value;" +
        "});\n" +
        "promise._future.get().result;\n"));
  }

  @Test
  public void testPromiseNested() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("fulfilled: 4", engine.eval(
        "var promise = Promise.resolve(1)" +
        ".then(function(value) {\n" +
        "  return Promise.resolve(value + 1).then(function(value) {" +
        "    return Promise.resolve(value + 1).then(function(value) {" +
        "      return Promise.resolve(value + 1);" +
        "    });\n" +
        "  });\n" +
        "}).then(function(value) {" +
        "  return 'fulfilled: ' + value;" +
        "});\n" +
        "promise._future.get().result;\n"));

    assertEquals("fulfilled: [1,[2,[3]],4]", engine.eval(
        "var promise = Promise.all([\n" +
        "  Promise.resolve(1),\n" +
        "  Promise.all([\n" +
        "    Promise.resolve(2),\n" +
        "    Promise.all([\n" +
        "      Promise.resolve(3)\n" +
        "    ])\n" +
        "  ]),\n" +
        "  Promise.resolve(4)\n" +
        "]).then(function(values) {" +
        "  return 'fulfilled: ' + JSON.stringify(values);" +
        "});\n" +
        "promise._future.get().result;\n"));
  }
}
