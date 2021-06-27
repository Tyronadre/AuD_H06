package h06.util.proxy;

import java.lang.reflect.InvocationHandler;

abstract public class Proxy implements InvocationHandler {

    /**
     * Returns a new proxy instance from the given class
     * @param proxyClass the class of the proxy implementation
     * @return a new proxy instance
     */
    public static Object get(Class<? extends Proxy> proxyClass) {
        try {
            return proxyClass.getDeclaredConstructor().newInstance().getProxyInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a proxy instance with interfaces and invocation handled by the overriding class
     * @return a new proxy instance
     */
    abstract protected Object getProxyInstance();
}
