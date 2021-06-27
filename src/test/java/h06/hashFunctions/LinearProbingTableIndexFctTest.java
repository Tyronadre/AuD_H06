package h06.hashFunctions;

import h06.util.TestClass;
import h06.util.provider.IntFunctionProvider;
import h06.util.provider.TableSizeProvider;
import h06.util.proxy.OtherToIntFunctionProxy;
import h06.util.proxy.Proxy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static h06.util.Assertions.*;
import static h06.util.Utils.*;
import static java.lang.reflect.Modifier.*;

public class LinearProbingTableIndexFctTest extends TestClass {

    public final Field function;
    public final Method apply, getTableSize, setTableSize;

    private final OtherToIntFunctionTest otherToIntFunctionTest;

    public LinearProbingTableIndexFctTest() {
        super("h06.hashFunctions.LinearProbingTableIndexFct", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 1 && types[0].getTypeName().equals("h06.hashFunctions.OtherToIntFunction<T>");
        });

        otherToIntFunctionTest = new OtherToIntFunctionTest();
        OtherAndIntToIntFunctionTest otherAndIntToIntFunctionTest = new OtherAndIntToIntFunctionTest();

        function = getFieldByCriteria("function", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) &&
                   !isStatic(modifiers) &&
                   isFinal(modifiers) &&
                   field.getType().equals(otherToIntFunctionTest.testedClass);
        });

        apply = otherAndIntToIntFunctionTest.apply;
        getTableSize = otherAndIntToIntFunctionTest.getTableSize;
        setTableSize = otherAndIntToIntFunctionTest.setTableSize;
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);
        assertDoesNotHaveModifiers(testedClass, ABSTRACT, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("T", UNBOUNDED));
        assertImplements(testedClass, "h06.hashFunctions.OtherAndIntToIntFunction<T>");

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);

        // fields
        assertHasModifiers(function, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(function, STATIC);
        assertType(function, otherToIntFunctionTest.className + "<T>");
    }

    @Test
    @Override
    public void testInstance() throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        // variable initialized correctly
        assertSame(proxyFunction, function.get(instance), "Wrong object assigned to " + function.getName());
    }

    @ParameterizedTest
    @ArgumentsSource(IntFunctionProvider.class)
    public void testApply(@SuppressWarnings("unused") int tableSize, int i, Object x) throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);
        int a = (int) otherToIntFunctionTest.apply.invoke(proxyFunction, x);

        assertEquals(Math.floorMod(a + i, (Integer) otherToIntFunctionTest.getTableSize.invoke(proxyFunction)), apply.invoke(instance, x, i),
                getMethodSignature(apply) + " returned a different value than what was expected");
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testGetTableSize(int tableSize) throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        otherToIntFunctionTest.setTableSize.invoke(proxyFunction, tableSize);

        assertEquals(tableSize, getTableSize.invoke(instance));
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testSetTableSize(int tableSize) throws ReflectiveOperationException {
        Object proxyFunction = Proxy.get(OtherToIntFunctionProxy.class), instance = newInstance(proxyFunction);

        otherToIntFunctionTest.setTableSize.invoke(proxyFunction, tableSize / 2);
        setTableSize.invoke(instance, tableSize);

        assertEquals(tableSize, otherToIntFunctionTest.getTableSize.invoke(proxyFunction),
                "Table size was not updated in " + function.getName());
    }
}
