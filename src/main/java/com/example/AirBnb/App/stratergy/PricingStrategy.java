package com.example.AirBnb.App.stratergy;

import com.example.AirBnb.App.entities.Inventory;

import java.math.BigDecimal;

public interface PricingStrategy {
    BigDecimal calculatePrice(Inventory inventory);
}
