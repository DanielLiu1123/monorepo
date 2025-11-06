# MapStruct SPI for Protocol Buffers

MapStruct SPI 实现，用于简化 Protocol Buffers 生成的 Java 类与普通 Java 对象之间的映射。

## 功能特性

### 1. ProtobufAccessorNamingStrategy

自动识别和处理 Protobuf 生成的特殊访问器方法：

- ✅ 识别 `getXxxList()` 方法（重复字段）
- ✅ 识别 `getXxxMap()` 方法（映射字段）
- ✅ 识别 `addAll()` 和 `putAll()` 方法
- ✅ 过滤 Protobuf 内部方法（如 `getDefaultInstanceForType()`）
- ✅ 处理特殊后缀方法（`getValue()`, `getCount()`, `getBytes()` 等）

### 2. ProtobufEnumMappingStrategy

按照 Google Protobuf 风格指南自动映射枚举类型：

- ✅ 自动移除枚举常量前缀（`STATUS_ACTIVE` → `ACTIVE`）
- ✅ 将 `XXX_UNSPECIFIED` 零值映射为 `null`
- ✅ 将 `UNRECOGNIZED` 映射为 `null`
- ✅ 支持通过编译选项自定义零值后缀

**示例：**

```protobuf
// Proto 定义
enum Status {
  STATUS_UNSPECIFIED = 0;
  STATUS_ACTIVE = 1;
  STATUS_INACTIVE = 2;
}
```

```java
// Java 枚举
enum Status {
  ACTIVE,
  INACTIVE
}

// MapStruct 自动映射：STATUS_ACTIVE → ACTIVE
```

### 3. ProtobufWellKnownTypeMappers

提供 Protobuf Well-Known 类型与 Java 类型之间的双向转换：

| Protobuf 类型 | Java 类型 |
|--------------|-----------|
| `google.protobuf.Timestamp` | `java.time.Instant` |
| `google.protobuf.Duration` | `java.time.Duration` |
| `google.protobuf.ByteString` | `byte[]` |
| `google.protobuf.StringValue` | `String` |
| `google.protobuf.Int32Value` | `Integer` |
| `google.protobuf.Int64Value` | `Long` |
| `google.protobuf.UInt32Value` | `Integer` |
| `google.protobuf.UInt64Value` | `Long` |
| `google.protobuf.FloatValue` | `Float` |
| `google.protobuf.DoubleValue` | `Double` |
| `google.protobuf.BoolValue` | `Boolean` |
| `google.protobuf.BytesValue` | `byte[]` |

## 使用方法

### 1. 添加依赖

```gradle
dependencies {
    implementation 'monorepo:mapstruct-spi-protobuf:0.1.0'

    // MapStruct 核心依赖
    implementation 'org.mapstruct:mapstruct:1.6.3'
    annotationProcessor 'org.mapstruct:mapstruct-processor:1.6.3'
}
```

### 2. 定义 Mapper

```java
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import monorepo.lib.msp.ProtobufWellKnownTypeMappers;

@Mapper(uses = ProtobufWellKnownTypeMappers.class)
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    // Protobuf → DTO
    OrderDTO modelToDTO(OrderModel orderModel);

    // DTO → Protobuf
    OrderModel dtoToModel(OrderDTO orderDTO);
}
```

### 3. 自动映射示例

**Protobuf 定义：**

```protobuf
syntax = "proto3";

import "google/protobuf/timestamp.proto";

message OrderModel {
  int64 id = 1;
  repeated int64 item_ids = 2;
  map<string, string> attributes = 3;
  google.protobuf.Timestamp created_at = 4;
  Status status = 5;
}

enum Status {
  STATUS_UNSPECIFIED = 0;
  STATUS_ACTIVE = 1;
  STATUS_INACTIVE = 2;
}
```

**Java DTO：**

```java
public class OrderDTO {
    private long id;
    private List<Long> itemIds;          // 自动映射 item_ids
    private Map<String, String> attributes;
    private Instant createdAt;           // 自动转换 Timestamp → Instant
    private Status status;               // 自动映射枚举（移除前缀）

    // getters and setters
}

public enum Status {
    ACTIVE,
    INACTIVE
}
```

**生成的映射代码：**

```java
public class OrderMapperImpl implements OrderMapper {
    private final ProtobufWellKnownTypeMappers protobufWellKnownTypeMappers =
        new ProtobufWellKnownTypeMappers();

    @Override
    public OrderDTO modelToDTO(OrderModel orderModel) {
        OrderDTO dto = new OrderDTO();
        dto.setId(orderModel.getId());
        dto.setItemIds(orderModel.getItemIdsList());  // 识别 List 方法
        dto.setAttributes(orderModel.getAttributesMap());  // 识别 Map 方法
        dto.setCreatedAt(protobufWellKnownTypeMappers
            .timestampToInstant(orderModel.getCreatedAt()));  // 自动转换
        dto.setStatus(mapStatus(orderModel.getStatus()));  // 移除前缀
        return dto;
    }
}
```

## 配置选项

### 自定义枚举零值后缀

通过 MapStruct 编译选项配置：

```gradle
tasks.withType(JavaCompile) {
    options.compilerArgs += [
        '-Amapstruct.protobuf.enumPostfixOverrides=com.example.MyEnum=UNKNOWN,com.example.OtherEnum=NONE'
    ]
}
```

或在 Maven 中：

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <configuration>
        <annotationProcessorPaths>
            <path>
                <groupId>org.mapstruct</groupId>
                <artifactId>mapstruct-processor</artifactId>
            </path>
        </annotationProcessorPaths>
        <compilerArgs>
            <arg>-Amapstruct.protobuf.enumPostfixOverrides=com.example.MyEnum=UNKNOWN</arg>
        </compilerArgs>
    </configuration>
</plugin>
```

## SPI 实现详情

本项目实现了以下 MapStruct SPI 接口：

1. **`org.mapstruct.ap.spi.AccessorNamingStrategy`**
   - 实现类：`ProtobufAccessorNamingStrategy`
   - 配置文件：`META-INF/services/org.mapstruct.ap.spi.AccessorNamingStrategy`

2. **`org.mapstruct.ap.spi.EnumMappingStrategy`**
   - 实现类：`ProtobufEnumMappingStrategy`
   - 配置文件：`META-INF/services/org.mapstruct.ap.spi.EnumMappingStrategy`

## 参考资料

- [MapStruct 官方文档](https://mapstruct.org/documentation/stable/reference/html/)
- [MapStruct SPI 扩展](https://mapstruct.org/documentation/stable/reference/html/#_service_provider_interface)
- [Protocol Buffers Style Guide](https://protobuf.dev/programming-guides/style/)
- [Entur MapStruct SPI Protobuf](https://github.com/entur/mapstruct-spi-protobuf)

## 许可证

基于 Entur 项目的实现，遵循 EUPL-1.1 许可证。

## 版本历史

### 0.1.0 (2025-01-06)

- ✅ 实现 `ProtobufAccessorNamingStrategy`
- ✅ 实现 `ProtobufEnumMappingStrategy`
- ✅ 实现 `ProtobufWellKnownTypeMappers`
- ✅ 完整的测试覆盖
- ✅ 支持 Protobuf Java 和 Java Lite
