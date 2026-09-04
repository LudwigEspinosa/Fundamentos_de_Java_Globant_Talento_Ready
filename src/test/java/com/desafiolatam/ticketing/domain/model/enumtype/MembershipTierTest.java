package com.desafiolatam.ticketing.domain.model.enumtype;

import com.desafiolatam.ticketing.domain.model.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("MembershipTier Enum Tests")
class MembershipTierTest {

    @ParameterizedTest(name = "Tier {0} with gross {1} calculates discount {2}")
    @CsvSource({
            "REGULAR, 100.0, 0.0",
            "PREMIUM, 100.0, 10.0",
            "VIP, 100.0, 20.0"
    })
    @DisplayName("Should correctly calculate discount Money value object")
    void shouldCalculateDiscountMoney(MembershipTier tier, double grossAmount, double expectedDiscount) {
        // Arrange
        Money gross = Money.of(grossAmount);

        // Act
        Money discount = tier.calculateDiscount(gross);

        // Assert
        assertEquals(expectedDiscount, discount.amount(), 0.001);
    }

    @Test
    @DisplayName("Should return Money.ZERO when gross price is null or zero")
    void shouldReturnZeroForNullOrZeroGross() {
        // Arrange & Act & Assert
        assertEquals(Money.ZERO, MembershipTier.VIP.calculateDiscount(null));
        assertEquals(Money.ZERO, MembershipTier.VIP.calculateDiscount(Money.ZERO));
        assertEquals(0.0, MembershipTier.REGULAR.getDiscountRate(), 0.001);
        assertEquals(0.10, MembershipTier.PREMIUM.getDiscountRate(), 0.001);
        assertEquals(0.20, MembershipTier.VIP.getDiscountRate(), 0.001);
        assertNotNull(MembershipTier.valueOf("VIP"));
    }
}
