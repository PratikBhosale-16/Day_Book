# Security rules

Daybook holds people's personal plans. That is sensitive data even though it looks mundane. Treat it that way.

## Non-negotiable

1. **No network in v1.** Do not add `INTERNET` permission. If a library requires it, that library is the wrong choice — say so instead of adding the permission.
2. **No analytics or telemetry that transmits user content.** Task text, page titles, and bucket names never leave the device. If crash reporting is added later, task content must be explicitly scrubbed from breadcrumbs and logs.
3. **Never log user content.** No `Log.d("task: $taskText")`, not even in debug builds — debug logs get shipped by accident and are readable by other apps on rooted devices. Log IDs and counts, never text.
4. **Minimum permissions.** Currently justified: `POST_NOTIFICATIONS`, exact-alarm permission, `RECEIVE_BOOT_COMPLETED`. Nothing else without an explicit decision. Never request broad storage permissions — use Storage Access Framework for the font picker.
5. **Request permissions in context**, at the moment the user does the thing that needs them, never in a batch at launch.

## Data handling

- Room database lives in app-private storage. Do not move it to external/shared storage.
- `android:allowBackup` — decide deliberately. If enabled, user tasks go to Google's cloud backup; if that is not intended, set it to `false` and document the choice.
- Set `android:exported="false"` on every component that does not genuinely need to be reachable from outside the app.
- **Validate every incoming Intent.** The widget's `ActionCallback` and the notification action receiver accept IDs from outside the app process — verify the ID exists and belongs to the user's data before acting on it. Never trust an intent extra blindly.
- No `WebView` in this app. If one ever appears, it needs its own review.

## Custom font upload (the largest attack surface in v1)

- Accept only through `ACTION_OPEN_DOCUMENT`.
- Enforce a size cap before reading the file into memory.
- Validate that it parses as a font; reject and show a clear error on failure rather than crashing.
- Copy into app-private storage and use that copy. Never persist a URI pointing at user-controllable storage.
- Wrap font loading in error handling — a malformed font must not be able to crash the widget process repeatedly, which is how a widget gets silently removed by the launcher.

## Secrets and repo hygiene

- No keystores, signing configs, API keys, or `local.properties` in version control. Verify `.gitignore` covers them before the first commit.
- If a secret is ever committed, say so immediately and treat it as compromised — rewriting history is not sufficient on its own.

## Dependencies

- Every new dependency needs a stated reason. Prefer the platform and AndroidX over third-party libraries.
- Prefer well-maintained libraries with recent releases. Flag anything unmaintained.
- Pin versions in the version catalog. No dynamic version ranges (`1.2.+`) — they make builds non-reproducible and silently pull unreviewed code.
