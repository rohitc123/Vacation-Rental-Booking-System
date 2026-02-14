package com.example.AirBnb.App.service;

import com.example.AirBnb.App.entities.Booking;

public interface CheckoutService {
    public String getCheckoutSession(Booking booking, String successUrl, String failureUrl);
}
