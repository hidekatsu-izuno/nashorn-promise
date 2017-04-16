[![license](http://img.shields.io/badge/license-MIT-brightgreen.svg)](LICENSE)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/net.arnx/nashorn-promise/badge.svg)](https://maven-badges.herokuapp.com/maven-central/net.arnx/nashorn-promise)


# ES6 Promise support for Nashorn

This is a polyfill of the ES6 Promise using Java 8 CompletableFuture.

## Getting Started

### Setup

To add a dependency using Maven, use the following:

```xml
<dependency>
  <groupId>net.arnx</groupId>
  <artifactId>nashorn-promise</artifactId>
  <version>0.1.0</version>
</dependency>
```

To add a dependency using Gradle:

```groovy
dependencies {
  compile 'net.arnx:nashorn-promise:latest.release'
}
```

### Usage

```java
ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
engine.eval("load('classpath:net/arnx/nashorn/lib/promise.js')");
```

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE) file for details