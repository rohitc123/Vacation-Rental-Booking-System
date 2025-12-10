package com.example.AirBnb.App.stratergy;

import com.example.AirBnb.App.entities.Inventory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@RequiredArgsConstructor
public class SurgePricingStrategy implements PricingStrategy{

    private final BasePricingStrategy wrapped;


    @Override
    public BigDecimal calculatePrice(Inventory inventory) {
        BigDecimal price=wrapped.calculatePrice(inventory);
        return price.multiply(inventory.getSurgeFactor());
    }
}
