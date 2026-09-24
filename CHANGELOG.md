# Changelog

All notable changes to Daybook are recorded here. Format follows Keep a Changelog; versions follow semver.

## [v0.4.0] - 2026-09-24
### Added
- Home screen widget (Jetpack Glance): Shows current "Today" page title, task list, and discrete progress ring.
- Tap to complete tasks directly from the widget without launching the app.
- Dynamic responsive layout (SizeMode.Responsive) that degrades gracefully by showing fewer tasks on smaller tiles rather than clipping text.
- Fallback UI: If a live data fetch fails, the widget automatically re-renders its last-known-good state (persisted via DataStore) rather than showing a blank or error tile.
- Centralized widget update architecture: `DaybookRepositoryImpl` triggers a debounced (400ms) widget refresh automatically on every write.

### Changed
- Refactored `DatabaseModule` CoroutineScope to use a dedicated `@ApplicationScope` qualifier for safer injection into the widget coordinator.

## [v0.3.0] - 2026-09-22
### Added
- Home screen: bucket pills (outlined/filled per active state), horizontal page pager,
  page title + discrete-segment progress ring, task list, and inline write-a-task dashed
  row — no FAB, no modal, per DESIGN.md.
- Task toggle: 200ms animated strikethrough with slight hand-drawn imperfection, text
  drops to `inkMuted`, light haptic tick on completion.
- Progress ring becomes a seal checkmark when all tasks are done (`isComplete`).
- Bucket pills use correct accent fill/ink from DESIGN.md per `ColorKey`; no hardcoded
  hex values in any composable.
- `HomeViewModel` via Hilt + UDF: repository Flow → ViewModel → sealed `HomeUiState` →
  Compose. Task toggle and add are off-main-thread coroutines.
- `MainActivity` with `@AndroidEntryPoint`, edge-to-edge, `safeDrawingPadding`.
- `@Preview` variants for light, dark, and large-font (`@PreviewFontScale`).
- Added `material-icons-extended` (from Compose BOM), `hilt-navigation-compose:1.4.0`,
  `lifecycle-viewmodel-compose:2.11.0`, and `lifecycle-runtime-compose:2.11.0`.
- Fixed gradle-wrapper.jar: replaced with the official Gradle 9.6.0 jar sourced from
  `github.com/gradle/gradle` at tag v9.6.0. SHA-256 verified against
  `services.gradle.org` reference: `497c8c2a…a9c7`.

## [v0.2.2] - 2026-09-19
### Added
- Instrumented test: `DatabaseSeederIntegrationTest` verifies the real Room-generated
  `BucketDao` and the `Bucket.name` unique index to prove idempotency against the real
  SQLite constraint. (Requires device to run).
- Added `androidx.test.ext:junit` and `androidx.test:core-ktx` dependencies.
- Updated `docs/PRD.md` to explicitly state bucket names must be unique and flag
  that the Settings UI must validate this and show an error (rather than silently
  failing via `INSERT OR IGNORE`).

## [v0.2.1] - 2026-09-19
### Fixed
- Added `unique = true` to the `name` index on `Bucket` — without this, INSERT OR IGNORE
  had no constraint to resolve against and a second seed run would have inserted 6 rows.
- Replaced the shape-only DatabaseSeeder tests with three real idempotency tests using a
  FakeBucketDao that simulates INSERT OR IGNORE by name: `rowCountStaysAtThree`,
  `noDuplicateNames`, and `originalIdsPreserved` after calling seedDefaultBuckets twice.
  All 15 unit tests pass.

## [v0.2.0] - 2026-09-19
### Added
- Room entities: Bucket, Page, Task with all TRD fields and required FK indices
  (`pageId`, `bucketId`, `sortOrder`, `archivedAt`).
- ColorKey enum and DaybookTypeConverters (stored by name, not ordinal — reorder-safe).
- DAOs (BucketDao, PageDao, TaskDao) exposing Flow-based observe and suspend mutations.
- Room schema export to `app/schemas/` (version-controlled per TRD).
- DatabaseSeeder callback seeds Today, This week, Someday buckets on first DB creation
  (idempotent via INSERT OR IGNORE).
- DataStore Preferences for all Settings fields (RolloverMode, pauseBeforeTurn,
  archiveAutoClear, archiveRetentionDays, widgetFadeDelayMinutes, taskFontKey,
  customFontUri, defaultReminderTime).
- Domain models: RolloverMode, Settings, PageProgress (derived, never stored).
- DaybookRepository interface (domain layer) and DaybookRepositoryImpl.
- Hilt DatabaseModule providing Room, DAOs, DataStore, repository, and
  application-scoped CoroutineScope.
- 13 unit tests: PageProgressTest (7 cases), DatabaseSeederTest (6 cases). All pass.
- DataStore 1.2.1 and kotlinx-coroutines-test 1.9.0 added to version catalog.

## [v0.1.1] - 2026-09-19
### Fixed
- Fixed hardcoded light theme by replacing it with a Material3 DayNight theme for system dark mode support.
- Configured Gradle Wrapper strictly to version 9.6.0 with distribution checksum.

## [v0.1.0] - 2026-09-19
### Added
- Initial project skeleton for Android app and Jetpack Glance widget
- Package structure: data, domain, ui, widget, work, notify
- Version catalog mapped to AGP 9.4.0, Kotlin 2.0.20, KSP, and Compose BOM 2026.08.00
- Theme foundation defined using DESIGN.md tokens
- DaybookApp with Hilt setup

## [Unreleased]

### Added
- Project scaffolding: PRD, TRD, design reference, agent rules and workflows
