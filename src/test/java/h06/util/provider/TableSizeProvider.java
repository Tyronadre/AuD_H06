package h06.util.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

import static h06.util.Utils.RANDOM;

public class TableSizeProvider extends Provider {

    @Override
    protected Stream<? extends Arguments> provideDefaultArguments(ExtensionContext context) {
        return Stream.generate(() -> Arguments.of(RANDOM.nextInt(99) + 2));
    }
}
