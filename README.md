Vacation-Rental-Booking-System

A scalable backend system inspired by Airbnb, built using Spring Boot, PostgreSQL, JPA, DTO architecture, Global Exception Handling, Pricing Strategy Pattern, and Scheduled Jobs.

Features

1) Hotel & Room Management

  - Create, update, delete hotels

  - Add rooms, inventory, contact info

  - Update minimum pricing

  - Structured DTO-based responses
    

2) Hotel Browsing

  - Paginated hotel search

  - Filter hotels

  - Fetch hotel details with rooms
    

3) Booking Workflow

  - Initiate booking

  - Add guest details

  - Initiate payment

  - Capture payment
    

4) Dynamic Pricing Engine

  - Using Strategy + Decorator Pattern

  - Base price

  - Surge pricing

  - Urgency pricing

  - Holiday pricing

  - Occupancy-based pricing
    

5) Scheduler

  - Auto-update dynamic pricing every X minutes

6) Exception Handling

  - GlobalExceptionHandler

  - Standard ApiResponse & ApiError format
