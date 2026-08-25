# ADR-0001: Microservices architecture

## Status
Accepted

## Context
Ticketing has domains with very different loads. When a popular show drops, the
Booking path spikes massively for minutes, while browsing and reporting do not. A
single monolith would force us to scale everything together and would let one
overloaded area take down the rest.

## Decision
Build the platform as independent microservices so each can be deployed and scaled
on its own, with failures isolated between them.

## Consequences
+ Independent scaling (scale Booking during a drop, nothing else), independent
  deploys, fault isolation, clear ownership per service.
- More operational complexity, network calls that can fail, and data spread across
  services (no cross-service SQL joins).