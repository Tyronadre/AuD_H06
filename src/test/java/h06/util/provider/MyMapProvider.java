package h06.util.provider;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static h06.util.Utils.RANDOM;

public class MyMapProvider extends Provider {

    @Override
    protected Stream<? extends Arguments> provideDefaultArguments(ExtensionContext context) {
        return Stream.generate(() -> Arguments.of(
                RANDOM.ints(RANDOM.nextInt(20) + 1, 1, 100000)
                      .mapToObj(hashCode -> new Object() {

                          @Override
                          public int hashCode() {
                              return hashCode;
                          }

                          @Override
                          @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
                          public boolean equals(Object obj) {
                              if (obj == null)
                                  return false;

                              return obj.hashCode() == this.hashCode();
                          }
                      })
                      .distinct()
                      .collect(Collectors.toUnmodifiableList())));
    }
}
