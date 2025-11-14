package monorepo.lib.msp;

import static org.mapstruct.CollectionMappingStrategy.TARGET_IMMUTABLE;
import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;
import static org.mapstruct.ReportingPolicy.ERROR;

import org.mapstruct.MapperConfig;

/**
 * MapStruct universal configuration based on best practices.
 *
 * @author Freeman
 * @since 2025/11/15
 */
@MapperConfig(
        uses = ProtobufWellKnownTypeMappers.class,
        nullValueCheckStrategy = ALWAYS,
        nullValuePropertyMappingStrategy = IGNORE,
        collectionMappingStrategy = TARGET_IMMUTABLE,
        unmappedTargetPolicy = ERROR)
public interface MapStructConfig {}
