# 🚀 Vacation Rental Booking System

A **scalable, production-ready backend system** for a vacation rental platform that supports **hotel management, real-time booking, dynamic pricing, and secure payment processing**.

---

## 🧠 Key Highlights

- 🔐 Implemented **JWT-based authentication & role-based access control** (Admin/User)
- 🏨 Built **hotel, room, and inventory management system**
- 📅 Designed **end-to-end booking lifecycle** (initiate → guest → payment → confirmation)
- 💳 Integrated secure payment system using Stripe with webhook handling
- ⚡ Prevented **double booking using pessimistic locking**
- 🔄 Ensured **data consistency with transactional integrity (`@Transactional`)**
- 💰 Developed **dynamic pricing engine** using Strategy + Decorator patterns
- ⏱️ Automated pricing updates using Spring Scheduler
- 🧩 Built clean APIs using **DTO architecture + global exception handling**

---

## 🏗️ Architecture

- Layered Architecture (**Controller → Service → Repository**)
- DTO-based request/response handling
- Centralized exception handling (`GlobalExceptionHandler`)
- Standard API response structure (`ApiResponse`, `ApiError`)

---

## ⚙️ Features

### 👨‍💼 Admin (Hotel Manager)

- Manage hotels (create, update, delete, activate)
- Manage rooms (add, update, delete)
- Manage inventory (availability per date range)
- View bookings by hotel
- Generate hotel-level reports

---

### 👤 User (Customer)

- Search & filter hotels
- View hotel and room details

#### Booking Workflow:

- Initiate booking
- Add / update / delete guests
- Initiate payment
- View booking status
- Cancel booking

---

## 💳 Payment Integration

- Integrated Stripe for secure payment processing
- Created payment intents during booking flow
- Implemented webhook-based event handling for reliable confirmation
- Updated booking status only after verified payment success
- Handled failure scenarios and retry cases

---

## 🔄 Booking, Concurrency & Consistency

- Prevented **double booking** using **pessimistic locking**
- Ensured **atomic operations** using `@Transactional`
- Maintained **data consistency** across booking and payment flow

---

## 💰 Dynamic Pricing Engine

Implemented using **Strategy + Decorator Pattern**:

- Base Price
- Surge Pricing (high demand)
- Urgency Pricing (last-minute booking)
- Holiday Pricing
- Occupancy-based Pricing

---

## ⏱️ Scheduler

- Auto-updates dynamic pricing at fixed intervals using Spring Scheduling

---

## ⚠️ Exception Handling

- Centralized `GlobalExceptionHandler`
- Standard `ApiResponse` & `ApiError` format

---

## 🧰 Tech Stack

- **Backend:** Spring Boot
- **Database:** PostgreSQL
- **ORM:** Spring Data JPA
- **Security:** Spring Security + JWT
- **Payments:** Stripe
- **Build Tool:** Maven
- **Scheduler:** Spring Scheduling
- **Object Mapping:** ModelMapper
