package monorepo.lib.msp;

import com.google.protobuf.BoolValue;
import com.google.protobuf.ByteString;
import com.google.protobuf.BytesValue;
import com.google.protobuf.DoubleValue;
import com.google.protobuf.FloatValue;
import com.google.protobuf.Int32Value;
import com.google.protobuf.StringValue;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Set;

record EverythingDTO(
        Integer int32,
        long int64,
        Float float_,
        Double double_,
        Boolean bool,
        String string,
        ByteString bytes,
        Integer int32Value,
        long int64Value,
        FloatValue floatValue,
        double doubleValue,
        boolean boolValue,
        StringValue stringValue,
        ByteString bytesValue,
        Integer[] repeatedInt32,
        Set<Long> repeatedInt64,
        List<Float> repeatedFloat,
        List<DoubleValue> repeatedDouble,
        List<BoolValue> repeatedBool,
        List<String> repeatedString,
        List<BytesValue> repeatedBytes,
        Map<Integer, ByteString> mapInt32String,
        Map<Long, String> mapInt64String,
        Map<BoolValue, String> mapBoolString,
        Map<String, String> mapStringString,
        Map<String, BytesValue> mapStringBytes,
        Message message,
        List<Message> repeatedMessage,
        Map<StringValue, Message> mapStringMessage,
        Integer enum_,
        int optionalEnum,
        List<String> repeatedEnum,
        Map<String, Integer> mapStringEnum,
        Instant timestamp,
        Duration duration,
        LocalTime timeOfDay,
        String date,
        DayOfWeek dayOfWeek,
        Month month,
        Int32Value oneofInt32,
        StringValue oneofString,
        Integer oneofEnum,
        Message oneofMessage,
        @Deprecated Integer deprecatedInt32,
        @Deprecated String deprecatedString,
        @Deprecated List<String> deprecatedRepeatedString,
        @Deprecated Map<String, Integer> deprecatedMapStringInt32,
        @Deprecated Integer deprecatedEnum,
        String strBytes,
        Integer enValue,
        List<String> reStringList,
        List<Integer> reEnumValueList,
        Map<String, String> maStringStringMap,
        Map<String, String> maStringEnumMap,
        Message msgBuilder,
        Message msgOrBuilder) {
    record Message(long id, String name) {}
}
