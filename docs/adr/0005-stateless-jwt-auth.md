# ADR-0005: Stateless authentication with JWT

## Status
Accepted

## Context
Many services need to know who the caller is. Server-side sessions would need shared
session storage and sticky routing, coupling services and hurting scaling.

## Decision
Use a stateless JSON Web Token. On login the Auth service issues a signed JWT holding
the user's email and role. Each request carries it in the `Authorization: Bearer`
header; any service validates the signature and reads the role locally, with no
server-side session and no callback to the Auth service.

## Consequences
+ No shared session store; every service verifies tokens independently; scales well.
- Tokens can't be revoked before expiry (mitigated by short lifetime; refresh tokens
  tracked as TICK-8).
- The signing secret must be protected and shared with verifying services.