package h06.hashTables;

import h06.util.TestClass;
import h06.util.Utils.TypeParameter;
import h06.util.provider.KeyValueProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class KeyValuePairTest extends TestClass {

    public final Field key, value;
    public final Method getKey, getValue, setValue;

    public KeyValuePairTest() {
        super("h06.hashTables.KeyValuePair", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 2 &&
                   types[0].getTypeName().equals("K") &&
                   types[1].getTypeName().equals("V");
        });

        key = getFieldByCriteria("key", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && isFinal(modifiers) && field.getGenericType().getTypeName().equals("K");
        });
        value = getFieldByCriteria("value", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && !isFinal(modifiers) && field.getGenericType().getTypeName().equals("V");
        });

        getKey = getMethodByName("getKey()");
        getValue = getMethodByName("getValue()");
        setValue = getMethodByName("setValue(V)");
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);
        assertDoesNotHaveModifiers(testedClass, ABSTRACT, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("K", UNBOUNDED), TypeParameter.of("V", UNBOUNDED));

        // fields
        assertHasModifiers(key, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(key, STATIC);
        assertType(key, "K");

        assertHasModifiers(value, PRIVATE);
        assertDoesNotHaveModifiers(value, STATIC, FINAL);
        assertType(value, "V");

        // methods
        assertHasModifiers(getKey, PUBLIC);
        assertReturnType(getKey, "K");

        assertHasModifiers(getValue, PUBLIC);
        assertReturnType(getValue, "V");

        assertHasModifiers(setValue, PUBLIC);
    }

    @Test
    @Override
    public void testInstance() throws Exception {
        Object instance = newInstance("key", "value");

        assertEquals("key", key.get(instance));
        assertEquals("value", value.get(instance));
    }

    @ParameterizedTest
    @ArgumentsSource(KeyValueProvider.class)
    public void testGetKey(Object keyObject, Object valueObject) throws ReflectiveOperationException {
        Object instance = newInstance(keyObject, valueObject);

        assertSame(keyObject, getKey.invoke(instance), "KeyValuePair#getKey() did not return the expected value");
    }

    @ParameterizedTest
    @ArgumentsSource(KeyValueProvider.class)
    public void testGetValue(Object keyObject, Object valueObject) throws ReflectiveOperationException {
        Object instance = newInstance(keyObject, valueObject);

        assertSame(valueObject, getValue.invoke(instance), "KeyValuePair#getValue() did not return the expected value");
    }

    @ParameterizedTest
    @ArgumentsSource(KeyValueProvider.class)
    public void testSetValue(Object keyObject, Object valueObject) throws ReflectiveOperationException {
        Object instance = newInstance(keyObject, valueObject);

        setValue.invoke(instance, keyObject);

        assertSame(keyObject, value.get(instance), "KeyValuePair#setValue(V) did not set value of field \"" + value.getName() + "\"");
    }
}
