# ADR-0004: Separate Venue and Event tables

## Status
Accepted

## Context
A venue (a physical place) hosts many events over time. Putting venue details
inside every event row would duplicate the venue's data across every event held
there, and updating a venue (e.g. its capacity) would mean editing many rows.

## Decision
Model Venue and Event as separate tables with a many-to-one relationship
(many events -> one venue).

## Consequences
+ No duplication; a venue is edited once; easy to query "all events at a venue".
- Showing an event with its venue needs a lookup/join; creating an event requires
  an existing venueId.