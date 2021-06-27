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

public class HashCodeTableIndexFctTest extends TestClass {

    public final Field tableSize, offset;
    public final Method apply, getTableSize, setTableSize;

    public HashCodeTableIndexFctTest() {
        super("h06.hashFunctions.HashCodeTableIndexFct", constructor -> {
            Type[] types = constructor.getGenericParameterTypes();

            return types.length == 2 &&
                   types[0].getTypeName().equals(int.class.getTypeName()) &&
                   types[1].getTypeName().equals(int.class.getTypeName());
        });

        OtherToIntFunctionTest otherToIntFunctionTest = new OtherToIntFunctionTest();

        tableSize = getFieldByCriteria("tableSize", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && !isFinal(modifiers) && field.getType().equals(int.class);
        });
        offset = getFieldByCriteria("offset", field -> {
            int modifiers = field.getModifiers();

            return isPrivate(modifiers) && !isStatic(modifiers) && isFinal(modifiers) && field.getType().equals(int.class);
        });

        apply = otherToIntFunctionTest.apply;
        getTableSize = otherToIntFunctionTest.getTableSize;
        setTableSize = otherToIntFunctionTest.setTableSize;
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);
        assertDoesNotHaveModifiers(testedClass, ABSTRACT, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("T", UNBOUNDED));
        assertImplements(testedClass, "h06.hashFunctions.OtherToIntFunction<T>");

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);

        // fields
        assertHasModifiers(tableSize, PRIVATE);
        assertDoesNotHaveModifiers(tableSize, STATIC, FINAL);
        assertType(tableSize, int.class.getTypeName());

        assertHasModifiers(offset, PRIVATE, FINAL);
        assertDoesNotHaveModifiers(offset, STATIC);
        assertType(offset, int.class.getTypeName());
    }

    @Test
    @Override
    public void testInstance() throws IllegalAccessException {
        int tableSizeValue = RANDOM.nextInt(100), offsetValue = 2;
        Object instance = newInstance(tableSizeValue, offsetValue);

        assertEquals(tableSizeValue, tableSize.get(instance));
        assertEquals(offsetValue, offset.get(instance));
    }

    @ParameterizedTest
    @ArgumentsSource(IntFunctionProvider.class)
    public void testApply(int tableSize, int offset, Object object) throws ReflectiveOperationException {
        Object instance = newInstance(tableSize, offset);

        assertEquals(Math.floorMod(object.hashCode() + offset, tableSize), apply.invoke(instance, object));
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testGetTableSize(int tableSize) throws ReflectiveOperationException {
        Object instance = newInstance(tableSize, 0);

        assertEquals(tableSize, getTableSize.invoke(instance));
    }

    @ParameterizedTest
    @ArgumentsSource(TableSizeProvider.class)
    public void testSetTableSize(int tableSize) throws ReflectiveOperationException {
        Object instance = newInstance(tableSize / 2, 0);

        setTableSize.invoke(instance, tableSize);

        assertEquals(tableSize, this.tableSize.get(instance));
    }
}
