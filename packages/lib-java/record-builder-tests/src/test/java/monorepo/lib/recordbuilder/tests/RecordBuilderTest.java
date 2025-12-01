package monorepo.lib.recordbuilder.tests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/**
 * Tests for RecordBuilder annotation processor.
 *
 * @author Freeman
 * @since 2025/11/30
 */
class RecordBuilderTest {

    @Test
    void testBasicBuilder() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .setBirthday(LocalDate.of(1993, 1, 1))
                .addAllHobbies(List.of("Reading"))
                .putAllAttributes(Map.of("key", "value"))
                .build();

        assertThat(user.name()).isEqualTo("John Doe");
        assertThat(user.age()).isEqualTo(30);
        assertThat(user.birthday()).isEqualTo(LocalDate.of(1993, 1, 1));
        assertThat(user.hobbies()).containsExactly("Reading");
        assertThat(user.attributes()).containsEntry("key", "value");
    }

    @Test
    void testAddMethods() {
        User user = UserBuilder.of()
                .setName("Jane Doe")
                .setAge(25)
                .addHobbies("Reading")
                .addHobbies("Gaming")
                .build();

        assertThat(user.hobbies()).containsExactly("Reading", "Gaming");
    }

    @Test
    void testAddAllMethods() {
        User user = UserBuilder.of()
                .setName("Jane Doe")
                .setAge(25)
                .addAllHobbies(List.of("Reading", "Gaming", "Cooking"))
                .build();

        assertThat(user.hobbies()).containsExactly("Reading", "Gaming", "Cooking");
    }

    @Test
    void testPutMethods() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .putAttributes("key1", "value1")
                .putAttributes("key2", "value2")
                .build();

        assertThat(user.attributes()).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }

    @Test
    void testPutAllMethods() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .putAllAttributes(Map.of("key1", "value1", "key2", "value2"))
                .build();

        assertThat(user.attributes()).containsEntry("key1", "value1").containsEntry("key2", "value2");
    }

    @Test
    void testFromMethod() {
        User originalUser = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .setBirthday(LocalDate.of(1993, 1, 1))
                .addHobbies("Reading")
                .putAttributes("key", "value")
                .build();

        User modifiedUser = UserBuilder.from(originalUser).setName("Jane Doe").build();

        assertThat(modifiedUser.name()).isEqualTo("Jane Doe");
        assertThat(modifiedUser.age()).isEqualTo(30);
        assertThat(modifiedUser.birthday()).isEqualTo(LocalDate.of(1993, 1, 1));
        assertThat(modifiedUser.hobbies()).containsExactly("Reading");
        assertThat(modifiedUser.attributes()).containsEntry("key", "value");
    }

    @Test
    void testClearMethod() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .addHobbies("Reading")
                .clearHobbies()
                .build();

        assertThat(user.hobbies()).isEmpty();
    }

    @Test
    void testNullableField() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                // birthday is nullable, so we don't set it
                .build();

        assertThat(user.name()).isEqualTo("John Doe");
        assertThat(user.age()).isEqualTo(30);
        assertThat(user.birthday()).isNull();
    }

    @Test
    void testNonNullValidation() {
        assertThatThrownBy(() -> UserBuilder.of()
                        .setAge(30) // missing required field: name
                        .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name cannot be null");
    }

    @Test
    void testImmutableCollections() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .addHobbies("Reading")
                .build();

        // Verify that the returned collections are immutable
        assertThatThrownBy(() -> user.hobbies().add("Gaming")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testModifyingBuilderDoesNotAffectBuiltRecord() {
        UserBuilder builder = UserBuilder.of().setName("John Doe").setAge(30).addHobbies("Reading");

        User user1 = builder.build();

        // Modify builder after building
        builder.addHobbies("Gaming");
        User user2 = builder.build();

        // user1 should only have "Reading"
        assertThat(user1.hobbies()).containsExactly("Reading");
        // user2 should have both hobbies
        assertThat(user2.hobbies()).containsExactly("Reading", "Gaming");
    }

    @Test
    void testClearPrimitiveField() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .clearAge() // Clear primitive field should set it to 0
                .build();

        assertThat(user.age()).isEqualTo(0);
    }

    @Test
    void testGetterMethods() {
        UserBuilder builder = UserBuilder.of().setName("John Doe").setAge(30).setBirthday(LocalDate.of(1993, 1, 1));

        // Test getters return current builder values
        assertThat(builder.getName()).isEqualTo("John Doe");
        assertThat(builder.getAge()).isEqualTo(30);
        assertThat(builder.getBirthday()).isEqualTo(LocalDate.of(1993, 1, 1));
        assertThat(builder.getHobbies()).isNull();
        assertThat(builder.getAttributes()).isNull();

        // Test getters after adding items
        builder.addHobbies("Reading").putAttributes("key", "value");
        assertThat(builder.getHobbies()).containsExactly("Reading");
        assertThat(builder.getAttributes()).containsEntry("key", "value");
    }

    @Test
    void testNullableCollectionElements() {
        // Test List with @Nullable elements
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .addTags("important")
                .addTags(null) // Should accept null due to @Nullable annotation
                .addTags("verified")
                .putScores("math", 95)
                .putScores("english", null) // Should accept null due to @Nullable annotation
                .putScores("science", 88)
                .build();

        assertThat(user.tags()).containsExactly("important", null, "verified");
        assertThat(user.scores()).containsEntry("math", 95);
        assertThat(user.scores()).containsEntry("english", null);
        assertThat(user.scores()).containsEntry("science", 88);
    }

    @Test
    void testSetterNullValidation() {
        // Test that setters reject null for non-nullable fields
        assertThatThrownBy(() -> UserBuilder.of().setName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("name cannot be null");

        // Test that nullable field setter accepts null
        UserBuilder builder = UserBuilder.of().setName("John Doe").setAge(30).setBirthday(null); // Should accept null

        User user = builder.build();
        assertThat(user.birthday()).isNull();
    }

    @Test
    void testSetSupport() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .addRoles("admin")
                .addRoles("user")
                .addRoles("admin") // duplicate should be ignored in Set
                .build();

        // Set should only contain unique elements
        assertThat(user.roles()).containsExactlyInAnyOrder("admin", "user");
        assertThat(user.roles()).hasSize(2);
    }

    @Test
    void testSetImmutability() {
        User user = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .addRoles("admin")
                .build();

        // Verify that the returned Set is immutable
        assertThatThrownBy(() -> user.roles().add("user")).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void testHasMethodsBasic() {
        // Test that hasXxx() returns false initially
        UserBuilder builder = UserBuilder.of();
        assertThat(builder.hasName()).isFalse();
        assertThat(builder.hasAge()).isFalse();
        assertThat(builder.hasBirthday()).isFalse();
        assertThat(builder.hasHobbies()).isFalse();

        // Test that hasXxx() returns true after setting
        builder.setName("John Doe");
        assertThat(builder.hasName()).isTrue();

        builder.setAge(30);
        assertThat(builder.hasAge()).isTrue();

        builder.setBirthday(LocalDate.of(1993, 1, 1));
        assertThat(builder.hasBirthday()).isTrue();

        builder.addHobbies("Reading");
        assertThat(builder.hasHobbies()).isTrue();
    }

    @Test
    void testHasMethodsWithNull() {
        // Test that hasXxx() returns true even when set to null (protobuf semantics)
        UserBuilder builder = UserBuilder.of();
        assertThat(builder.hasBirthday()).isFalse();

        builder.setBirthday(null);
        assertThat(builder.hasBirthday()).isTrue(); // Should be true even when set to null
    }

    @Test
    void testHasMethodsWithClear() {
        // Test that hasXxx() returns false after clear
        UserBuilder builder = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .setBirthday(LocalDate.of(1993, 1, 1))
                .addHobbies("Reading");

        assertThat(builder.hasName()).isTrue();
        assertThat(builder.hasAge()).isTrue();
        assertThat(builder.hasBirthday()).isTrue();
        assertThat(builder.hasHobbies()).isTrue();

        // Clear fields
        builder.clearName();
        builder.clearAge();
        builder.clearBirthday();
        builder.clearHobbies();

        // Verify hasXxx() returns false after clear
        assertThat(builder.hasName()).isFalse();
        assertThat(builder.hasAge()).isFalse();
        assertThat(builder.hasBirthday()).isFalse();
        assertThat(builder.hasHobbies()).isFalse();
    }

    @Test
    void testHasMethodsWithFrom() {
        // Test that hasXxx() is set correctly when using from()
        User originalUser = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .setBirthday(LocalDate.of(1993, 1, 1))
                .addHobbies("Reading")
                .putAttributes("key", "value")
                .build();

        UserBuilder builder = UserBuilder.from(originalUser);

        // Verify hasXxx() returns true for all fields that were set in originalUser
        assertThat(builder.hasName()).isTrue();
        assertThat(builder.hasAge()).isTrue();
        assertThat(builder.hasBirthday()).isTrue();
        assertThat(builder.hasHobbies()).isTrue();
        assertThat(builder.hasAttributes()).isTrue();
        // For non-nullable collections, build() creates empty collections, so from() will set has flag
        assertThat(builder.hasTags()).isTrue(); // Empty collection created by build()
        assertThat(builder.hasScores()).isTrue(); // Empty collection created by build()
        assertThat(builder.hasRoles()).isTrue(); // Empty collection created by build()
    }

    @Test
    void testHasMethodsWithCollections() {
        // Test hasXxx() for collection fields
        UserBuilder builder = UserBuilder.of();
        assertThat(builder.hasHobbies()).isFalse();
        assertThat(builder.hasAttributes()).isFalse();

        // Test add methods
        builder.addHobbies("Reading");
        assertThat(builder.hasHobbies()).isTrue();

        // Test put methods
        builder.putAttributes("key", "value");
        assertThat(builder.hasAttributes()).isTrue();

        // Test addAll with empty collection
        builder = UserBuilder.of();
        builder.addAllHobbies(List.of());
        assertThat(builder.hasHobbies()).isTrue(); // Should be true even with empty collection

        // Test putAll with empty map
        builder = UserBuilder.of();
        builder.putAllAttributes(Map.of());
        assertThat(builder.hasAttributes()).isTrue(); // Should be true even with empty map
    }

    @Test
    void testHasMethodsForAllFields() {
        // Comprehensive test for all field types
        UserBuilder builder = UserBuilder.of()
                .setName("John Doe")
                .setAge(30)
                .setBirthday(LocalDate.of(1993, 1, 1))
                .addHobbies("Reading")
                .putAttributes("key", "value")
                .addTags("tag1")
                .putScores("math", 95)
                .addRoles("admin");

        // Verify all hasXxx() methods return true
        assertThat(builder.hasName()).isTrue();
        assertThat(builder.hasAge()).isTrue();
        assertThat(builder.hasBirthday()).isTrue();
        assertThat(builder.hasHobbies()).isTrue();
        assertThat(builder.hasAttributes()).isTrue();
        assertThat(builder.hasTags()).isTrue();
        assertThat(builder.hasScores()).isTrue();
        assertThat(builder.hasRoles()).isTrue();
    }
}
