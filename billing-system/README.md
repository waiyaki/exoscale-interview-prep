# Simple Metered Billing System

## Objective
Define a simple metered billing system feeding off a list of usage records, where the usage records designate events happening on resources. The billing system should have a `process-usage` function which computes a collection of usage records and produces a collection of billing statements, one per account.

## Assumptions
1. Every `:usage.event/create` event of a particular resource has a corresponding `:usage.event/destroy` event for the same resource. `create` events without corresponding `destroy` events are ignored.

## Unmet Requirements and Corresponding Constraints
The following requirements were not met:
1. Produce a single billing statement per account. This requirement could not be met because it collides with the output format, where each billing record from `process-usage` has `:usage/uuid` and `:usage/resource` fields. This was interpreted as a single account could have multiple billing records derived from different individual usage event pairs based on the usage event UUIDs (and resource types).

## Pending Questions
- Can there be usage events with the same `:usage/uuid` but different `:usage/resource`?
