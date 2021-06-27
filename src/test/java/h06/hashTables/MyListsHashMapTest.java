package h06.hashTables;

import h06.hashFunctions.OtherToIntFunctionTest;
import h06.util.TestClass;
import h06.util.Utils.TypeParameter;
import h06.util.provider.MyMapProvider;
import h06.util.proxy.OtherToIntFunctionProxy;
import h06.util.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class MyListsHashMapTest extends TestClass {

    public final Field table, function;
    public final Method containsKey, getValue, put, remove;

    private final OtherToIntFunctionTest otherToIntFunctionTest;
    private final KeyValuePairTest keyValuePairTest;

    public MyListsHashMapTest() {
        super("h06.hashTables.MyListsHashMap", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 1 &&
                   types[0].getTypeName().equals("h06.hashFunctions.OtherToIntFunction<K>");
        });

        MyMapTest myMapTest = new MyMapTest();
        otherToIntFunctionTest = new OtherToIntFunctionTest();
        keyValuePairTest = new KeyValuePairTest();

        table = getFieldByCriteria("table", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) &&
                   !isStatic(modifiers) &&
                   isFinal(modifiers) &&
                   field.getGenericType().getTypeName().equals("java.util.LinkedList<h06.hashTables.KeyValuePair<K, V>>[]");
        });
        function = getFieldByCriteria("function", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) &&
                   !isStatic(modifiers) &&
                   isFinal(modifiers) &&
                   field.getGenericType().getTypeName().equals("h06.hashFunctions.OtherToIntFunction<K>");
        });

        containsKey = myMapTest.containsKey;
        getValue = myMapTest.getValue;
        put = myMapTest.put;
        remove = myMapTest.remove;
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);
        assertDoesNotHaveModifiers(testedClass, ABSTRACT, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("K", UNBOUNDED), TypeParameter.of("V", UNBOUNDED));
        assertImplements(testedClass, "h06.hashTables.MyMap<K, V>");

        // fields
        assertHasModifiers(table, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(table, STATIC);
        assertType(table, "java.util.LinkedList<h06.hashTables.KeyValuePair<K, V>>[]");

        assertHasModifiers(function, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(function, STATIC);
        assertType(function, otherToIntFunctionTest.className + "<K>");

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);
    }

    @Test
    @Override
    public void testInstance() throws Exception {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);
        Object[] tableArray = (Object[]) table.get(instance);

        assertEquals(otherToIntFunctionTest.getTableSize.invoke(proxyFunction), tableArray.length,
                "Constructor did not initialize field \"table\" correctly");

        for (Object object : tableArray)
            assertTrue(object instanceof java.util.LinkedList, "Array component is not an instance of java.util.LinkedList");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testContainsKey(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);

        for (Object object : addedObjects)
            assertTrue((Boolean) containsKey.invoke(instance, object),
                    "The object is in table[" + otherToIntFunctionTest.apply.invoke(proxyFunction, object) + "], but was not found");

        for (Object object : remainingObjects)
            assertFalse((Boolean) containsKey.invoke(instance, object), "The object is not in any list in table, but was supposedly found");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testGetValue(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);

        for (Object object : addedObjects)
            assertSame(object, getValue.invoke(instance, object),
                    "The object is in table[" + otherToIntFunctionTest.apply.invoke(proxyFunction, object) + "], but was not found");

        for (Object object : remainingObjects)
            assertNull(getValue.invoke(instance, object), "The object is not in any list in table, but was supposedly found");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testPut(List<Object> objectList) throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);
        int[] listSizes = new int[(Integer) otherToIntFunctionTest.getTableSize.invoke(proxyFunction)];

        for (Object object : objectList) {
            int index = (Integer) otherToIntFunctionTest.apply.invoke(proxyFunction, object);

            assertNull(put.invoke(instance, object, object),
                    "Given objects are distinct from another so put(K, V) shouldn't return a value other than null");
            assertEquals(++listSizes[index], ((LinkedList<?>[]) table.get(instance))[index].size(),
                    "Size of list table[" + index + "] differs from expected one");
            assertSame(object, keyValuePairTest.getValue.invoke(((LinkedList<?>[]) table.get(instance))[index].get(0)),
                    "Key / value pairs must be inserted at index 0");
        }

        for (Object object : objectList)
            assertSame(object, put.invoke(instance, object, object));
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testRemove(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);

        for (Object object : addedObjects)
            assertSame(object, remove.invoke(instance, object),
                    "The object is in table[" + otherToIntFunctionTest.apply.invoke(proxyFunction, object) + "], but was not found");

        for (Object object : remainingObjects)
            assertNull(remove.invoke(instance, object), "The object is not in any list in table, but was supposedly found");
    }
}
