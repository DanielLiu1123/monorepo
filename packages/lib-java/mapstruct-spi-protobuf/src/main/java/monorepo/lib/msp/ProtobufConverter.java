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
        return enumValue.getNumber();
    }

    @SuppressWarnings("unchecked")
    public static <E extends ProtocolMessageEnum> E integerToProtobufEnum(
            Integer number, @TargetType Class<E> enumClass) {
        try {
            return (E) enumClass.getMethod("forNumber", int.class).invoke(null, number);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    String.format("Unable to convert number '%d' to enum %s", number, enumClass.getName()), e);
        }
    }

    // ==================== Timestamp ====================

    public static Instant protobufTimestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static Timestamp instantToProtobufTimestamp(Instant instant) {
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static LocalDateTime protobufTimestampToLocalDateTime(Timestamp timestamp) {
        Instant instant = protobufTimestampToInstant(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static Timestamp localDateTimeToProtobufTimestamp(LocalDateTime localDateTime) {
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instantToProtobufTimestamp(instant);
    }

    public static java.util.Date protobufTimestampToDate(Timestamp timestamp) {
        Instant instant = protobufTimestampToInstant(timestamp);
        return java.util.Date.from(instant);
    }

    public static Timestamp dateToProtobufTimestamp(java.util.Date date) {
        Instant instant = date.toInstant();
        return instantToProtobufTimestamp(instant);
    }

    // ==================== Duration ====================

    public static java.time.Duration protoDurationToJavaDuration(Duration duration) {
        return java.time.Duration.ofSeconds(duration.getSeconds(), duration.getNanos());
    }

    public static Duration javaDurationToProtoDuration(java.time.Duration duration) {
        return Duration.newBuilder()
                .setSeconds(duration.getSeconds())
                .setNanos(duration.getNano())
                .build();
    }

    // ==================== ByteString ====================

    public static byte[] byteStringToBytes(ByteString byteString) {
        return byteString.toByteArray();
    }

    public static ByteString bytesToByteString(byte[] bytes) {
        return ByteString.copyFrom(bytes);
    }

    public static String byteStringToString(ByteString byteString) {
        return byteString.toStringUtf8();
    }

    public static ByteString stringToByteString(String string) {
        return ByteString.copyFromUtf8(string);
    }

    // ==================== Wrapper Types ====================

    public static String stringValueToString(StringValue value) {
        return value.getValue();
    }

    public static StringValue stringToStringValue(String value) {
        return StringValue.of(value);
    }

    public static Integer int32ValueToInteger(Int32Value value) {
        return value.getValue();
    }

    public static Int32Value integerToInt32Value(Integer value) {
        return Int32Value.of(value);
    }

    public static Long int64ValueToLong(Int64Value value) {
        return value.getValue();
    }

    public static Int64Value longToInt64Value(Long value) {
        return Int64Value.of(value);
    }

    public static Integer uint32ValueToInteger(UInt32Value value) {
        return value.getValue();
    }

    public static UInt32Value integerToUint32Value(Integer value) {
        return UInt32Value.of(value);
    }

    public static Long uint64ValueToLong(UInt64Value value) {
        return value.getValue();
    }

    public static UInt64Value longToUint64Value(Long value) {
        return UInt64Value.of(value);
    }

    public static Float floatValueToFloat(FloatValue value) {
        return value.getValue();
    }

    public static FloatValue floatToFloatValue(Float value) {
        return FloatValue.of(value);
    }

    public static Double doubleValueToDouble(DoubleValue value) {
        return value.getValue();
    }

    public static DoubleValue doubleToDoubleValue(Double value) {
        return DoubleValue.of(value);
    }

    public static Boolean boolValueToBoolean(BoolValue value) {
        return value.getValue();
    }

    public static BoolValue booleanToBoolValue(Boolean value) {
        return BoolValue.of(value);
    }

    public static ByteString bytesValueToByteString(BytesValue value) {
        return value.getValue();
    }

    public static BytesValue byteStringToBytesValue(ByteString byteString) {
        return BytesValue.newBuilder().setValue(byteString).build();
    }

    // google/type package

    // ==================== Date ====================
    public static LocalDate googleDateToLocalDate(com.google.type.Date date) {
        return LocalDate.of(date.getYear(), date.getMonth(), date.getDay());
    }

    public static com.google.type.Date localDateToGoogleDate(LocalDate localDate) {
        return com.google.type.Date.newBuilder()
                .setYear(localDate.getYear())
                .setMonth(localDate.getMonthValue())
                .setDay(localDate.getDayOfMonth())
                .build();
    }

    // ==================== TimeOfDay ====================
    public static java.time.LocalTime googleTimeOfDayToLocalTime(com.google.type.TimeOfDay timeOfDay) {
        return java.time.LocalTime.of(
                timeOfDay.getHours(), timeOfDay.getMinutes(), timeOfDay.getSeconds(), timeOfDay.getNanos());
    }

    public static com.google.type.TimeOfDay localTimeToGoogleTimeOfDay(java.time.LocalTime localTime) {
        return com.google.type.TimeOfDay.newBuilder()
                .setHours(localTime.getHour())
                .setMinutes(localTime.getMinute())
                .setSeconds(localTime.getSecond())
                .setNanos(localTime.getNano())
                .build();
    }

    // ==================== DayOfWeek ====================
    public static java.time.DayOfWeek googleDayOfWeekToDayOfWeek(com.google.type.DayOfWeek dayOfWeek) {
        return java.time.DayOfWeek.of(dayOfWeek.getNumber());
    }

    public static com.google.type.DayOfWeek dayOfWeekToGoogleDayOfWeek(java.time.DayOfWeek dayOfWeek) {
        return com.google.type.DayOfWeek.forNumber(dayOfWeek.getValue());
    }

    // ==================== Month ====================
    public static java.time.Month googleMonthToMonth(com.google.type.Month month) {
        return java.time.Month.of(month.getNumber());
    }

    public static com.google.type.Month monthToGoogleMonth(java.time.Month month) {
        return com.google.type.Month.forNumber(month.getValue());
    }
}
