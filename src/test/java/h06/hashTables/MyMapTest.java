package h06.hashTables;

import h06.util.TestClass;
import h06.util.Utils.TypeParameter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class MyMapTest extends TestClass {

    public final Method containsKey, getValue, put, remove;

    public MyMapTest() {
        super("h06.hashTables.MyMap", null);

        containsKey = getMethodByName("containsKey(K)");
        getValue = getMethodByName("getValue(K)");
        put = getMethodByName("put(K, V)");
        remove = getMethodByName("remove(K)");
    }

    @Test
    @Override
    public void testDefinition() {
        // interface
        assertHasModifiers(testedClass, PUBLIC, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("K", UNBOUNDED), TypeParameter.of("V", UNBOUNDED));

        // methods
        assertHasModifiers(containsKey, ABSTRACT);
        assertReturnType(containsKey, boolean.class.getTypeName());

        assertHasModifiers(getValue, ABSTRACT);
        assertReturnType(getValue, "V");

        assertHasModifiers(put, ABSTRACT);
        assertReturnType(put, "V");

        assertHasModifiers(remove, ABSTRACT);
        assertReturnType(remove, "V");
    }
}
