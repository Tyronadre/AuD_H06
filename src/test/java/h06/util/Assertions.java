package h06.util;

import org.opentest4j.AssertionFailedError;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.reflect.Modifier.*;

@SuppressWarnings("unused")
public class Assertions extends org.junit.jupiter.api.Assertions {

    // ----------------------------------- //
    // DO NOT CHANGE ANYTHING IN THIS FILE //
    // ----------------------------------- //

    public static final String[] UNBOUNDED = new String[] {Object.class.getTypeName()};

    private static final Map<Integer, Object[]> MODIFIER_METHODS;

    static {
        Map<Integer, Object[]> modifierMethodsTmp = null;

        try {
            modifierMethodsTmp = new HashMap<>() {{
                put(PUBLIC,       new Object[] {Modifier.class.getDeclaredMethod("isPublic", int.class), "public"});
                put(PRIVATE,      new Object[] {Modifier.class.getDeclaredMethod("isPrivate", int.class), "private"});
                put(PROTECTED,    new Object[] {Modifier.class.getDeclaredMethod("isProtected", int.class), "protected"});
                put(STATIC,       new Object[] {Modifier.class.getDeclaredMethod("isStatic", int.class), "static"});
                put(FINAL,        new Object[] {Modifier.class.getDeclaredMethod("isFinal", int.class), "final"});
                put(SYNCHRONIZED, new Object[] {Modifier.class.getDeclaredMethod("isSynchronized", int.class), "synchronized"});
                put(VOLATILE,     new Object[] {Modifier.class.getDeclaredMethod("isVolatile", int.class), "volatile"});
                put(TRANSIENT,    new Object[] {Modifier.class.getDeclaredMethod("isTransient", int.class), "transient"});
                put(NATIVE,       new Object[] {Modifier.class.getDeclaredMethod("isNative", int.class), "native"});
                put(INTERFACE,    new Object[] {Modifier.class.getDeclaredMethod("isInterface", int.class), "an interface"});
                put(ABSTRACT,     new Object[] {Modifier.class.getDeclaredMethod("isAbstract", int.class), "abstract"});
                put(STRICT,       new Object[] {Modifier.class.getDeclaredMethod("isStrict", int.class), "strict"});
            }};
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        MODIFIER_METHODS = modifierMethodsTmp;
    }

    /**
     * Assert that the given member has the specified modifiers.
     * <br><br>
     * Calls {@link Assertions#assertHasModifiers(Member, String, Integer...)} with {@code null} as error
     * message (default error message)
     * @param member    the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     * @throws IllegalArgumentException if a value does not correspond to a modifier
     */
    public static void assertHasModifiers(Member member, Integer... modifiers) {
        assertHasModifiers(member, null, modifiers);
    }

    /**
     * Assert that the given member has the specified modifiers.
     * @param member    the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     * @param errorMsg  the error message to pass to {@link AssertionError}
     * @throws IllegalArgumentException if a value does not correspond to a modifier
     */
    public static void assertHasModifiers(Member member, String errorMsg, Integer... modifiers) {
        try {
            for (Integer modifier : modifiers) {
                Object[] value = MODIFIER_METHODS.getOrDefault(modifier, null);

                if (value == null)
                    throw new IllegalArgumentException(String.valueOf(modifier));

                Method method = (Method) value[0];
                String msgSuffix = (String) value[1];

                if (arrayContains(modifier, modifiers))
                    assertTrue((Boolean) method.invoke(null, member.getModifiers()),
                            errorMsg != null ? errorMsg : member.getName() + " must be " + msgSuffix);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assert that the given class has the specified modifiers.
     * <br><br>
     * Calls {@link Assertions#assertHasModifiers(Class, String, Integer...)} with {@code null} as error
     * message (default error message)
     * @param c         the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     */
    public static void assertHasModifiers(Class<?> c, Integer... modifiers) {
        assertHasModifiers(c, null, modifiers);
    }

    /**
     * Assert that the given class has the specified modifiers.
     * @param c         the member to check the modifiers of
     * @param errorMsg  the error message to pass to {@link AssertionError}
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     */
    public static void assertHasModifiers(Class<?> c, String errorMsg, Integer... modifiers) {
        try {
            for (Integer modifier : modifiers) {
                Object[] value = MODIFIER_METHODS.getOrDefault(modifier, null);

                if (value == null)
                    throw new IllegalArgumentException(String.valueOf(modifier));

                Method method = (Method) value[0];
                String msgSuffix = (String) value[1];

                if (arrayContains(modifier, modifiers))
                    assertTrue((Boolean) method.invoke(null, c.getModifiers()),
                            errorMsg != null ? errorMsg : c.getName() + " must be " + msgSuffix);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assert that the given member does not have the specified modifiers.
     * <br><br>
     * Calls {@link Assertions#assertDoesNotHaveModifiers(Member, String, Integer...)} with {@code null} as error
     * message (default error message)
     * @param member    the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     * @throws IllegalArgumentException if a value does not correspond to a modifier
     */
    public static void assertDoesNotHaveModifiers(Member member, Integer... modifiers) {
        assertDoesNotHaveModifiers(member, null, modifiers);
    }

    /**
     * Assert that the given member does not have the specified modifiers.
     * @param member    the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     * @param errorMsg  the error message to pass to {@link AssertionError}
     * @throws IllegalArgumentException if a value does not correspond to a modifier
     */
    public static void assertDoesNotHaveModifiers(Member member, String errorMsg, Integer... modifiers) {
        try {
            for (Integer modifier : modifiers) {
                Object[] value = MODIFIER_METHODS.getOrDefault(modifier, null);

                if (value == null)
                    throw new IllegalArgumentException(String.valueOf(modifier));

                Method method = (Method) value[0];
                String msgSuffix = (String) value[1];

                if (arrayContains(modifier, modifiers))
                    assertFalse((Boolean) method.invoke(null, member.getModifiers()),
                            errorMsg != null ? errorMsg : member.getName() + " must not be " + msgSuffix);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assert that the given class does not have the specified modifiers.
     * <br><br>
     * Calls {@link Assertions#assertDoesNotHaveModifiers(Class, String, Integer...)} with {@code null} as error
     * message (default error message)
     * @param c         the member to check the modifiers of
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     */
    public static void assertDoesNotHaveModifiers(Class<?> c, Integer... modifiers) {
        assertDoesNotHaveModifiers(c, null, modifiers);
    }

    /**
     * Assert that the given class does not have the specified modifiers.
     * @param c         the member to check the modifiers of
     * @param errorMsg  the error message to pass to {@link AssertionError}
     * @param modifiers a list of modifiers (constants of {@link Modifier})
     */
    public static void assertDoesNotHaveModifiers(Class<?> c, String errorMsg, Integer... modifiers) {
        try {
            for (Integer modifier : modifiers) {
                Object[] value = MODIFIER_METHODS.getOrDefault(modifier, null);

                if (value == null)
                    throw new IllegalArgumentException(String.valueOf(modifier));

                Method method = (Method) value[0];
                String msgSuffix = (String) value[1];

                if (arrayContains(modifier, modifiers))
                    assertFalse((Boolean) method.invoke(null, c.getModifiers()),
                            errorMsg != null ? errorMsg : c.getName() + " must not be " + msgSuffix);
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Assert that the given class is generic and its type parameters have the given bounds
     * @param c                      the class to check
     * @param expectedTypeParameters an array of expected type parameters and their bounds
     */
    public static void assertIsGeneric(Class<?> c, Utils.TypeParameter... expectedTypeParameters) {
        int i = 0;
        TypeVariable<?>[] actualTypeParameters = c.getTypeParameters();

        assertNotEquals(0, actualTypeParameters.length, c.getName() + " must be generic");
        assertEquals(expectedTypeParameters.length, actualTypeParameters.length,
                c.getName() + " must have exactly " + expectedTypeParameters.length + " type parameters");

        for (Utils.TypeParameter expectedTypeParameter : expectedTypeParameters) {
            TypeVariable<?> actualTypeParameter = actualTypeParameters[i++];

            assertEquals(expectedTypeParameter.name, actualTypeParameter.getTypeName(), "Unexpected type parameter");
            assertEquals(expectedTypeParameter.bounds.length, actualTypeParameter.getBounds().length, "Number of bounds differ");

            for (String bound : expectedTypeParameter.bounds)
                if (!arrayContains(bound, Arrays.stream(actualTypeParameter.getBounds()).map(Type::getTypeName).toArray()))
                    throw new AssertionFailedError(
                            c.getName() + " is missing a required bound for type parameter " + expectedTypeParameter.name,
                            bound, null);
        }
    }

    /**
     * Assert that the given class is not generic
     * @param c the class to check
     */
    public static void assertNotGeneric(Class<?> c) {
        assertEquals(0, c.getTypeParameters().length, c.getName() + " must not be generic");
    }

    /**
     * Assert that the given field has the type {@code typeName}
     * @param field    the field to check
     * @param typeName the type (type name)
     */
    public static void assertType(Field field, String typeName) {
        assertEquals(typeName, field.getGenericType().getTypeName(), "Type of field " + field.getName() + " is incorrect");
    }

    /**
     * Assert that the given method has the return type {@code typeName}
     * @param method   the method to check
     * @param typeName the return type (type name)
     */
    public static void assertReturnType(Method method, String typeName) {
        assertEquals(typeName, method.getGenericReturnType().getTypeName(), "Return type of method " + method.getName() + " is incorrect");
    }

    /**
     * Assert that a class implements all given interfaces
     * @param c          the class to check
     * @param interfaces the interfaces the class has to implement (type name)
     */
    public static void assertImplements(Class<?> c, String... interfaces) {
        List<String> actualInterfaces = Arrays.stream(c.getGenericInterfaces()).map(Type::getTypeName).collect(Collectors.toList());

        for (String intf : interfaces)
            if (!actualInterfaces.contains(intf))
                throw new AssertionFailedError("Required interface not implemented in " + c.getName(), intf, null);
    }

    private static boolean arrayContains(Object needle, Object[] stack) {
        for (Object stackElement : stack)
            if (Objects.equals(needle, stackElement))
                return true;

        return false;
    }
}
