package monorepo.lib.msp;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.MapperConfig;

/**
 * MapStruct universal configuration based on best practices.
 *
 * <p> Example:
 * <pre>{@code
 * @Mapper(config = MapStructConfig.class)
 * public interface MyMapper {
 *    // mapping methods
 * }
 * }</pre>
 *
 * @author Freeman
 * @since 2025/11/15
 */
@MapperConfig(
        uses = ProtobufConverter.class,
        nullValueCheckStrategy = ALWAYS,
        nullValuePropertyMappingStrategy = IGNORE, // Ignore nulls when updating existing objects (using @MappingTarget)
        collectionMappingStrategy = TARGET_IMMUTABLE, // Immutable first
        unmappedTargetPolicy = ERROR // Fail on unmapped target properties
        )
public final class MapStructConfig {}
