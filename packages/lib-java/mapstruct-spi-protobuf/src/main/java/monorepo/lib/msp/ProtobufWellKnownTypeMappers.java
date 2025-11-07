package monorepo.lib.msp;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * MapStruct type converters for Protobuf Well-Known Types.
 */
public final class ProtobufWellKnownTypeMappers {

    // ==================== Timestamp ====================

    /**
     * Converts google.protobuf.Timestamp to java.time.Instant.
     *
     * @param timestamp the protobuf timestamp, may be null
     * @return the equivalent Instant, or null if input is null
     */
    public static Instant timestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    /**
     * Converts java.time.Instant to google.protobuf.Timestamp.
     *
     * @param instant the Java instant, may be null
     * @return the equivalent protobuf Timestamp, or null if input is null
     */
    public static Timestamp instantToTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Converts google.protobuf.Timestamp to java.time.LocalDate.
     *
     * @param timestamp the protobuf timestamp, may be null
     * @return the equivalent LocalDateTime, or null if input is null
     */
    public static LocalDateTime timestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    /**
     * Converts java.time.LocalDateTime to google.protobuf.Timestamp.
     *
     * @param localDateTime the Java LocalDateTime, may be null
     * @return the equivalent protobuf Timestamp, or null if input is null
     */
    public static Timestamp localDateTimeToTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    /**
     * Converts google.protobuf.Timestamp to java.util.Date.
     *
     * @param timestamp the protobuf timestamp, may be null
     * @return the equivalence Date, or null if input is null
     */
    public static java.util.Date timestampToDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
        return java.util.Date.from(instant);
    }

    /**
     * Converts java.util.Date to google.protobuf.Timestamp.
     *
     * @param date the Java Date, may be null
     * @return the equivalent protobuf Timestamp, or null if input is null
     */
    public static Timestamp dateToTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    // ==================== Duration ====================

    /**
     * Converts google.protobuf.Duration to java.time.Duration.
     *
     * @param duration the protobuf duration, may be null
     * @return the equivalent Java Duration, or null if input is null
     */
    public static java.time.Duration protoDurationToJavaDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        return java.time.Duration.ofSeconds(duration.getSeconds(), duration.getNanos());
    }

    /**
     * Converts java.time.Duration to google.protobuf.Duration.
     *
     * @param duration the Java duration, may be null
     * @return the equivalent protobuf Duration, or null if input is null
     */
    public static Duration javaDurationToProtoDuration(java.time.Duration duration) {
        if (duration == null) {
            return null;
        }
        return Duration.newBuilder()
                .setSeconds(duration.getSeconds())
                .setNanos(duration.getNano())
                .build();
    }

    // ==================== ByteString ====================

    /**
     * Converts google.protobuf.ByteString to byte array.
     *
     * @param byteString the protobuf ByteString, may be null
     * @return the equivalent byte array, or null if input is null
     */
    public static byte[] byteStringToBytes(ByteString byteString) {
        if (byteString == null) {
            return null;
        }
        return byteString.toByteArray();
    }

    /**
     * Converts byte array to google.protobuf.ByteString.
     *
     * @param bytes the byte array, may be null
     * @return the equivalent protobuf ByteString, or null if input is null
     */
    public static ByteString bytesToByteString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return ByteString.copyFrom(bytes);
    }

    // ==================== Wrapper Types ====================

    /**
     * Converts google.protobuf.StringValue to String.
     *
     * @param value the protobuf StringValue, may be null
     * @return the unwrapped String, or null if input is null
     */
    public static String stringValueToString(StringValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts String to google.protobuf.StringValue.
     *
     * @param value the String, may be null
     * @return the wrapped StringValue, or null if input is null
     */
    public static StringValue stringToStringValue(String value) {
        if (value == null) {
            return null;
        }
        return StringValue.of(value);
    }

    /**
     * Converts google.protobuf.Int32Value to Integer.
     *
     * @param value the protobuf Int32Value, may be null
     * @return the unwrapped Integer, or null if input is null
     */
    public static Integer int32ValueToInteger(Int32Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Integer to google.protobuf.Int32Value.
     *
     * @param value the Integer, may be null
     * @return the wrapped Int32Value, or null if input is null
     */
    public static Int32Value integerToInt32Value(Integer value) {
        if (value == null) {
            return null;
        }
        return Int32Value.of(value);
    }

    /**
     * Converts google.protobuf.Int64Value to Long.
     *
     * @param value the protobuf Int64Value, may be null
     * @return the unwrapped Long, or null if input is null
     */
    public static Long int64ValueToLong(Int64Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Long to google.protobuf.Int64Value.
     *
     * @param value the Long, may be null
     * @return the wrapped Int64Value, or null if input is null
     */
    public static Int64Value longToInt64Value(Long value) {
        if (value == null) {
            return null;
        }
        return Int64Value.of(value);
    }

    /**
     * Converts google.protobuf.UInt32Value to Integer.
     *
     * @param value the protobuf UInt32Value, may be null
     * @return the unwrapped Integer, or null if input is null
     */
    public static Integer uint32ValueToInteger(UInt32Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Integer to google.protobuf.UInt32Value.
     *
     * @param value the Integer, may be null
     * @return the wrapped UInt32Value, or null if input is null
     */
    public static UInt32Value integerToUint32Value(Integer value) {
        if (value == null) {
            return null;
        }
        return UInt32Value.of(value);
    }

    /**
     * Converts google.protobuf.UInt64Value to Long.
     *
     * @param value the protobuf UInt64Value, may be null
     * @return the unwrapped Long, or null if input is null
     */
    public static Long uint64ValueToLong(UInt64Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Long to google.protobuf.UInt64Value.
     *
     * @param value the Long, may be null
     * @return the wrapped UInt64Value, or null if input is null
     */
    public static UInt64Value longToUint64Value(Long value) {
        if (value == null) {
            return null;
        }
        return UInt64Value.of(value);
    }

    /**
     * Converts google.protobuf.FloatValue to Float.
     *
     * @param value the protobuf FloatValue, may be null
     * @return the unwrapped Float, or null if input is null
     */
    public static Float floatValueToFloat(FloatValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Float to google.protobuf.FloatValue.
     *
     * @param value the Float, may be null
     * @return the wrapped FloatValue, or null if input is null
     */
    public static FloatValue floatToFloatValue(Float value) {
        if (value == null) {
            return null;
        }
        return FloatValue.of(value);
    }

    /**
     * Converts google.protobuf.DoubleValue to Double.
     *
     * @param value the protobuf DoubleValue, may be null
     * @return the unwrapped Double, or null if input is null
     */
    public static Double doubleValueToDouble(DoubleValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Double to google.protobuf.DoubleValue.
     *
     * @param value the Double, may be null
     * @return the wrapped DoubleValue, or null if input is null
     */
    public static DoubleValue doubleToDoubleValue(Double value) {
        if (value == null) {
            return null;
        }
        return DoubleValue.of(value);
    }

    /**
     * Converts google.protobuf.BoolValue to Boolean.
     *
     * @param value the protobuf BoolValue, may be null
     * @return the unwrapped Boolean, or null if input is null
     */
    public static Boolean boolValueToBoolean(BoolValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    /**
     * Converts Boolean to google.protobuf.BoolValue.
     *
     * @param value the Boolean, may be null
     * @return the wrapped BoolValue, or null if input is null
     */
    public static BoolValue booleanToBoolValue(Boolean value) {
        if (value == null) {
            return null;
        }
        return BoolValue.of(value);
    }

    /**
     * Converts google.protobuf.BytesValue to byte array.
     *
     * @param value the protobuf BytesValue, may be null
     * @return the unwrapped byte array, or null if input is null
     */
    public static byte[] bytesValueToBytes(BytesValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue().toByteArray();
    }

    /**
     * Converts byte array to google.protobuf.BytesValue.
     *
     * @param value the byte array, may be null
     * @return the wrapped BytesValue, or null if input is null
     */
    public static BytesValue bytesToBytesValue(byte[] value) {
        if (value == null) {
            return null;
        }
        return BytesValue.of(ByteString.copyFrom(value));
    }
}
