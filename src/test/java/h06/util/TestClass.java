package h06.util;

import h06.util.Utils.MissingMemberException;
import org.opentest4j.AssertionFailedError;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static h06.util.Config.FIELD_NAME;
import static h06.util.Utils.getClassForName;
import static h06.util.Utils.getMethodSignature;

/**
 * Common class for tests
 */
@SuppressWarnings("unused")
abstract public class TestClass {

    // ----------------------------------- //
    // DO NOT CHANGE ANYTHING IN THIS FILE //
    // ----------------------------------- //

    public final String className;
    public final Class<?> testedClass;
    public final Constructor<?> testedClassConstructor;
    public final Map<String, Field> fields;
    public final Map<String, Method> methods;

    /**
     * Initializes a test class
     * @param className            the name of the class / interface to be tested
     * @param constructorPredicate a predicate to find a constructor of the tested class, may be null to not set any
     */
    protected TestClass(String className, Predicate<Constructor<?>> constructorPredicate) {
        this.className = className;
        this.testedClass = getClassForName(className);
        this.testedClassConstructor = constructorPredicate == null ?
                null :
                Arrays.stream(testedClass.getDeclaredConstructors())
                      .filter(constructorPredicate)
                      .findFirst()
                      .orElseThrow(() -> new MissingMemberException(className + " is missing a constructor matching all given criteria"));
        this.fields = new HashMap<>() {{
            for (Field field : testedClass.getDeclaredFields())
                if (!field.isSynthetic()) {
                    field.setAccessible(true);

                    put(field.getName(), field);
                }
        }};
        this.methods = new HashMap<>() {{
            for (Method method : testedClass.getDeclaredMethods())
                if (!method.isSynthetic()) {
                    method.setAccessible(true);

                    put(getMethodSignature(method), method);
                }
        }};

        if (this.testedClassConstructor != null)
            this.testedClassConstructor.setAccessible(true);
    }

    /**
     * Tests the definition of {@link TestClass#testedClass}.
     * Implementations must be annotated with {@link org.junit.jupiter.api.Test}.
     * @throws AssertionFailedError if any assertion failed
     */
    abstract public void testDefinition();

    /**
     * Tests an instance of {@link TestClass#testedClass}.
     * Implementations must be annotated with {@link org.junit.jupiter.api.Test}.
     * @throws AssertionFailedError if any assertion failed
     */
    public void testInstance() throws Exception {
        throw new UnsupportedOperationException("This test class does not support tests on an instance of " + className);
    }

    /**
     * Get a field for a given name
     * @param fieldName the name of the field
     * @return the field
     * @throws RuntimeException if the field does not exist under that name
     */
    public Field getFieldByName(String fieldName) {
        Field field = fields.get(fieldName);

        if (field == null)
            throw new MissingMemberException("Field " + fieldName + " was not found in " + className);

        return field;
    }

    /**
     * Get a field matching a given predicate
     * @param identifier a string identifying a field in the test class
     * @param predicate  the predicate to test with
     * @return the field
     * @throws RuntimeException if the predicate doesn't match any fields
     */
    public Field getFieldByCriteria(String identifier, Predicate<Field> predicate) {
        if (FIELD_NAME.containsKey(getClass().getTypeName() + '#' + identifier))
            return getFieldByName(FIELD_NAME.get(getClass().getTypeName() + '#' + identifier));

        return fields.values()
                     .stream()
                     .filter(predicate)
                     .findFirst()
                     .orElseThrow(() -> new MissingMemberException("No fields matching the given predicate have been found in " + className));
    }

    /**
     * Get a method for a given signature
     * @param methodSignature the name of the method
     * @return the field
     * @throws RuntimeException if the method does not exist under that name
     */
    public Method getMethodByName(String methodSignature) {
        Method method = methods.get(className + '#' + methodSignature);

        if (method == null)
            throw new MissingMemberException("Method " + methodSignature + " was not found in " + className);

        return method;
    }

    /**
     * Get a method matching a given predicate
     * @param predicate the predicate to test with
     * @return the method
     * @throws RuntimeException if the predicate doesn't match any methods
     */
    public Method getMethodByCriteria(String identifier, Predicate<Method> predicate) {
        return methods.values()
                      .stream()
                      .filter(predicate)
                      .findFirst()
                      .orElseThrow(() -> new MissingMemberException("No methods matching the given predicate have been found in " + className));
    }

    /**
     * Create a new instance of the tested class
     * @param params the parameters to supply to the constructor
     * @return the instance
     */
    public Object newInstance(Object... params) {
        try {
            return testedClassConstructor.newInstance(params);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }
}
