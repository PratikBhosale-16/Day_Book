# AGENTS.md — Daybook

> Standing instructions for every agent working in this repo. Read this first, then read only the rule files relevant to your task.

## What this project is

**Daybook** is a widget-first to-do app. The home-screen widget is the product; the app is a backstage room the user rarely opens. Every technical decision should be weighed against one question: *does this make the widget faster, clearer, or more reliable?*

**Platform order:** Android first (ship it, learn from it), iOS second. Do not add iOS code or cross-platform abstractions until Android v1 ships.

## Stack (do not change without an explicit instruction)

- **Language:** Kotlin
- **App UI:** Jetpack Compose
- **Widget:** Jetpack Glance
- **Local storage:** Room (SQLite) — single source of truth
- **Preferences:** DataStore (Proto or Preferences)
- **Async:** Coroutines + Flow
- **DI:** Hilt
- **Background work:** WorkManager (midnight rollover, archive cleanup)
- **Notifications:** AlarmManager (exact reminders) + NotificationCompat actions
- **Min SDK:** 26 · **Target SDK:** latest stable
- **No backend. No accounts. No network calls in v1.** All data is local.

## Where to find things

| You need | Read |
|---|---|
| What to build and why | `docs/PRD.md` |
| Architecture, data model, technical rules | `docs/TRD.md` |
| Colors, type, spacing, component specs | `docs/DESIGN.md` |
| Kotlin/Compose/Glance conventions | `.agents/rules/android.md` |
| Security requirements | `.agents/rules/security.md` |
| Performance requirements | `.agents/rules/performance.md` |
| How to ship a change | `.agents/workflows/build-and-commit.md` |
| Rules against guessing/hallucinating | `.agents/rules/accuracy.md` (read this every task) |

**Read only what you need.** Do not load every doc for every task — a widget rendering fix does not need the PRD.

## Repository

Remote: `https://github.com/PratikBhosale-16/Day_Book.git` (branch `main`)

Every verified change is committed and pushed following `.agents/workflows/build-and-commit.md`.

## Non-negotiables

0. **Never guess.** Verify APIs before using them; never claim a build or test passed without running it. See `.agents/rules/accuracy.md`.
1. **Never break the widget.** Any change touching the data layer must be verified against widget rendering before it is committed.
2. **Offline-first, local-only.** No analytics SDKs, no crash reporters that upload user content, no network permission in v1.
3. **User data is never lost.** Archive is kept by default. Deletions are explicit and user-initiated.
4. **Always run `./gradlew build` and the test suite before committing.** A red build is never committed.
5. **Follow `.agents/workflows/build-and-commit.md` for every commit** — version bump, changelog entry, conventional commit message.
6. **Ask before adding a dependency.** Every new library is a security and size cost. Prefer the platform.

## Working style

- Small, reviewable commits. One logical change per commit.
- Write the test alongside the code, not after.
- When a spec is ambiguous, state your assumption in the commit body rather than silently guessing.
- If a requirement in the docs seems wrong or contradicts another doc, flag it instead of picking one silently.
