package monorepo.lib.recordbuilder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to generate a builder for a Java record.
 * <p>
 * The generated builder will include:
 * <ul>
 *   <li>setXxx() methods for all fields</li>
 *   <li>addXxx() and addAllXxx() methods for List fields</li>
 *   <li>putXxx() and putAllXxx() methods for Map fields</li>
 *   <li>clearXxx() methods for all fields</li>
 *   <li>static of() method to create a new builder</li>
 *   <li>static from(record) method to create a builder from an existing record</li>
 *   <li>build() method to construct the final record</li>
 * </ul>
 * <p>
 * Non-null validation is applied by default unless the field is annotated with @Nullable.
 *
 * @author Freeman
 * @since 2025/11/30
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface RecordBuilder {}
