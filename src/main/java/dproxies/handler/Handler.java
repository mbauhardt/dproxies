package dproxies.handler;

public interface Handler<T> {

    boolean handle(T t) throws Exception;
}
