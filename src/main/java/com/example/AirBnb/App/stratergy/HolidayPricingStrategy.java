package com.example.AirBnb.App.stratergy;

import com.example.AirBnb.App.entities.Inventory;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.Set;

@RequiredArgsConstructor
public class HolidayPricingStrategy implements PricingStrategy {

    private final PricingStrategy wrapped;

    // 🔥 Define holidays here
    public static final Set<MonthDay> HOLIDAYS = Set.of(
            MonthDay.of(1, 1),   // New Year
            MonthDay.of(11, 20), // Diwali
            MonthDay.of(12, 25), // Christmas
            MonthDay.of(8, 15)   // Independence Day
    );

    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price = wrapped.calculatePrice(inventory);

        MonthDay todayMonthDay = MonthDay.from(inventory.getDate());

        if (HOLIDAYS.contains(todayMonthDay)) {
            price = price.multiply(BigDecimal.valueOf(1.20));
        }

        return price;
    }

}

