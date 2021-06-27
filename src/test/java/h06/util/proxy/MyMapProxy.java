package h06.util.proxy;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class MyMapProxy extends h06.util.proxy.Proxy {

    private final Class<?> myMapClass;
    private final Map<Object, Object> map = new HashMap<>();

    public MyMapProxy() {
        try {
            myMapClass = Class.forName("h06.hashTables.MyMap");
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {
        try {
            if (method.equals(myMapClass.getDeclaredMethod("containsKey", Object.class)))
                return map.containsKey(objects[0]);
            else if (method.equals(myMapClass.getDeclaredMethod("getValue", Object.class)))
                return map.get(objects[0]);
            else if (method.equals(myMapClass.getDeclaredMethod("put", Object.class, Object.class)))
                return map.put(objects[0], objects[1]);
            else if (method.equals(myMapClass.getDeclaredMethod("remove", Object.class)))
                return map.remove(objects[0]);
            else if (method.equals(Object.class.getDeclaredMethod("equals", Object.class)))
                return equals(objects[0]);
            else if (method.equals(Object.class.getDeclaredMethod("hashCode")))
                return hashCode();
            else if (method.equals(Object.class.getDeclaredMethod("toString")))
                return toString();
            else
                throw new NoSuchMethodException(method.getName());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {myMapClass},
                this);
    }
}
