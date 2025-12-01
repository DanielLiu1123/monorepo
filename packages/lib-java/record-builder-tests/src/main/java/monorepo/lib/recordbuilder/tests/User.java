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
public record User(
        String name,
        int age,
        @Nullable LocalDate birthday,
        List<String> hobbies,
        Map<String, String> attributes,
        List<@Nullable String> tags,
        Map<String, @Nullable Integer> scores,
        Set<String> roles) {}
