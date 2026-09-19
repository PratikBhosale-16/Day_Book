# Daybook — Product Requirements

## Problem

Task apps assume you'll open them constantly. Most people won't. Tasks written in an app that stays closed are tasks that don't get done.

## Solution

A to-do app where the home-screen widget is the primary surface. Write once, glance all day, complete from the widget or the notification — rarely open the app at all.

## Who it's for

People who want a calm, tactile task list they actually see — not a project management tool. The emotional target is the satisfaction of a paper planner, not the pressure of a productivity system.

## Core concept

Each list is a **page** of paper. You write on it, cross things off, and when everything is struck through the page gets a seal and turns to reveal the next one.

## Structure

- **Buckets** — default: Today, This week, Someday. Users can rename, reorder, and add custom buckets.
  - **Bucket names must be unique** (deliberate constraint, added v0.2.1). Two buckets with identical names would produce indistinguishable pills in the UI. The database enforces this via a UNIQUE index on `name`. The Settings UI for adding/renaming a bucket must validate the name before writing and show an inline error message on collision (e.g. "A bucket named 'Today' already exists") — a silent `INSERT OR IGNORE` failure is not acceptable user-facing behaviour.
- **Pages** — each bucket holds one or more pages (e.g. "Groceries" and "Work calls" both under Today). The user decides how much goes on a page.
- **Tasks** — a line of text on a page, optionally with a reminder.

## Navigation

- **Vertical swipe** = change bucket
- **Horizontal swipe** = change page within the current bucket
- Small dots indicate how many pages exist. The widget shows exactly one page at a time, always — no matter how many exist.

## The reward system (this is the product's core)

Four layers, ordered by how often they fire:

1. **Per task** — tapping a task draws a strike-through line (~200ms, slightly imperfect, not a robotic straight line) with a light haptic tick.
2. **Per page** — a circular progress ring fills by task *count* (discrete segments, not smooth percentage). When all tasks are struck, the ring becomes a checkmark "seal."
3. **Auto-turn** — a sealed page automatically turns to reveal the next page. A user setting controls whether there's a brief pause on the checkmark first (default: pause on).
4. **Retrospective** — the archive of finished pages becomes a quiet record of what you got done.

**No gamification.** No points, streaks, XP, or confetti. The reward is tactile, not scored.

## Lifecycle of a page

`open -> all tasks struck, sealed -> auto-turns -> fades from widget after a user-set delay -> kept in archive`

Archive auto-clear is **off by default**. The archive is a bonus journal unless the user deliberately turns on cleanup.

## Rollover

Unfinished tasks at midnight either auto-carry to the new page or require manual review. **One global setting**, not per-bucket.

## Screens

1. **Home** — bucket pills on top, the page in the middle (title, progress ring, tasks, a dashed "write a task..." line), archive/dots/settings on the bottom. Adding a task happens inline — no separate screen, no modal, no form.
2. **Archive** — past pages as miniature thumbnails grouped by month, showing title, faint strike-lines, bucket-color dot, seal. Read-only; reopening a task means "copy to today," not unlocking the old page.
3. **Settings** — grouped: Pages (rollover, pause-before-turn), Archive (auto-clear, off by default), Appearance (task font, buckets and colors), Reminders (default time).
4. **Onboarding** — screen one is a blank page with a cursor ("This is your page"). Immediately after the first task is written, prompt to add the widget. No feature carousel.

## Reminders

- Tap a task's bell, then inline chips: Morning / Afternoon / Evening / Custom. Not a date-time picker wheel.
- Notification permission is requested **in context**, the first time a bell is tapped — never at first launch.
- Notification copy matches the product voice: "Coffee beans — still on your page," not "Reminder: Coffee beans."
- Notification actions: **Mark done** and **Remind me later**. Completing from the lock screen without opening the app is a core use case, not a nice-to-have.

## Typography choice (user-facing feature)

Task text uses a rounded, legible font by default. Users can pick from curated presets or upload their own font file (e.g. their own handwriting).

- Uploaded fonts apply to **task text only** — never UI chrome, so navigation stays readable.
- Show a **preview at real widget size before applying**, so legibility problems are visible up front.

## v1 scope

**In:** buckets, pages, tasks, widget, reminders, archive, settings, onboarding, custom fonts, Android only.

**Out (explicitly, for v1):** accounts, cloud sync, sharing, collaboration, subtasks, attachments, recurring tasks, tags, search, iOS, tablets, wear.

## Success means

- The user adds the widget during onboarding (if they don't, the product has failed regardless of everything else).
- Most task completions happen from the widget or a notification, not from inside the app.
