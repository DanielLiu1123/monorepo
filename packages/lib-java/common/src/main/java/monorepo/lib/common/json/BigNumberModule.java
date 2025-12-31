package monorepo.lib.common.json;

import java.math.BigDecimal;
import java.math.BigInteger;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

/**
 * Jackson module for serializing big numbers as strings.
 *
 * @author Freeman
 * @since 2025/11/18
 */
public final class BigNumberModule extends SimpleModule {
    public BigNumberModule() {
        addSerializer(Long.class, ToStringSerializer.instance);
        addSerializer(Long.TYPE, ToStringSerializer.instance);
        addSerializer(BigDecimal.class, ToStringSerializer.instance);
        addSerializer(BigInteger.class, ToStringSerializer.instance);
    }
}
