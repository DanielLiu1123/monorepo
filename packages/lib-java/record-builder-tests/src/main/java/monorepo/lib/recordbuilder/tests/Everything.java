package monorepo.lib.recordbuilder.tests;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import monorepo.lib.recordbuilder.RecordBuilder;
import org.jspecify.annotations.Nullable;

/**
 * Test record for RecordBuilder annotation processor.
 *
 * @author Freeman
 * @since 2025/11/30
 */
@RecordBuilder
public record Everything(
        // all primitive types
        byte byte_,
        @Nullable Short short_,
        int int_,
        long long_,
        float float_,
        Double double_,
        char char_,
        Boolean boolean_,

        // reference types
        String string,
        @Nullable String nullableString,
        LocalDate localDate,
        @Nullable LocalDate nullableLocalDate,

        // collection types
        List<String> listString,
        @Nullable List<String> nullableListString,
        List<@Nullable String> listNullableString,
        Set<String> setString,
        @Nullable Set<String> nullableSetString,
        Set<@Nullable String> setNullableString,
        Map<String, Integer> mapStringInteger,
        @Nullable Map<String, Integer> nullableMapStringInteger,
        Map<@Nullable String, Integer> mapNullableStringInteger,
        Map<String, @Nullable Integer> mapStringNullableInteger,
        Map<@Nullable String, @Nullable Integer> mapNullableStringNullableInteger) {}
