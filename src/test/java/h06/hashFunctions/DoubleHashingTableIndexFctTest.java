package h06.hashFunctions;

import h06.util.TestClass;
import h06.util.provider.IntFunctionProvider;
import h06.util.provider.TableSizeProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import static h06.util.Assertions.*;
import static h06.util.Utils.*;
import static java.lang.reflect.Modifier.*;

public class DoubleHashingTableIndexFctTest extends TestClass {

    public final Field fct1, fct2;
    public final Method apply, getTableSize, setTableSize;

    private final HashCodeTableIndexFctTest hashCodeTableIndexFctTest;
    private final OtherToIntFunctionTest otherToIntFunctionTest;

    public DoubleHashingTableIndexFctTest() {
        super("h06.hashFunctions.DoubleHashingTableIndexFct", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 2 &&
                   types[0].getTypeName().equals("h06.hashFunctions.HashCodeTableIndexFct<T>") &&
                   types[1].getTypeName().equals("h06.hashFunctions.HashCodeTableIndexFct<T>");
        });

        hashCodeTableIndexFctTest = new HashCodeTableIndexFctTest();
        otherToIntFunctionTest = new OtherToIntFunctionTest();
        OtherAndIntToIntFunctionTest otherAndIntToIntFunctionTest = new OtherAndIntToIntFunctionTest();

        fct1 = getFieldByName("fct1");
        fct2 = getFieldByName("fct2");

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
        assertHasModifiers(fct1, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(fct1, STATIC);
        assertType(fct1, hashCodeTableIndexFctTest.className + "<T>");

        assertHasModifiers(fct2, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(fct2, STATIC);
        assertType(fct2, hashCodeTableIndexFctTest.className + "<T>");
    }

    @Test
    @Override
    public void testInstance() throws ReflectiveOperationException {
        int tableSize = RANDOM.nextInt(10) + 1;

        Object hashCodeTableIndexFctInstance1 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               hashCodeTableIndexFctInstance2 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               instance = newInstance(hashCodeTableIndexFctInstance1, hashCodeTableIndexFctInstance2);

        // variables initialized correctly
        assertSame(hashCodeTableIndexFctInstance1, fct1.get(instance), "Wrong object assigned to fct1");
        assertSame(hashCodeTableIndexFctInstance2, fct2.get(instance), "Wrong object assigned to fct2");
    }

    @ParameterizedTest
    @ArgumentsSource(IntFunctionProvider.class)
    public void testApply(int tableSize, int i, Object x) throws ReflectiveOperationException {
        Object hashCodeTableIndexFctInstance1 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               hashCodeTableIndexFctInstance2 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               instance = newInstance(hashCodeTableIndexFctInstance1, hashCodeTableIndexFctInstance2);
        int a = (int) otherToIntFunctionTest.apply.invoke(hashCodeTableIndexFctInstance1, x),
            b = (int) otherToIntFunctionTest.apply.invoke(hashCodeTableIndexFctInstance2, x);

        assertEquals(Math.floorMod(a + i * b, tableSize), apply.invoke(instance, x, i),
                getMethodSignature(apply) + " returned a different value than what was expected");
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testGetTableSize(int tableSize) throws ReflectiveOperationException {
        Object hashCodeTableIndexFctInstance1 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               hashCodeTableIndexFctInstance2 = hashCodeTableIndexFctTest.newInstance(tableSize, 0),
               instance = newInstance(hashCodeTableIndexFctInstance1, hashCodeTableIndexFctInstance2);

        assertEquals(tableSize, getTableSize.invoke(instance));
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testSetTableSize(int tableSize) throws ReflectiveOperationException {
        Object hashCodeTableIndexFctInstance1 = hashCodeTableIndexFctTest.newInstance(tableSize / 2, 0),
               hashCodeTableIndexFctInstance2 = hashCodeTableIndexFctTest.newInstance(tableSize / 2, 0),
               instance = newInstance(hashCodeTableIndexFctInstance1, hashCodeTableIndexFctInstance2);

        setTableSize.invoke(instance, tableSize);

        assertEquals(tableSize, otherToIntFunctionTest.getTableSize.invoke(hashCodeTableIndexFctInstance1),
                "Table size was not updated in fct1");
        assertEquals(tableSize, otherToIntFunctionTest.getTableSize.invoke(hashCodeTableIndexFctInstance2),
                "Table size was not updated in fct2");
    }
}
