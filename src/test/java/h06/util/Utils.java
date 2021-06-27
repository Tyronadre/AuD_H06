package h06.util;

import org.opentest4j.TestAbortedException;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static h06.util.Config.CHECK_FOR_UPDATES;
import static h06.util.Updater.ASSIGNMENT_ID;

public class Utils {

    // ----------------------------------- //
    // DO NOT CHANGE ANYTHING IN THIS FILE //
    // ----------------------------------- //

    @SuppressWarnings("ConstantConditions")
    private static final long SEED = Config.SEED == null ? new Random().nextLong() : Config.SEED;

    public static final Random RANDOM = new Random(SEED);

    static {
        System.out.println("Reminder: remove these tests before submitting!");

        if (!CHECK_FOR_UPDATES || !Updater.checkForUpdates()) {
            System.out.println("Seed: " + SEED);
        } else {
            System.out.println("Updated tests, please re-run");

            System.exit(0);
        }
    }

    /**
     * Returns the class object for a given name or throws an {@link TestAbortedException} if it is not found
     * @param className the fully qualified name of the class
     * @return the class object for the corresponding name
     */
    public static Class<?> getClassForName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new TestAbortedException("Class " + e.getMessage() + " not found", e);
        }
    }

    /**
     * Returns the signature of the given method, appended to the fully qualified class name and '#'.
     * The returned String for this method would be: <br>
     * "{@code h06.util.Utils#getMethodSignature(java.lang.reflect.Method)}"
     * @param method the method
     * @return the fully qualified class name, followed by '#' and the method signature
     */
    public static String getMethodSignature(Method method) {
        return method.getDeclaringClass().getTypeName() + '#' +
               method.getName() + '(' + Arrays.stream(method.getGenericParameterTypes())
                                              .map(Type::getTypeName)
                                              .collect(Collectors.joining(", ")) +
                                  ')';
    }

    public static class TypeParameter {

        final String name;
        final String[] bounds;

        private TypeParameter(String typeParameterName, String[] typeParameterBounds) {
            this.name = typeParameterName;
            this.bounds = typeParameterBounds;
        }

        public static TypeParameter of(String typeParameterName, String[] typeParameterBounds) {
            return new TypeParameter(typeParameterName, typeParameterBounds);
        }
    }

    /**
     * An exception class to indicate that a member was not found when searching by name or criteria
     * @see TestClass
     */
    public static class MissingMemberException extends RuntimeException {

        private final String message;

        public MissingMemberException(String message) {
            this.message = message;

            setStackTrace(Arrays.stream(getStackTrace())
                                .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith(ASSIGNMENT_ID.toLowerCase()))
                                .toArray(StackTraceElement[]::new));
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}
