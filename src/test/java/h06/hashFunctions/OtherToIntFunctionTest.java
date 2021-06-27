package h06.hashFunctions;

import h06.util.TestClass;
import h06.util.Utils.TypeParameter;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static h06.util.Assertions.*;
import static java.lang.reflect.Modifier.*;

public class OtherToIntFunctionTest extends TestClass {

    public final Method apply, getTableSize, setTableSize;

    public OtherToIntFunctionTest() {
        super("h06.hashFunctions.OtherToIntFunction", null);

        apply = getMethodByName("apply(T)");
        getTableSize = getMethodByName("getTableSize()");
        setTableSize = getMethodByName("setTableSize(int)");
    }

    @Test
    @Override
    public void testDefinition() {
        // interface
        assertHasModifiers(testedClass, PUBLIC, INTERFACE);
        assertIsGeneric(testedClass, TypeParameter.of("T", UNBOUNDED));

        // methods
        assertHasModifiers(apply, ABSTRACT);
        assertReturnType(apply, int.class.getTypeName());

        assertHasModifiers(getTableSize, ABSTRACT);
        assertReturnType(getTableSize, int.class.getTypeName());

        assertHasModifiers(setTableSize, ABSTRACT);
        assertReturnType(setTableSize, void.class.getTypeName());
    }
}
