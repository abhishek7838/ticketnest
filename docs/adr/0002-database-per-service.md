# ADR-0002: Database per service

## Status
Accepted

## Context
Sharing one database across services creates hidden coupling: a schema change in one
service can break another, and services can't evolve independently.

## Decision
Each service owns its own database. No service reads another's tables directly;
cross-service data is fetched via that service's API or received as events.

## Consequences
+ Independent deployment/scaling, clear ownership, failure isolation.
- Some data duplication; cross-service data needs API calls or events
  (eventual consistency).