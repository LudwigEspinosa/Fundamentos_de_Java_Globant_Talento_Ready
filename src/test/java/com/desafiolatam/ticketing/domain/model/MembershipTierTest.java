package com.desafiolatam.ticketing.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("MembershipTier Enum & Business Rules Tests")
class MembershipTierTest {

    @ParameterizedTest(name = "Tier {0} with gross {1} should calculate discount {2}")
    @CsvSource({
            "REGULAR, 100.0, 0.0",
            "REGULAR, 50.0, 0.0",
            "PREMIUM, 100.0, 10.0",
            "PREMIUM, 250.0, 25.0",
            "VIP, 100.0, 20.0",
            "VIP, 300.0, 60.0"
    })
    @DisplayName("Should correctly calculate discount amount based on tier discount rate")
    void shouldCalculateCorrectDiscountAmount(MembershipTier tier, double grossAmount, double expectedDiscount) {
        // Arrange
        double amount = grossAmount;

        // Act
        double actualDiscount = tier.calculateDiscount(amount);

        // Assert
        assertEquals(expectedDiscount, actualDiscount, 0.001);
    }

    @ParameterizedTest
    @EnumSource(MembershipTier.class)
    @DisplayName("Should return zero discount when gross amount is zero or negative")
    void shouldReturnZeroDiscountForNonPositiveAmount(MembershipTier tier) {
        // Arrange
        double zeroAmount = 0.0;
        double negativeAmount = -50.0;

        // Act
        double discountForZero = tier.calculateDiscount(zeroAmount);
        double discountForNegative = tier.calculateDiscount(negativeAmount);

        // Assert
        assertEquals(0.0, discountForZero, 0.001);
        assertEquals(0.0, discountForNegative, 0.001);
    }

    @Test
    @DisplayName("Should return valid discount rates for all membership tiers")
    void shouldReturnValidDiscountRates() {
        // Arrange & Act & Assert
        assertEquals(0.00, MembershipTier.REGULAR.getDiscountRate(), 0.001);
        assertEquals(0.10, MembershipTier.PREMIUM.getDiscountRate(), 0.001);
        assertEquals(0.20, MembershipTier.VIP.getDiscountRate(), 0.001);
        assertNotNull(MembershipTier.valueOf("VIP"));
    }
}
