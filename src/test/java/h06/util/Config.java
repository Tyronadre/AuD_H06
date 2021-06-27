package h06.util;

import org.junit.jupiter.params.provider.Arguments;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains constants that are used as settings for the tests.
 * They will not be overwritten when an update is downloaded (assuming this file is in {@link Config#EXCLUDED_FILES}),
 * but will be updated is such a way that everything between the lines containing ">>>##" and "##<<<" will be kept.
 * This includes any changes or appended code.
 */
public class Config {

    // >>>## UPDATE MARKER, DO NOT REMOVE, ONLY MODIFY THE LINES BELOW

    /**
     * Seed that is used for initialization of {@link Utils#RANDOM}. <br>
     * Set to a fixed value other than {@code null} for (hopefully) reproducible results.
     */
    public static final Long SEED = null;

    /**
     * Settings for the updater <br>
     * Set the values of these constants to {@code true} or {@code false} respectively, if you want or don't want to...
     * <ul>
     *     <li>{@code CHECK_FOR_UPDATES} - use the functionality at all</li>
     *     <li>{@code CHECK_HASHES} - compare the hashes of local files with the ones in the repository</li>
     *     <li>{@code AUTO_UPDATE} - let the updater download files from the repository and overwrite the local files automatically</li>
     * </ul>
     * @see Updater
     */
    public static final boolean CHECK_FOR_UPDATES = true, CHECK_HASHES = true, AUTO_UPDATE = true;

    /**
     * A list of files (with path relative to project root) to be excluded from updates.
     * This does not prevent updates to this configuration file ({@link Config#AUTO_UPDATE} does that),
     * it just prevents complete overwrites.
     * @see Config
     */
    public static final List<String> EXCLUDED_FILES = List.of(
            "src/test/java/h06/util/Config.java"
    );

    /**
     * Allows customization of the number of test runs for a parameterized test.
     * To override the number of runs, add an entry consisting of the type name + '#' + the method signature
     * (again with type names) mapped to an integer value (example below).
     * If the method is not in this map, a default value of 5 is used.
     * @see Utils#getMethodSignature(Method)
     */
    public static final Map<String, Integer> NUMBER_OF_TEST_RUNS = Map.of(
            // "h06.AbcTest#testMethod(java.lang.String)", 10
    );

    /**
     * Allows injection of custom arguments into a parameterized test.
     * The number and types of arguments must match the ones expected by the test method.
     * The syntax is similar to {@link Config#NUMBER_OF_TEST_RUNS}, except that the value is a stream of arguments.
     * If the number of arguments in the stream are greater than the number of test runs, they will be cut off at that value.
     * The first arguments in the stream passed to the test will be the injected ones.
     * @see Utils#getMethodSignature(Method)
     */
    public static final Map<String, Stream<? extends Arguments>> INJECTED_ARGUMENTS = Map.of(
            // "h06.AbcTest#testMethod(java.lang.String)", Stream.of(Arguments.of(..., ...), ...)
    );

    /**
     * Allows to set the name of a field manually if it can't be found by {@link TestClass#getFieldByCriteria(String, Predicate)}.
     * The key is the type name of the test class + '#' + the identifier (aka the field name in the test class)
     */
    public static final Map<String, String> FIELD_NAME = Map.of(
            // "h06.AbcTest#identifier", "testField"
    );

    // ##<<< UPDATE MARKER, DO NOT REMOVE, DO NOT CHANGE ANYTHING BELOW THIS LINE

    /**
     * Returns a set of the names of all fields in this class.
     * @return the set
     */
    public static Set<String> getConfigs() {
        return Arrays.stream(Config.class.getDeclaredFields()).map(Field::getName).collect(Collectors.toUnmodifiableSet());
    }
}
