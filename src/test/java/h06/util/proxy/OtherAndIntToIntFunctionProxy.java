package h06.util.proxy;

import h06.util.Utils;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class OtherAndIntToIntFunctionProxy extends h06.util.proxy.Proxy {

    final Class<?> otherAndIntToIntFunctionClass;
    final int offset;
    int tableSize;

    public OtherAndIntToIntFunctionProxy() {
        try {
            otherAndIntToIntFunctionClass = Class.forName("h06.hashFunctions.OtherAndIntToIntFunction");
            offset = Utils.RANDOM.nextInt(100);
            tableSize = Utils.RANDOM.nextInt(10) + 1;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) {
        try {
            if (method.equals(otherAndIntToIntFunctionClass.getDeclaredMethod("apply", Object.class, int.class)))
                return Math.floorMod(Math.floorMod(objects[0].hashCode() + offset, tableSize) + (Integer) objects[1], tableSize);
            else if (method.equals(otherAndIntToIntFunctionClass.getDeclaredMethod("getTableSize")))
                return tableSize;
            else if (method.equals(otherAndIntToIntFunctionClass.getDeclaredMethod("setTableSize", int.class)))
                return tableSize = (int) objects[0];
            else if (method.equals(Object.class.getDeclaredMethod("equals", Object.class)))
                return equals(objects[0]);
            else if (method.equals(Object.class.getDeclaredMethod("hashCode")))
                return hashCode();
            else if (method.equals(Object.class.getDeclaredMethod("toString")))
                return toString();
            else
                return null;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getProxyInstance() {
        return Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class[] {otherAndIntToIntFunctionClass},
                this);
    }
}
