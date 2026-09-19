# Antigravity prompts for Daybook

**Use these one at a time, in order.** Do not paste them all at once. Each milestone ends with a working, committed, verified state — that gate between milestones is what keeps the agent honest and stops it inventing code it never ran.

---

## Prompt 0 — Setup (run once)

```
Read AGENTS.md, then read .agents/rules/accuracy.md in full. Follow both for
every task in this project.

Before writing any code, do these in order and report back:

1. List the files currently in this workspace so we both know the starting state.
2. Confirm the local Android toolchain: JDK version, Android SDK location, and
   whether an emulator or physical device is available. If anything required is
   missing, tell me — do not work around it silently.
3. Check whether this workspace is already a git repo and whether the remote
   https://github.com/PratikBhosale-16/Day_Book.git is configured. If not,
   initialise it and add that remote, branch main.
4. Look up the current stable versions of: Jetpack Compose BOM, Glance,
   Room, Hilt, WorkManager, and Kotlin. Do not guess these — verify them,
   and tell me where you checked.

Stop after step 4 and show me the versions you found before continuing.
```

---

## Prompt 1 — Project skeleton

```
Create the Android project skeleton per docs/TRD.md.

- Kotlin, Jetpack Compose, min SDK 26, target latest stable
- Gradle version catalog (gradle/libs.versions.toml) using the versions you
  verified in the previous step
- Package structure: data / domain / ui / widget / work / notify
- Hilt set up, application class, theme scaffold using the tokens in
  docs/DESIGN.md
- .gitignore covering local.properties, keystores, build outputs, .idea

Do not implement any features yet. Do not add any dependency not listed in
docs/TRD.md.

When done: run ./gradlew assembleDebug, show me the actual output, then follow
.agents/workflows/build-and-commit.md to commit and push as v0.1.0.
```

---

## Prompt 2 — Data layer

```
Implement the data layer only, per the data model in docs/TRD.md.

- Room entities: Bucket, Page, Task with the fields specified
- DAOs exposing Flow, with the indices listed in .agents/rules/performance.md
- Repository layer, injected via Hilt
- DataStore for the Settings fields listed in the TRD
- Seed the three default buckets (Today, This week, Someday) on first run
- Export the Room schema to version control

Progress is derived, never stored — re-read the TRD on that point.

Write unit tests for: progress/seal calculation, and default bucket seeding.

When done: run ./gradlew test and show me the real output. If anything fails,
tell me rather than fixing it silently. Then run the build-and-commit workflow.
```

---

## Prompt 3 — Home screen (first vertical slice)

```
Build the Home screen per docs/PRD.md and docs/DESIGN.md.

- Bucket pills, page card with title and progress ring, task list,
  inline "write a task..." dashed line (no FAB, no modal, no separate screen)
- Tap a task to toggle done: strike-through animation ~200ms with a light
  haptic, text drops to inkMuted
- Progress ring fills by task count in discrete segments, becomes a checkmark
  when the page is complete
- Horizontal swipe between pages in a bucket, vertical swipe between buckets
- @Preview for light, dark, and large-font

Use only the design tokens from docs/DESIGN.md. No hardcoded hex values.

When done: build it, launch it on a device or emulator, and show me a
screenshot. If you can't run it, say so explicitly rather than assuming it
works. Then run the build-and-commit workflow.
```

---

## Prompt 4 — The widget (the hard part)

```
Build the home-screen widget with Jetpack Glance, per docs/TRD.md.

Before writing code: confirm which Glance APIs you plan to use actually exist
in the Glance version we pinned, and tell me what you verified. Glance is not
Compose — do not assume Compose APIs transfer.

Requirements:
- Renders one page: title, progress ring, task list, page dots
- Tap a task to complete it — writes through the repository and re-renders,
  without launching the app
- Push updates only. No polling, no periodic refresh (see
  .agents/rules/performance.md)
- Validate the incoming task ID in the ActionCallback before acting on it
  (see .agents/rules/security.md)
- Handles resizing; degrades by showing fewer tasks, never clipped text
- Falls back to last known good state on error, never a blank or error tile

Test it on an emulator at two sizes, and confirm app and widget stay in sync.
Show me what you actually observed. Then run the build-and-commit workflow.
```

---

## Prompt 5 onward

Continue one milestone at a time, same pattern each time — build, verify with real output, commit:

- **Reminders** — bell chips, exact alarms, notification actions, boot re-registration
- **Archive** — month-grouped thumbnails, paginated, read-only
- **Settings** — grouped list, all settings from the PRD wired to DataStore
- **Rollover + cleanup** — WorkManager, idempotent, reboot-safe
- **Custom fonts** — SAF picker, validation, widget-process loading, live preview
- **Onboarding** — blank-page first screen, add-widget prompt

---

## If the agent starts drifting

Paste this:

```
Stop. Re-read .agents/rules/accuracy.md.

Tell me specifically:
- Which APIs in what you just wrote did you actually verify, and where?
- Which did you assume?
- Did you actually run the build and tests, or are you inferring they pass?

Don't rewrite anything yet. Just answer.
```

---

## A note on committing every build

`.agents/workflows/build-and-commit.md` is run by the agent as a deliberate
step, not fired automatically on every compile. That's on purpose: a build that
compiles is not a build that works, and automatic commits would fill the repo
with noise and happily commit broken behavior. If you want it fully hands-off
later, that belongs in a git hook or CI, not in the agent's instructions.
