package h06.test;

import h06.util.TestClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static h06.util.Assertions.assertHasModifiers;
import static java.lang.reflect.Modifier.PUBLIC;

public class RuntimeTestTest extends TestClass {

    public final Method Test;

    public RuntimeTestTest() {
        super("h06.test.RuntimeTest", constructor -> constructor.getGenericParameterTypes().length == 0);

        Test = getMethodByName("Test(int, int, int, int)");
    }

    @Test
    @Override
    public void testDefinition() {
        // class
        assertHasModifiers(testedClass, PUBLIC);

        // constructor
        assertHasModifiers(testedClassConstructor, PUBLIC);

        // methods
        assertHasModifiers(Test, PUBLIC);
    }
}
