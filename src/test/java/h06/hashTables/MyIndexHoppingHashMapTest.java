package h06.hashTables;

import h06.hashFunctions.OtherAndIntToIntFunctionTest;
import h06.util.TestClass;
import h06.util.Utils.TypeParameter;
import h06.util.provider.MyMapProvider;
import h06.util.proxy.OtherAndIntToIntFunctionProxy;
import h06.util.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class MyIndexHoppingHashMapTest extends TestClass {

    public final Field theKeys, theValues, occupiedSinceLastRehash, numberOfTrues, resizeFactor, occupationThreshold, function;
    public final Method containsKey, getValue, put, remove, rehash;

    private final OtherAndIntToIntFunctionTest otherAndIntToIntFunctionTest;

    public MyIndexHoppingHashMapTest() {
        super("h06.hashTables.MyIndexHoppingHashMap", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 4 &&
                   types[0].equals(int.class) &&
                   types[1].equals(double.class) &&
                   types[2].equals(double.class) &&
                   types[3].getTypeName().equals("h06.hashFunctions.OtherAndIntToIntFunction<K>");
        });

        otherAndIntToIntFunctionTest = new OtherAndIntToIntFunctionTest();
        MyMapTest myMapTest = new MyMapTest();
        Predicate<Field> privateObjectVariable = field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && !isFinal(modifiers);
        }, privateObjectConstant = field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && isFinal(modifiers);
        };
        Object instance = newInstance(10, 5.0, 0.5, Proxy.get(OtherAndIntToIntFunctionProxy.class));

        theKeys = getFieldByName("theKeys");
        theValues = getFieldByName("theValues");
        occupiedSinceLastRehash = getFieldByName("occupiedSinceLastRehash");
        numberOfTrues = getFieldByCriteria("numberOfTrues", field ->
                privateObjectVariable.test(field) && (field.getType().equals(int.class) || field.getType().equals(Integer.class)));
        resizeFactor = getFieldByCriteria("resizeFactor", field -> {
            try {
                return privateObjectConstant.test(field) && field.getType().equals(double.class) && field.get(instance).equals(5.0);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        occupationThreshold = getFieldByCriteria("occupationThreshold", field -> {
            try {
                return privateObjectConstant.test(field) && field.getType().equals(double.class) && field.get(instance).equals(0.5);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
        function = getFieldByCriteria("function", field ->
                privateObjectVariable.test(field) && field.getGenericType().getTypeName().equals("h06.hashFunctions.OtherAndIntToIntFunction<K>"));

        containsKey = myMapTest.containsKey;
        getValue = myMapTest.getValue;
        put = myMapTest.put;
        remove = myMapTest.remove;
        rehash = getMethodByName("rehash()");
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);
        assertDoesNotHaveModifiers(testedClass, ABSTRACT, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("K", UNBOUNDED), TypeParameter.of("V", UNBOUNDED));
        assertImplements(testedClass, "h06.hashTables.MyMap<K, V>");

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);

        // fields
        assertHasModifiers(theKeys, PRIVATE);
        assertDoesNotHaveModifiers(theKeys, STATIC, FINAL);
        assertType(theKeys, "K[]");

        assertHasModifiers(theValues, PRIVATE);
        assertDoesNotHaveModifiers(theValues, STATIC, FINAL);
        assertType(theValues, "V[]");

        assertHasModifiers(occupiedSinceLastRehash, PRIVATE);
        assertDoesNotHaveModifiers(occupiedSinceLastRehash, STATIC, FINAL);
        assertType(occupiedSinceLastRehash, boolean[].class.getTypeName());

        assertHasModifiers(numberOfTrues, PRIVATE);
        assertDoesNotHaveModifiers(numberOfTrues, STATIC, FINAL);
        assertTrue(numberOfTrues.getType().equals(int.class) || numberOfTrues.getType().equals(Integer.class),
                "Type of field " + numberOfTrues.getName() + " is incorrect");

        assertHasModifiers(resizeFactor, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(resizeFactor, STATIC);
        assertType(resizeFactor, double.class.getTypeName());

        assertHasModifiers(occupationThreshold, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(occupationThreshold, STATIC);
        assertType(occupationThreshold, double.class.getTypeName());

        assertHasModifiers(function, PRIVATE);
        assertDoesNotHaveModifiers(function, STATIC, FINAL);
        assertType(function, otherAndIntToIntFunctionTest.className + "<K>");

        // methods
        assertHasModifiers(rehash, PRIVATE);
        assertDoesNotHaveModifiers(rehash, STATIC);
    }

    @Test
    @Override
    public void testInstance() throws Exception {
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(10, 5.0, 0.5, proxyFunction);

        assertEquals(5.0, resizeFactor.get(instance),
                "Resize factor (" + resizeFactor.getName() + ") differs from the expected value");
        assertEquals(0.5, occupationThreshold.get(instance),
                "Occupation threshold (" + occupationThreshold.getName() + ") differs from the expected value");
        assertSame(proxyFunction, function.get(instance));
        
        assertEquals(10, ((Object[]) theKeys.get(instance)).length,
                "Field theKeys does not have the same length as the table size");
        assertEquals(10, ((Object[]) theValues.get(instance)).length,
                "Field theValues does not have the same length as the table size");
        assertEquals(10, ((boolean[]) occupiedSinceLastRehash.get(instance)).length,
                "Field occupiedSinceLastRehash does not have the same length as the table size");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testContainsKey(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(20, 5.0, 1.0, proxyFunction);

        otherAndIntToIntFunctionTest.setTableSize.invoke(proxyFunction, 20);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);

        for (Object object : addedObjects)
            assertTrue((Boolean) containsKey.invoke(instance, object),
                    "The object is in both theKeys and theValues (not necessarily at the expected index, but it exists), but was not found");

        for (Object object : remainingObjects)
            assertFalse((Boolean) containsKey.invoke(instance, object),
                    "The object is in neither theKeys nor theValues, but was supposedly found");

        for (int i = 0; i < 3; i++)
            for (Object object : addedObjects)
                assertTrue((Boolean) containsKey.invoke(instance, object),
                        "The object is in both theKeys and theValues (not necessarily at the expected index, but it exists), " +
                                "but was not found again -> inconsistent results");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testGetValue(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(20, 5.0, 1.0, proxyFunction);

        otherAndIntToIntFunctionTest.setTableSize.invoke(proxyFunction, 20);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);


        for (Object object : addedObjects)
            assertSame(object, getValue.invoke(instance, object),
                    "The object is in both theKeys and theValues (not necessarily at the expected index, but it exists), " +
                            "but was not found or a different value was returned");

        for (Object object : remainingObjects)
            assertNull(getValue.invoke(instance, object), "The object is in neither theKeys nor theValues, but was supposedly found");

        for (int i = 0; i < 3; i++)
            for (Object object : addedObjects)
                assertSame(object, getValue.invoke(instance, object),
                        "The object is in both theKeys and theValues (not necessarily at the expected index, but it exists), " +
                                "but was not found again -> inconsistent results");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testPut(List<Object> objectList) throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(5, 5.0, 0.3, proxyFunction);
        int tableSize = 5, addedObjects = 0;

        otherAndIntToIntFunctionTest.setTableSize.invoke(proxyFunction, 5);

        for (Object object : objectList) {
            assertNull(put.invoke(instance, object, object));

            if (addedObjects + 1 > tableSize * 0.5)
                assertEquals(tableSize *= 5, ((Object[]) theKeys.get(instance)).length);

            addedObjects++;
        }

        for (Object object : objectList)
            assertSame(object, put.invoke(instance, object, object));
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testRemove(List<Object> objectList) throws ReflectiveOperationException {
        List<Object> addedObjects = objectList.subList(0, objectList.size() / 2),
                     remainingObjects = objectList.subList(objectList.size() / 2, objectList.size());
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(20, 5.0, 1.0, proxyFunction);

        otherAndIntToIntFunctionTest.setTableSize.invoke(proxyFunction, 20);

        for (Object object : addedObjects)
            put.invoke(instance, object, object);

        for (Object object : addedObjects) {
            assertSame(object, remove.invoke(instance, object),
                    "The object is in both theKeys and theValues (not necessarily at the expected index, but it exists), " +
                            "but was not found or a different value was returned");
            assertTrue(Arrays.stream((Object[]) theKeys.get(instance)).noneMatch(o -> o == object),
                    "The object should have been removed, but was found in theKeys");
            assertTrue(Arrays.stream((Object[]) theValues.get(instance)).noneMatch(o -> o == object),
                    "The object should have been removed, but was found in theValues");
        }

        for (Object object : remainingObjects)
            assertNull(remove.invoke(instance, object), "The object is in neither theKeys nor theValues, but was supposedly found");

        for (int i = 0; i < 3; i++)
            for (Object object : addedObjects)
                assertNull(remove.invoke(instance, object),
                        "The object should have been removed, but was supposedly found -> inconsistent results");
    }

    @ParameterizedTest
    @ArgumentsSource(MyMapProvider.class)
    public void testRehash(List<Object> objectList) throws ReflectiveOperationException {
        Iterator<Object> iterator = objectList.iterator();
        Object proxyFunction = Proxy.get(OtherAndIntToIntFunctionProxy.class), instance = newInstance(20, 5.0, 1.0, proxyFunction);
        Object[] keys = new Object[20], values = new Object[20];
        boolean[] occupied = new boolean[20];

        for (int i = 0; i < 20 && iterator.hasNext(); i++) {
            keys[i] = values[i] = iterator.next();
            occupied[i] = true;
        }

        theKeys.set(instance, keys);
        theValues.set(instance, values);
        occupiedSinceLastRehash.set(instance, occupied);
        numberOfTrues.set(instance, objectList.size());
        otherAndIntToIntFunctionTest.setTableSize.invoke(proxyFunction, 20);

        rehash.invoke(instance);

        int keyCounter = 0, valueCounter = 0, occupiedCounter = 0;
        keys = (Object[]) theKeys.get(instance);
        values = (Object[]) theValues.get(instance);
        occupied = (boolean[]) occupiedSinceLastRehash.get(instance);

        assertEquals(100, keys.length, "Field theKeys does not have correct length after calling rehash()");
        assertEquals(100, values.length, "Field theValues does not have correct length after calling rehash()");
        assertEquals(100, occupied.length, "Field occupiedSinceLastRehash does not have correct length after calling rehash()");

        for (Object object : keys)
            if (object != null) {
                assertTrue(objectList.contains(object), "The object is not in the given list of objects");

                keyCounter++;
            }

        for (Object object : values)
            if (object != null) {
                assertTrue(objectList.contains(object), "The object is not in the given list of objects");

                valueCounter++;
            }

        for (boolean bool : occupied)
            if (bool)
                occupiedCounter++;

        assertEquals(objectList.size(), keyCounter, "Number of elements in new array does not equal number of elements in old array");
        assertEquals(objectList.size(), valueCounter, "Number of elements in new array does not equal number of elements in old array");
        assertEquals(objectList.size(), occupiedCounter, "Number of elements in new array does not equal number of elements in old array");
    }
}
