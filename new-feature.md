---
description: Implement a new feature end to end, from spec check through committed code.
---

# New feature

## 1. Read only what you need

- `docs/PRD.md` — confirm the feature is in v1 scope. **If it is in the explicit "Out" list, stop and ask** rather than building it.
- `docs/TRD.md` — architecture and data model
- `docs/DESIGN.md` — only if the feature has UI
- The relevant rules file(s)

## 2. Plan before coding

State briefly: what changes in the data layer, what changes in the UI, whether the widget is affected, whether a migration is needed. Surface open questions now, not halfway through.

## 3. Build in this order

1. Domain model and use case (pure Kotlin, testable without a device)
2. Unit tests for the logic
3. Data layer (Room entity/DAO/repository + migration if schema changed)
4. UI (Compose)
5. Widget, if affected
6. Previews for light, dark, and large-font

## 4. Check the failure modes

- What happens with zero data? With a very long task title? With 200 tasks on one page?
- What happens if the user backgrounds the app mid-action?
- What happens after reboot?
- Does it still work with the largest system font size?

## 5. Ship it

Run `.agents/workflows/build-and-commit.md`.
