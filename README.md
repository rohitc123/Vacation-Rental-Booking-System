# Vacation-Rental-Booking-System

A scalable backend system inspired by Airbnb, built using **Spring Boot, PostgreSQL, JPA, DTO architecture, Global Exception Handling, Pricing Strategy Pattern, and Scheduled Jobs**.

---

## Features

### Hotel & Room Management
- Create, update, delete hotels  
- Add rooms, inventory, contact info  
- Update minimum pricing  
- Structured DTO-based responses  

### Hotel Browsing
- Paginated hotel search  
- Filter hotels  
- Fetch hotel details with rooms  

### Booking Workflow
- Initiate booking  
- Add guest details  
- Initiate payment  
- Capture payment  

### Dynamic Pricing Engine
- Using **Strategy + Decorator Pattern**  
- Base price  
- Surge pricing  
- Urgency pricing  
- Holiday pricing  
- Occupancy-based pricing  

### Scheduler
- Auto-update dynamic pricing every X minutes  

### Exception Handling
- `GlobalExceptionHandler`  
- Standard `ApiResponse` & `ApiError` format


## Tech Stack

- **Backend:** Spring Boot  
- **Database:** PostgreSQL  
- **ORM:** Spring Data JPA  
- **Build Tool:** Maven  
- **Design Patterns:** Strategy + Decorator  
- **Scheduler:** Spring Scheduling  
- **Object Mapping:** ModelMapper / MapStruct
- **Security:** Spring Security

