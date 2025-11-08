package monorepo.services.product.entity;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.StringValue;
import lombok.Data;
import monorepo.proto.order.v1.EverythingModel;

/**
 *
 * int32 int32 = 1;
 * int64 int64 = 3;
 * float float = 11;
 * double double = 12;
 * bool bool = 13;
 * string string = 14;
 * bytes bytes = 15;
 * repeated int32 repeated_int32 = 16;
 * repeated int64 repeated_int64 = 18;
 * repeated float repeated_float = 26;
 * repeated double repeated_double = 27;
 * repeated bool repeated_bool = 28;
 * repeated string repeated_string = 29;
 * repeated bytes repeated_bytes = 30;
 * <p>
 * map<int32, string> map_int32_string = 32;
 * map<int64, string> map_int64_string = 34;
 * map<bool, string> map_bool_string = 44;
 * map<string, string> map_string_string = 45;
 * <p>
 * map<string, bytes> map_string_bytes = 46;
 * <p>
 * Message message = 50;
 * repeated Message repeated_message = 51;
 * map<string, Message> map_string_message = 52;
 * <p>
 * Enum enum = 60;
 * optional Enum optional_enum = 62;
 * repeated Enum repeated_enum = 61;
 * map<string, Enum> map_string_enum = 63;
 *
 * @author Freeman
 * @since 2025/11/6
 */
@Data
public class Everything {
    // Primitive types
    private Integer int32;
    private long int64;
    private Float float_;
    private Double double_;
    private Boolean bool;
    private String string;
    private ByteString bytes;

    // Wrapper types
    private Integer int32Value;
    private long int64Value;
    private FloatValue floatValue;
    private double doubleValue;
    private boolean boolValue;
    private StringValue stringValue;
    private ByteString bytesValue;

    // repeated
    private Integer[] repeatedInt32;
    private Set<Long> repeatedInt64;
    private List<Double> repeatedFloat;
    private List<DoubleValue> repeatedDouble;
    private List<BoolValue> repeatedBool;
    private List<String> repeatedString;
    private List<BytesValue> repeatedBytes;

    // map
    private Map<Integer, ByteString> mapInt32String;
    private Map<Long, String> mapInt64String;
    private Map<BoolValue, String> mapBoolString;
    private Map<String, String> mapStringString;
    private Map<String, BytesValue> mapStringBytes;

    // message
    private EverythingModel.Message message;
    private List<Message> repeatedMessage;
    private Map<StringValue, Message> mapStringMessage;

    // enum
    private EverythingModel.Enum enum_;
    private int optionalEnum;
    private List<String> repeatedEnum;
    private Map<String, Integer> mapStringEnum;

    // wellknown
    private Instant timestamp;
    private Duration duration;

    // google/type package
    private LocalTime timeOfDay;
    private LocalDate date;
    private DayOfWeek dayOfWeek;
    private Month month;

    @Data
    public static class Message {
        private long id;
        private String name;
    }
}
