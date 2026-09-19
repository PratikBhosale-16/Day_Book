# Changelog

All notable changes to Daybook are recorded here. Format follows Keep a Changelog; versions follow semver.

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
