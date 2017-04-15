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

    assertEquals("6: test", engine.eval(
        "var count = 0;\n" +
        "var promise = Promise.all([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));

    assertEquals("2", engine.eval(
        "var count = 0;\n" +
        "var promise = Promise.all([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));
  }

  @Test
  public void testPromiseRace() throws ScriptException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js');");

    assertEquals("4", engine.eval(
        "var count = 0;\n" +
        "var promise = Promise.race([new Promise(function(fulfill, reject) {\n" +
        "  fulfill(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  fulfill(3);\n" +
        "})]).then(function(value) {\n" +
        "  return value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));

    assertEquals("4", engine.eval(
        "var count = 0;\n" +
        "var promise = Promise.race([new Promise(function(fulfill, reject) {\n" +
        "  reject(1);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(2);\n" +
        "}), new Promise(function(fulfill, reject) {\n" +
        "  reject(3);\n" +
        "})]).then(function(value) {\n" +
        "  return value.reduce(function(prev, current) { return prev + current; });\n" +
        "}).catch(function(reason) {\n" +
        "  return reason;\n" +
        "});\n" +
        "String(promise._future.get().result);\n"));
  }
}
