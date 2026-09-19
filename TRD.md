# Daybook — Technical Requirements

## Architecture

Single-module-first, clean layering. Split into Gradle modules only when build times justify it — premature modularization costs more than it saves at this size.

```
app/
  data/        Room entities, DAOs, repositories, DataStore
  domain/      models, use cases (pure Kotlin, no Android imports)
  ui/          Compose screens, viewmodels, theme
  widget/      Glance widget, receivers, update logic
  work/        WorkManager workers (rollover, archive cleanup)
  notify/      AlarmManager scheduling, notification builders
```

**Rule:** the widget and the app both read from the same repository. Never let the widget keep its own copy of state — divergence between what the widget shows and what the app holds is the single worst bug class in this product.

## Data model

```
Bucket
  id, name, colorKey, sortOrder, isDefault

Page
  id, bucketId, title, sortOrder, createdAt,
  sealedAt (null until complete), archivedAt (null until archived)

Task
  id, pageId, text, isDone, doneAt, sortOrder,
  reminderAt (nullable), reminderRequestCode (nullable)

Settings (DataStore, not Room)
  rolloverMode: AUTO_CARRY | MANUAL
  pauseBeforeTurn: Boolean (default true)
  archiveAutoClear: Boolean (default false)
  archiveRetentionDays: Int? (null unless enabled)
  widgetFadeDelayMinutes: Int
  taskFontKey: String
  customFontUri: String?
  defaultReminderTime: LocalTime
```

Derived, never stored: a page's progress (count of done tasks / total). Computing it is cheap; storing it creates a second source of truth that will drift.

## Widget (Glance) — the critical path

- Built with **Jetpack Glance**, not RemoteViews directly.
- Updates are **push, not poll**: when the repository changes, trigger a widget update. Never schedule periodic refreshes to "keep it fresh" — that burns battery and is the most common cause of a widget app being uninstalled.
- **Tap-to-complete runs through an `ActionCallback`** that writes to the repository, then updates the widget. It must not launch the app.
- Widgets render in a **separate process** with a hard memory limit and a payload size cap. Keep the rendered tree small; render the tasks for one page only, never a full list.
- Glance does **not** support arbitrary animation. The strike-through and page-turn on the widget are **state transitions between two rendered snapshots**, not continuous animations. In-app (Compose) they can be true animations. Do not attempt to simulate draggable page-turn physics inside the widget.
- Support Android's widget resizing: define `targetCellWidth`/`targetCellHeight` and a sensible `minWidth`/`minHeight`. Degrade gracefully — fewer visible tasks at small sizes, never clipped text.
- Test on at least one third-party launcher, not just Pixel Launcher. Widget behavior varies.

## Background work

- **Midnight rollover** — WorkManager, daily. Respects the global rollover setting. Must be idempotent: running twice in one day must not duplicate tasks.
- **Archive cleanup** — WorkManager, daily, and only if the user enabled auto-clear. Never runs when the setting is off.
- **Widget fade** — after a page seals, schedule a one-shot update after the user's delay. Cancel it if the user un-completes a task.
- All workers must handle **device reboot** (`BOOT_COMPLETED` re-registration) and Doze. Assume they will be delayed; never assume exact timing for non-alarm work.

## Reminders

- Use `AlarmManager` with exact alarms for user-set reminder times. On Android 12+ this requires `SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` — check `canScheduleExactAlarms()` and fall back to an inexact alarm with a clear explanation rather than silently failing.
- Store the alarm request code with the task so it can be cancelled when the task is edited, completed, or deleted. **Orphaned alarms firing for deleted tasks is a real and embarrassing bug — guard against it explicitly.**
- Notification actions (`Mark done`, `Remind me later`) are handled by a `BroadcastReceiver` that writes to the repository and updates the widget, without starting an Activity.
- Re-register all pending alarms on `BOOT_COMPLETED`. Alarms do not survive reboot.

## Custom fonts

- Accept the file via Storage Access Framework (`ACTION_OPEN_DOCUMENT`). **Do not request broad storage permissions.**
- Validate before accepting: confirm it parses as a real font, enforce a size cap (suggest 5 MB), and reject anything that fails to load.
- Copy into app-private storage; never hold a long-lived URI to a location the user can change underneath you.
- The font must be loadable from the **widget process** too — verify this explicitly, it is easy to get working in-app and silently fail on the widget.
- Applies to task text only. UI chrome always uses the bundled font.

## Testing

- **Unit:** rollover logic, seal/progress calculation, archive retention, alarm scheduling and cancellation. These are the rules that corrupt user data when wrong — cover them properly.
- **Instrumented:** Room migrations (every one, both directions where applicable), widget update after a data change, notification action handling.
- **Manual before release:** add widget on two launchers, resize it, complete from lock screen, reboot with a pending reminder, change system font size and dark mode.

## Migrations

Never ship a destructive migration. Every schema change gets a written `Migration` and a test. Export the Room schema to version control so migrations can be diffed.

## Build

- Gradle version catalogs (`libs.versions.toml`) for dependencies.
- R8/ProGuard enabled for release; verify Room, Glance, and reflection-dependent code survive obfuscation.
- Debug and release signing configs kept out of version control (see security rules).
