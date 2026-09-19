---
description: Verify, version, changelog, and commit a completed change. Run this for every change before it is considered done.
---

# Build and commit

Run these steps in order. **If any step fails, stop and fix it — never commit past a failure.**

## 1. Verify

```bash
./gradlew ktlintCheck detekt      # skip whichever is not configured yet
./gradlew test                    # unit tests
./gradlew assembleDebug           # must compile clean
```

If the change touched the data layer, Glance widget, or notifications, also run:

```bash
./gradlew connectedAndroidTest    # requires a device/emulator
```

## 2. Check against the rules

Before committing, confirm the change does not violate:

- `.agents/rules/security.md` — no new permissions, no logged user content, no new network capability
- `.agents/rules/performance.md` — no new polling, no main-thread I/O, no extra background work

If the change adds a dependency, state why in the commit body.

## 3. Widget sanity check (only if the change touched data, widget, or notifications)

The widget breaking silently is this project's worst failure mode. Confirm:

- Widget still renders after the change
- Completing a task from the widget still writes through and re-renders
- App and widget show the same state

## 4. Version

Bump `versionName` in the app's `build.gradle.kts` using semver:

- **patch** (`1.0.0 -> 1.0.1`) — bug fix, no behavior change
- **minor** (`1.0.0 -> 1.1.0`) — new feature, backwards compatible
- **major** — breaking change to data or user-facing behavior

Increment `versionCode` by 1 for every bump, always.

## 5. Changelog

Add an entry at the top of `CHANGELOG.md` under the new version heading, with today's date. Group as `Added` / `Changed` / `Fixed` / `Security`. Write for a human reading release notes, not a commit log.

## 6. Commit

Conventional commit format:

```
feat(widget): complete tasks from widget without opening app

Adds an ActionCallback that writes through the repository and
triggers a widget update. Validates the incoming task ID before
acting on it per security rules.

Version: 1.1.0
```

Types: `feat`, `fix`, `perf`, `refactor`, `docs`, `test`, `chore`, `security`.

## 7. Push

```bash
git push origin main
```

Tag releases (not every commit):

```bash
git tag -a v1.1.0 -m "v1.1.0"
git push origin v1.1.0
```

## Rules for this workflow

- **Never** use `git push --force` on `main`.
- **Never** commit `local.properties`, keystores, or signing config.
- One logical change per commit. If you did two things, make two commits.
- If a test was failing before your change, say so rather than quietly fixing or skipping it.
