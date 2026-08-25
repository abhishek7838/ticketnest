# TicketNest

An event-ticketing platform (concerts, comedy, workshops) built as microservices.
Browse events, pick real seats that are held while you pay, get a QR ticket, and get
scanned in at the door. Organizers manage their own events and see their own sales.

## Constraint
100% free and open-source. No paid services at any stage:
PostgreSQL (+ PL/pgSQL, not Oracle), Redis, Kafka/RabbitMQ, free hosting
(Render / Fly.io / Oracle Cloud Always Free, not AWS), Razorpay test mode for payments,
Mailhog for email.

## Services
- **auth** — login, registration, JWT, roles (customer, organizer, admin, gate-staff)
- **event** — events, venues, seat maps, pricing
- **booking** — seat holds (Redis) + booking lifecycle  ← core
- **payment** — payment orders + webhook verification
- **ticket** — signed QR generation + gate scan-in
- **notification** — emails
- **reporting** — sales analytics (CQRS read model)
- **platform** — API Gateway, Eureka, Config Server

## Tech
Java 21, Spring Boot 4.1, Spring Cloud, PostgreSQL, Redis, Kafka/RabbitMQ,
Spring Security + JWT, ZXing (QR), Docker, React + Tailwind/Bootstrap.

## Status
Step 1 of 19 — project kickoff & architecture. See docs/adr/ for decisions.