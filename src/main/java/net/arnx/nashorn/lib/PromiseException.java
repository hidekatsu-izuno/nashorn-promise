package net.arnx.nashorn.lib;

@SuppressWarnings("serial")
public class PromiseException extends RuntimeException {
  private Object result;

  public PromiseException(Object result) {
    this.result = result;
  }

  public Object getResult() {
    return result;
  }
}
