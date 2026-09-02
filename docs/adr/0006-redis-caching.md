# ADR-0006: Redis for caching hot reads

## Status
Accepted

## Context
Popular events are viewed far more often than they change. Hitting PostgreSQL on
every event-detail view wastes database capacity and slows responses under load,
exactly when a hot show is on sale.

## Decision
Cache event reads in Redis, an in-memory store. The Event service caches getById
results with a short TTL; the database is queried only on a cache miss.

## Consequences
+ Far fewer DB hits for hot events; faster reads; the DB is protected under load.
- Cached data can be briefly stale (bounded by the TTL); we must evict on writes.
- Adds Redis as infrastructure (also used later for seat holds).
