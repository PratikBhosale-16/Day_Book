# Android / Kotlin conventions

## Language

- Kotlin only. Explicit types on public APIs; inferred locally is fine.
- Prefer immutable data (`val`, `data class`, immutable collections). Mutable shared state is the enemy of a widget staying in sync.
- Use sealed interfaces for UI state and results rather than nullable-flag combinations.
- No `!!`. Handle null explicitly or restructure so it cannot be null.
- Suspend functions for anything that touches disk. No blocking calls on the main thread, ever.

## Architecture

- Unidirectional data flow: repository emits Flow -> ViewModel maps to UI state -> Compose renders -> events go back down.
- ViewModels hold no Android framework references beyond what is unavoidable. Domain logic lives in pure Kotlin use cases so it is testable without a device.
- One repository per aggregate (buckets/pages/tasks can share one). The widget and the app must use the same repository instance graph via Hilt.

## Compose

- Stateless composables where possible; hoist state to the caller.
- Every composable that renders user data gets a `@Preview` covering: light, dark, and large-font variants.
- No business logic in composables. No database access in composables.
- Use the design tokens from `docs/DESIGN.md` via the theme — never hardcode a hex value in a composable.

## Glance (widget)

- Glance is not Compose. Do not assume Compose APIs, modifiers, or animation support transfer. Verify against Glance's actual API surface.
- Widget state changes go through `ActionCallback` and `updateAll`. Keep callbacks fast.
- Every widget layout needs testing at multiple sizes and on more than one launcher.

## Errors

- No empty catch blocks. Ever.
- User-facing failures get a plain-language message, not an exception string.
- A failure in the widget path must degrade gracefully — show the last known good state rather than an error tile or a blank widget.

## Naming and structure

- Packages by feature, then by layer. Files named after their primary declaration.
- Test files mirror source structure. Test names describe behavior: `rollover_withAutoCarryOff_leavesTasksOnOldPage`.

## What to avoid

- Do not introduce a networking layer, a sync engine, or an account system. They are explicitly out of v1 scope.
- Do not add a new architecture pattern or DI framework alongside Hilt.
- Do not refactor broadly while implementing a feature. Separate commits, separate concerns.
