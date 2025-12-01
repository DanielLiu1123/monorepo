package monorepo.lib.recordbuilder.tests;

import java.util.List;
import javax.annotation.Nullable;
import monorepo.lib.recordbuilder.RecordBuilder;

/**
 * Test record using javax.annotation.Nullable.
 *
 * @author Freeman
 * @since 2025/12/01
 */
@RecordBuilder
public record Product(String name, @Nullable String description, List<String> tags) {}
