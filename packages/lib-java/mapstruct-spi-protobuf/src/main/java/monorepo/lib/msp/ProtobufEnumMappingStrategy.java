package monorepo.lib.msp;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.mapstruct.MappingConstants;
import org.mapstruct.ap.spi.DefaultEnumMappingStrategy;
import org.mapstruct.ap.spi.MapStructProcessingEnvironment;

/**
 * MapStruct SPI implementation for Protocol Buffer enum mapping strategy.
 * <p>
 * This strategy handles the automatic mapping of Protobuf-generated enums according to
 * Google's protobuf style guide, which recommends:
 * <ul>
 *   <li>Enum constant names should be UPPER_SNAKE_CASE</li>
 *   <li>Enum constant names should be prefixed with the enum type name</li>
 *   <li>The first enum value should be a zero value with suffix "_UNSPECIFIED"</li>
 * </ul>
 *
 * <p>Example protobuf enum:
 * <pre>
 * enum Status {
 *   STATUS_UNSPECIFIED = 0;  // Maps to null in target
 *   STATUS_ACTIVE = 1;       // Maps to ACTIVE
 *   STATUS_INACTIVE = 2;     // Maps to INACTIVE
 * }
 * </pre>
 *
 * <p>Configuration via MapStruct processor options:
 * <pre>
 * mapstruct.protobuf.enumPostfixOverrides=com.example.MyEnum=UNKNOWN,com.example.OtherEnum=NONE
 * </pre>
 *
 * @author MapStruct SPI Protobuf
 */
public class ProtobufEnumMappingStrategy extends DefaultEnumMappingStrategy {

    /**
     * Default postfix for the zero-value enum constant.
     * Following Google's protobuf style guide.
     */
    private static final String DEFAULT_ENUM_POSTFIX = "UNSPECIFIED";

    /**
     * Special constant name for unparsable enum values in protobuf.
     */
    private static final String UNPARSABLE_ENUM_CONSTANT_UNRECOGNIZED = "UNRECOGNIZED";

    /**
     * Full interface name for protobuf enums (full version).
     */
    private static final String PROTOBUF_ENUM_INTERFACE = "com.google.protobuf.ProtocolMessageEnum";

    /**
     * Full interface name for protobuf enums (lite version).
     */
    private static final String PROTOBUF_LITE_ENUM_INTERFACE = "com.google.protobuf.Internal.EnumLite";

    /**
     * Cache of known enum types to avoid repeated reflection checks.
     */
    private static final Map<TypeElement, Boolean> KNOWN_ENUMS = new HashMap<>();

    /**
     * Processor option key for enum postfix overrides.
     */
    private static final String OPTION_ENUM_POSTFIX_OVERRIDES = "mapstruct.protobuf.enumPostfixOverrides";

    /**
     * Overrides for the zero-value enum constant postfix, keyed by enum package/class prefix.
     */
    private Map<String, String> enumPostfixOverrides;

    /**
     * MapStruct processing environment.
     */
    private MapStructProcessingEnvironment processingEnv;

    @Override
    public void init(MapStructProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        this.processingEnv = processingEnvironment;
    }

    /**
     * Gets the postfix for the zero-value enum constant.
     * <p>
     * Checks for overrides configured via processor options, otherwise returns the default.
     *
     * @param enumType the enum type
     * @return the postfix to use for the zero-value constant
     */
    private String getEnumPostfix(TypeElement enumType) {
        if (enumPostfixOverrides == null) {
            initEnumPostfixOverrides();
        }
        String enumTypeName = enumType.getQualifiedName().toString();
        Optional<String> override = enumPostfixOverrides.keySet().stream()
                .filter(enumTypeName::startsWith)
                .map(enumPostfixOverrides::get)
                .findFirst();
        return override.orElse(DEFAULT_ENUM_POSTFIX);
    }

    /**
     * Initializes enum postfix overrides from MapStruct processor options.
     * <p>
     * Format: "com.example.package=UNKNOWN,com.example.OtherEnum=NONE"
     */
    private void initEnumPostfixOverrides() {
        Map<String, String> options = processingEnv != null ? processingEnv.getOptions() : Map.of();
        String overridesOption = options.get(OPTION_ENUM_POSTFIX_OVERRIDES);

        if (overridesOption != null && !overridesOption.trim().isEmpty()) {
            enumPostfixOverrides = Arrays.stream(overridesOption.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty() && s.contains("="))
                    .map(override -> override.split("=", 2))
                    .collect(Collectors.toMap(args -> args[0].trim(), args -> args[1].trim()));
        } else {
            enumPostfixOverrides = Map.of();
        }
    }

    /**
     * Determines if a source enum value should be mapped to null.
     * <p>
     * Returns true for:
     * <ul>
     *   <li>UNRECOGNIZED - special protobuf constant for unparsable values</li>
     *   <li>XXX_UNSPECIFIED - the zero-value constant (or configured postfix)</li>
     * </ul>
     *
     * @param enumType the enum type
     * @param sourceEnumValue the source enum constant name
     * @return true if the value should map to null
     */
    public boolean isMapEnumConstantToNull(TypeElement enumType, String sourceEnumValue) {
        if (isProtobufEnum(enumType)) {
            // UNRECOGNIZED is a special protobuf constant for unparsable values
            if (UNPARSABLE_ENUM_CONSTANT_UNRECOGNIZED.equals(sourceEnumValue)) {
                return true;
            }
            // Check if this is the zero-value constant (e.g., STATUS_UNSPECIFIED)
            String trimmedEnumValue = removeEnumNamePrefixFromConstant(enumType, sourceEnumValue);
            if (getEnumPostfix(enumType).equals(trimmedEnumValue)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDefaultNullEnumConstant(TypeElement enumType) {
        if (isProtobufEnum(enumType)) {
            return addEnumNamePrefixToConstant(enumType, getEnumPostfix(enumType));
        }
        return null;
    }

    @Override
    public String getEnumConstant(TypeElement enumType, String sourceEnumValue) {
        if (isProtobufEnum(enumType)) {
            if (isMapEnumConstantToNull(enumType, sourceEnumValue)) {
                return MappingConstants.NULL;
            } else if (sourceEnumValue == null) {
                return getDefaultNullEnumConstant(enumType);
            }
            return removeEnumNamePrefixFromConstant(enumType, sourceEnumValue);
        }
        return sourceEnumValue;
    }

    /**
     * Adds the enum type name prefix to a constant name.
     * <p>
     * Example: Status + ACTIVE -> STATUS_ACTIVE
     *
     * @param enumType the enum type
     * @param constant the constant name without prefix
     * @return the constant name with prefix
     */
    private String addEnumNamePrefixToConstant(TypeElement enumType, String constant) {
        String enumName = enumType.getSimpleName().toString();
        String prefix = camelToUpperSnakeCase(enumName);
        return prefix + "_" + constant;
    }

    /**
     * Removes the enum type name prefix from a constant name.
     * <p>
     * Example: STATUS_ACTIVE -> ACTIVE
     *
     * @param enumType the enum type
     * @param sourceEnumValue the source constant name with prefix
     * @return the constant name without prefix
     */
    private String removeEnumNamePrefixFromConstant(TypeElement enumType, String sourceEnumValue) {
        String enumName = enumType.getSimpleName().toString();
        String prefix = camelToUpperSnakeCase(enumName);
        String expectedPrefix = prefix + "_";

        if (sourceEnumValue.startsWith(expectedPrefix)) {
            return sourceEnumValue.substring(expectedPrefix.length());
        }
        return sourceEnumValue;
    }

    /**
     * Checks if the given type is a Protobuf-generated enum.
     * <p>
     * Protobuf enums implement either ProtocolMessageEnum or Internal.EnumLite.
     *
     * @param enumType the enum type to check
     * @return true if this is a protobuf enum
     */
    private boolean isProtobufEnum(TypeElement enumType) {
        return KNOWN_ENUMS.computeIfAbsent(enumType, type -> {
            List<? extends TypeMirror> interfaces = type.getInterfaces();
            for (TypeMirror implementedInterface : interfaces) {
                String interfaceName = implementedInterface.toString();
                if (PROTOBUF_ENUM_INTERFACE.equals(interfaceName)
                        || PROTOBUF_LITE_ENUM_INTERFACE.equals(interfaceName)) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * Converts CamelCase to UPPER_SNAKE_CASE.
     * <p>
     * Example: StatusCode -> STATUS_CODE
     *
     * @param camelCase the camel case string
     * @return the upper snake case string
     */
    String camelToUpperSnakeCase(String camelCase) {
        if (camelCase == null || camelCase.isEmpty()) {
            return camelCase;
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < camelCase.length(); i++) {
            char c = camelCase.charAt(i);
            if (Character.isUpperCase(c) && i > 0) {
                result.append('_');
            }
            result.append(Character.toUpperCase(c));
        }
        return result.toString();
    }
}
