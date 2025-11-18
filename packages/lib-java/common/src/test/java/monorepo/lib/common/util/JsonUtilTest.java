package monorepo.lib.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

    @Test
    void fromJson() {
        var table = new Object[][] {
            {
                """
                        {
                          "string": "example",
                          "int": 42,
                          "long": "1234567890123456789",
                          "bigDecimal": "1234567890.123456789",
                          "bigInteger": "123456789012345678901234567890",
                          "localDate": "2025-12-25",
                          "localDateTime": "2025-12-25T10:15:30",
                          "instant": "2025-12-25T15:30:45Z",
                          "mapStringLong": { "key1": "100" },
                          "listMonth": [ "JANUARY", "FEBRUARY", "MARCH" ],
                          "month": "DECEMBER",
                          "dayOfWeek": "THURSDAY",
                          "object": {},
                          "null": null
                        }
                        """,
                new Anything(
                        "example",
                        42,
                        1234567890123456789L,
                        new BigDecimal("1234567890.123456789"),
                        new BigInteger("123456789012345678901234567890"),
                        LocalDate.of(2025, 12, 25),
                        LocalDateTime.of(2025, 12, 25, 10, 15, 30),
                        Instant.parse("2025-12-25T15:30:45Z"),
                        Map.of("key1", 100L),
                        List.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH),
                        Month.DECEMBER,
                        DayOfWeek.THURSDAY,
                        Map.of(),
                        null,
                        Optional.empty())
            }
        };

        for (var row : table) {
            var input = (String) row[0];
            var expected = (Anything) row[1];
            var actual = JsonUtil.toObject(input, Anything.class);
            assertThat(actual).isEqualTo(expected);
        }
    }

    @Test
    void toJson() {
        var table = new Object[][] {
            {
                new Anything(
                        "example",
                        42,
                        1234567890123456789L,
                        new BigDecimal("1234567890.123456789"),
                        new BigInteger("123456789012345678901234567890"),
                        LocalDate.of(2025, 12, 25),
                        LocalDateTime.of(2025, 12, 25, 10, 15, 30),
                        Instant.parse("2025-12-25T15:30:45Z"),
                        Map.of("key1", 100L),
                        List.of(Month.JANUARY, Month.FEBRUARY, Month.MARCH),
                        Month.DECEMBER,
                        DayOfWeek.THURSDAY,
                        new Object(),
                        null,
                        Optional.empty()),
                """
                        {"string":"example","int":42,"long":"1234567890123456789","bigDecimal":"1234567890.123456789","bigInteger":"123456789012345678901234567890","localDate":"2025-12-25","localDateTime":"2025-12-25T10:15:30","instant":"2025-12-25T15:30:45Z","mapStringLong":{"key1":"100"},"listMonth":[1,2,3],"month":12,"dayOfWeek":"THURSDAY","object":{},"optionalString":null}"""
            }
        };

        for (var row : table) {
            var input = (Anything) row[0];
            var expected = (String) row[1];
            var actual = JsonUtil.toJson(input);
            assertThat(actual).isEqualTo(expected);
        }
    }

    record Anything(
            String string,
            @JsonProperty("int") int int_,
            @JsonProperty("long") Long long_,
            BigDecimal bigDecimal,
            BigInteger bigInteger,
            LocalDate localDate,
            LocalDateTime localDateTime,
            Instant instant,
            Map<String, Long> mapStringLong,
            List<Month> listMonth,
            Month month,
            DayOfWeek dayOfWeek,
            Object object,
            @JsonProperty("null") Object null_,
            Optional<String> optionalString) {}
}
