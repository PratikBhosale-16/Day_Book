# Performance rules

The widget is the product. A widget that is slow, stale, or battery-hungry gets removed from the home screen, and the app dies with it.

## Widget performance (highest priority)

- **Push updates, never poll.** Update the widget when data actually changes. Do not schedule periodic refreshes "to keep it fresh."
- **No `updatePeriodMillis`-driven frequent refresh.** If periodic update is configured at all, keep it long (hours, not minutes).
- Keep the rendered tree small. Render one page's tasks only. A widget that renders too much gets killed by the system's payload limit.
- Widget updates must complete quickly and off the main thread. Do database reads in a coroutine, then update.
- Coalesce updates: completing three tasks quickly should not trigger three separate widget rebuilds. Debounce.
- Never do file I/O, font parsing, or bitmap work during widget render. Preload and cache.

## Battery

- Prefer WorkManager for deferrable work; it respects Doze and batching. Reserve exact alarms for actual user-set reminders only.
- Never hold wake locks.
- One daily rollover worker, one daily cleanup worker (only when enabled). That is the entire background budget. Adding more needs justification.
- Cancel scheduled work that is no longer needed — a fade-update scheduled for a page the user reopened must be cancelled.

## Database

- All queries off the main thread. Expose Flows from DAOs, collect them lifecycle-aware.
- Index the foreign keys and the sort columns that are actually queried (`pageId`, `bucketId`, `sortOrder`, `archivedAt`).
- Do not compute progress in a query per task — read the page's tasks once and derive.
- Archive queries must be paginated. A user with two years of pages should not load all of them to render the archive screen.

## App UI

- Target stable 60fps (120 where the device supports it) on the home screen. Test on a low-end device, not just an emulator or a flagship.
- Compose: hoist state properly, use stable keys in lists, avoid recomposing the whole page on a single task toggle. Verify with recomposition counts if a screen feels slow.
- Avoid unnecessary `remember` of large objects and avoid allocating in composition.
- Animations should be short (roughly 200ms for the strike, under 400ms for the page turn). Long animations feel slow, not premium.

## Startup and size

- Cold start under 1 second on a mid-range device. The app opens to today's page — nothing else should block first frame.
- No heavy initialization in `Application.onCreate()`. Defer everything that is not needed for the first frame.
- Keep the APK small. Every dependency is size the user pays for on a task app that should feel light.

## Verification

Before a performance-affecting change is committed, state what was measured and how — not "this should be faster." Use Android Studio's profiler, Macrobenchmark, or Battery Historian as appropriate.
