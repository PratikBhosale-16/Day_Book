# Daybook

A widget-first to-do app for Android. The home-screen widget is the product; the app is a backstage room you rarely open.

## For agents

Start with `AGENTS.md`. It tells you what to read and what not to read.

## For humans

| Doc | What's in it |
|---|---|
| `docs/PRD.md` | What we're building and why |
| `docs/TRD.md` | Architecture, data model, technical rules |
| `docs/DESIGN.md` | Colors, type, components, accessibility |
| `.agents/rules/` | Standing rules: Android conventions, security, performance |
| `.agents/workflows/` | `/build-and-commit`, `/new-feature` |

## Stack

Kotlin · Jetpack Compose · Glance (widget) · Room · DataStore · Hilt · WorkManager. No backend, no accounts, local-only.
