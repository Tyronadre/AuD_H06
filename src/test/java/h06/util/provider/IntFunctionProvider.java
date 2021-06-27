package h06.util.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static h06.util.Utils.RANDOM;

public class IntFunctionProvider extends Provider {

    @Override
    protected Stream<? extends Arguments> provideDefaultArguments(ExtensionContext context) {
        return Stream.generate(() -> {
            int tableSize = RANDOM.nextInt(100) + 1, offset = RANDOM.nextInt(100), hashCode = RANDOM.nextInt(10000);
            Object object = new Object() {
                @Override
                public int hashCode() {
                    return hashCode;
                }
            };

            return Arguments.of(tableSize, offset, object);
        });
    }
}
