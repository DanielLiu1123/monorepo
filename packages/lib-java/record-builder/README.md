# Record Builder

Not null (but respecting jspecify @Nullable), Protobuf inspired Java record builder annotation processor.

## Overview

```java
@RecordBuilder
public record User(String name, int age, @Nullable LocalDate birthday, List<String> hobbies, Map<String, String> attributes) {}

// Generated builder usage
User user = UserBuilder.of()
        .setName("John Doe")
        .setAge(30)
        .setBirthday(LocalDate.of(1993, 1, 1))
        .addHobbies("Reading") // generate addXxx and addAllXxx for List fields
        .putAttributes("key", "value") // generate putXxx and putAllXxx for Map fields
    .build();

User anotherUser = UserBuilder.from(user)
        .setName("Jane Doe")
        .clearHobbies() // generate clearXxx for all fields
    .build();
```