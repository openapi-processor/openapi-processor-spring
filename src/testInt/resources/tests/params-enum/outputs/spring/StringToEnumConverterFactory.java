package generated.spring;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

import java.util.EnumSet;
import java.util.function.Supplier;

public class StringToEnumConverterFactory<T extends Enum<T> & Supplier<String>>
    implements ConverterFactory<String, T> {

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public <E extends T> Converter<String, E> getConverter(Class<E> targetType) {
        return new StringToEnumConverter(targetType);
    }

    static class StringToEnumConverter<T extends Enum<T> & Supplier<String>> implements Converter<String, T> {

        private final Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            String sourceValue = source.trim();

            for (T e : EnumSet.allOf(enumType)) {
                if (e.get().equals(sourceValue)) {
                    return e;
                }
            }

            throw new IllegalArgumentException(String.format("No enum constant of %s has the value %s",
                enumType.getCanonicalName(),
                sourceValue));
        }
    }
}
