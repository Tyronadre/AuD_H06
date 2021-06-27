package h06.util.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static h06.util.Utils.RANDOM;

public class KeyValueProvider extends Provider {

    @Override
    protected Stream<? extends Arguments> provideDefaultArguments(ExtensionContext context) {
        return Stream.generate(() -> {
            int hashCodeKey = RANDOM.nextInt(10000), hashCodeValue = RANDOM.nextInt(10000);
            Object key = new Object() {
                @Override
                public int hashCode() {
                    return hashCodeKey;
                }
            }, value = new Object() {
                @Override
                public int hashCode() {
                    return hashCodeValue;
                }
            };

            return Arguments.of(key, value);
        });
    }
}
