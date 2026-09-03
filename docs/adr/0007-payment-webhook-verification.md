# ADR-0007: Verify payment webhooks server-side

## Status
Accepted

## Context
After a user pays, the payment gateway notifies us via a webhook. We must never
trust the browser (or an attacker) simply claiming "I paid" — that would let anyone
confirm a booking for free.

## Decision
Every payment-success webhook carries an HMAC signature computed by the gateway
using a shared secret. Our Payment service recomputes the signature over the raw
payload with the same secret and accepts the event only if the signatures match.
For local dev we simulate the gateway; the verification logic is identical.

## Consequences
+ Forged "payment succeeded" calls are rejected; only genuine events confirm bookings.
- We must protect the shared secret and verify over the exact raw payload bytes.
- Ties us to the gateway's signing scheme (abstracted behind our verifier).
