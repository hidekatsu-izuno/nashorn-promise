[![license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](LICENSE)

# ES6 Promise support for Nashorn

This is a polyfill of the ES6 Promise using Java 8 CompletableFuture.

## Getting Started

### Usage

```java
ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js')");
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details