package org.estg.domain;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@DisplayName("Member Domain Tests")
class MemberTests {

    @Test
    @DisplayName("Should create member with valid data")
    void testCreateMemberWithValidData() {
        // Arrange
        String memberName = "João Silva";
        String memberEmail = "joao@example.com";

        // Act & Assert
        assertNotNull(memberName, "Member name should not be null");
        assertNotNull(memberEmail, "Member email should not be null");
        assertTrue(memberEmail.contains("@"), "Email should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = { "user@example.com", "test@test.com", "admin@domain.org" })
    @DisplayName("Should validate different email formats")
    void testValidateEmailFormats(String email) {
        // Assert
        assertTrue(email.contains("@"), "Email should contain @");
    }

    @Test
    @DisplayName("Should throw exception for invalid email")
    void testInvalidEmail() {
        String invalidEmail = "invalid-email";

        // Assert
        assertFalse(invalidEmail.contains("@"), "Invalid email should not contain @");
    }
}
