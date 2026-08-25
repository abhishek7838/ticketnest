# ADR-0003: Redis for temporary seat holds

## Status
Accepted

## Context
Marking a seat "booked" in the database the moment a user clicks fails two ways:
abandoned carts lock seats forever, and database row-locks buckle when hundreds of
users fight over the same front-row seats during a hot-show drop.

## Decision
Hold seats temporarily in Redis, not the DB. On seat select:
`SET event:{id}:seat:{n} <holdId> NX EX 300`.
NX = set only if absent (atomic — exactly one of many concurrent clicks wins).
EX 300 = the hold auto-expires in 5 minutes. On payment we promote the hold to a
real booking in the DB and delete the Redis key; on abandonment the key expires
by itself. At confirm time we re-check the hold still belongs to this user.

## Consequences
+ No permanent locks, survives high-concurrency bursts, self-cleaning abandoned holds.
- Hold state lives in Redis and must be reconciled with the DB on confirmation; adds
  Redis as a dependency.