package h06.util.provider;

import h06.util.Utils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Method;
import java.util.stream.Stream;

import static h06.util.Config.NUMBER_OF_TEST_RUNS;
import static h06.util.Config.INJECTED_ARGUMENTS;

/**
 * Allows the injection of custom arguments if used as an argument source.
 */
abstract public class Provider implements ArgumentsProvider {

    /**
     * Returns the concatenation of injected and default argument streams and limits the stream size
     * to the number specified in {@link h06.util.Config#NUMBER_OF_TEST_RUNS}.
     * @inheritDoc
     */
    @Override
    public final Stream<? extends Arguments> provideArguments(ExtensionContext context) {
        Method method = context.getRequiredTestMethod();

        return Stream.concat(INJECTED_ARGUMENTS.getOrDefault(Utils.getMethodSignature(method), Stream.of()), provideDefaultArguments(context))
                     .limit(NUMBER_OF_TEST_RUNS.getOrDefault(Utils.getMethodSignature(method), 5));
    }

    /**
     * Provides a stream of arguments for {@link Provider#provideArguments(ExtensionContext)}
     * @param context the current extension context; never {@code null}
     * @return a stream of arguments
     * @see h06.util.Config#INJECTED_ARGUMENTS
     */
    abstract protected Stream<? extends Arguments> provideDefaultArguments(ExtensionContext context);
}
