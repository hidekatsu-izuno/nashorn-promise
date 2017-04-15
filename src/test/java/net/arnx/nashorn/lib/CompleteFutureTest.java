package net.arnx.nashorn.lib;

import java.util.concurrent.CompletableFuture;

import org.junit.Assert;
import org.junit.Test;

public class CompleteFutureTest {
  @Test
  public void test() throws Exception {
    Assert.assertEquals((Integer)2, CompletableFuture.anyOf(
        CompletableFuture.<Integer>supplyAsync(() -> {
          throw new PromiseException((Integer)0);
        }),
        CompletableFuture.<Integer>supplyAsync(() -> {
          return 100;
        })
    ).handle((success, exception) -> {
      return ((Integer)((PromiseException)exception.getCause()).getResult() + 1);
    }).handle((success, exception) -> {
      return (success + 1);
    }).get());
  }
}
