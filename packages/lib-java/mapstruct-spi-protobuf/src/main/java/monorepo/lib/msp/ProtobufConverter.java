package monorepo.lib.msp;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.Duration;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.Int64Value;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.StringValue;
import com.google.protobuf.Timestamp;
import com.google.protobuf.UInt32Value;
import com.google.protobuf.UInt64Value;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import org.mapstruct.TargetType;

/**
 * A utility class for converting between Protobuf types and Java types.
 *
 * @author Freeman
 * @since 2025/11/17
 */
public final class ProtobufConverter {

    // ==================== Protobuf Enum ====================
    public static <E extends ProtocolMessageEnum> Integer protobufEnumToInteger(E enumValue) {
        if (enumValue == null) {
            return null;
        }
        return enumValue.getNumber();
    }

    @SuppressWarnings("unchecked")
    public static <E extends ProtocolMessageEnum> E integerToProtobufEnum(
            Integer number, @TargetType Class<E> enumClass) {
        if (number == null) {
            return null;
        }
        try {
            return (E) enumClass.getMethod("forNumber", int.class).invoke(null, number);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Unable to convert number '%d' to enum %s", number, enumClass.getName()), e);
        }
    }

    // ==================== Timestamp ====================

    public static Instant protobufTimestampToInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static Timestamp instantToProtobufTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static LocalDateTime protobufTimestampToLocalDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = protobufTimestampToInstant(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Timestamp localDateTimeToProtobufTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instantToProtobufTimestamp(instant);
    }

    public static java.util.Date protobufTimestampToDate(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        Instant instant = protobufTimestampToInstant(timestamp);
        return java.util.Date.from(instant);
    }

    public static Timestamp dateToProtobufTimestamp(java.util.Date date) {
        if (date == null) {
            return null;
        }
        Instant instant = date.toInstant();
        return instantToProtobufTimestamp(instant);
    }

    // ==================== Duration ====================

    public static java.time.Duration protoDurationToJavaDuration(Duration duration) {
        if (duration == null) {
            return null;
        }
        return java.time.Duration.ofSeconds(duration.getSeconds(), duration.getNanos());
    }

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

    public static byte[] byteStringToBytes(ByteString byteString) {
        if (byteString == null) {
            return null;
        }
        return byteString.toByteArray();
    }

    public static ByteString bytesToByteString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return ByteString.copyFrom(bytes);
    }

    public static String byteStringToString(ByteString byteString) {
        if (byteString == null) {
            return null;
        }
        return byteString.toStringUtf8();
    }

    public static ByteString stringToByteString(String string) {
        if (string == null) {
            return null;
        }
        return ByteString.copyFromUtf8(string);
    }

    // ==================== Wrapper Types ====================

    public static String stringValueToString(StringValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static StringValue stringToStringValue(String value) {
        if (value == null) {
            return null;
        }
        return StringValue.of(value);
    }

    public static Integer int32ValueToInteger(Int32Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static Int32Value integerToInt32Value(Integer value) {
        if (value == null) {
            return null;
        }
        return Int32Value.of(value);
    }

    public static Long int64ValueToLong(Int64Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static Int64Value longToInt64Value(Long value) {
        if (value == null) {
            return null;
        }
        return Int64Value.of(value);
    }

    public static Integer uint32ValueToInteger(UInt32Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static UInt32Value integerToUint32Value(Integer value) {
        if (value == null) {
            return null;
        }
        return UInt32Value.of(value);
    }

    public static Long uint64ValueToLong(UInt64Value value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static UInt64Value longToUint64Value(Long value) {
        if (value == null) {
            return null;
        }
        return UInt64Value.of(value);
    }

    public static Float floatValueToFloat(FloatValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static FloatValue floatToFloatValue(Float value) {
        if (value == null) {
            return null;
        }
        return FloatValue.of(value);
    }

    public static Double doubleValueToDouble(DoubleValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static DoubleValue doubleToDoubleValue(Double value) {
        if (value == null) {
            return null;
        }
        return DoubleValue.of(value);
    }

    public static Boolean boolValueToBoolean(BoolValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static BoolValue booleanToBoolValue(Boolean value) {
        if (value == null) {
            return null;
        }
        return BoolValue.of(value);
    }

    public static ByteString bytesValueToByteString(BytesValue value) {
        if (value == null) {
            return null;
        }
        return value.getValue();
    }

    public static BytesValue byteStringToBytesValue(ByteString byteString) {
        if (byteString == null) {
            return null;
        }
        return BytesValue.newBuilder().setValue(byteString).build();
    }

    // google/type package

    // ==================== Date ====================
    public static LocalDate googleDateToLocalDate(com.google.type.Date date) {
        if (date == null) {
            return null;
        }
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    public static com.google.type.Date localDateToGoogleDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return com.google.type.Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonthValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }

    // ==================== TimeOfDay ====================
    public static java.time.LocalTime googleTimeOfDayToLocalTime(com.google.type.TimeOfDay timeOfDay) {
        if (timeOfDay == null) {
            return null;
        }
        return java.time.LocalTime.of(
                timeOfDay.getHours(), timeOfDay.getMinutes(), timeOfDay.getSeconds(), timeOfDay.getNanos());
    }

    public static com.google.type.TimeOfDay localTimeToGoogleTimeOfDay(java.time.LocalTime localTime) {
        if (localTime == null) {
            return null;
        }
        return com.google.type.TimeOfDay.newBuilder()
                .setHours(localTime.getHour())
                .setMinutes(localTime.getMinute())
                .setSeconds(localTime.getSecond())
                .setNanos(localTime.getNano())
                .build();
    }

    // ==================== DayOfWeek ====================
    public static java.time.DayOfWeek googleDayOfWeekToDayOfWeek(com.google.type.DayOfWeek dayOfWeek) {
        if (dayOfWeek == null || dayOfWeek == com.google.type.DayOfWeek.DAY_OF_WEEK_UNSPECIFIED) {
            return null;
        }
        return java.time.DayOfWeek.of(dayOfWeek.getNumber());
    }

    public static com.google.type.DayOfWeek dayOfWeekToGoogleDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        if (dayOfWeek == null) {
            return com.google.type.DayOfWeek.DAY_OF_WEEK_UNSPECIFIED;
        }
        return com.google.type.DayOfWeek.forNumber(dayOfWeek.getValue());
    }

    // ==================== Month ====================
    public static java.time.Month googleMonthToMonth(com.google.type.Month month) {
        if (month == null || month == com.google.type.Month.MONTH_UNSPECIFIED) {
            return null;
        }
        return java.time.Month.of(month.getNumber());
    }

    public static com.google.type.Month monthToGoogleMonth(java.time.Month month) {
        if (month == null) {
            return com.google.type.Month.MONTH_UNSPECIFIED;
        }
        return com.google.type.Month.forNumber(month.getValue());
    }
}
