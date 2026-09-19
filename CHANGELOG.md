# Changelog

All notable changes to Daybook are recorded here. Format follows Keep a Changelog; versions follow semver.

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
