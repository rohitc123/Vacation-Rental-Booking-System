package com.example.AirBnb.App.stratergy;

import com.example.AirBnb.App.entities.Inventory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PricingService {

    public BigDecimal calculateDynamicPricing(Inventory inventory){
        PricingStrategy pricingStrategy=new BasePricingStrategy();
        pricingStrategy = new SurgePricingStrategy((BasePricingStrategy) pricingStrategy);
        pricingStrategy = new OccupancyPricingStrategy(pricingStrategy);
        pricingStrategy = new UrgencyPricingStrategy(pricingStrategy);
        pricingStrategy = new HolidayPricingStrategy(pricingStrategy);


        return pricingStrategy.calculatePrice(inventory);

    }

    //return the sum of price of inventory
    public BigDecimal calculateTotalPrice(List<Inventory> inventoryList){
        return inventoryList.stream()
                .map(this::calculateDynamicPricing)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
    }

}
