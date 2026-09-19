# Daybook — Design Reference

## Principle

The app should feel like *opening the notebook*, not switching to a different product. The widget, home screen, and icon share one visual language. Settings is the only screen allowed to look like an ordinary utility screen — over-theming a settings list reads as trying too hard.

## Color

**The page background is one consistent warm paper tone across every bucket.** This is what makes the product read as one notebook rather than a pile of colored sticky notes. Bucket identity lives in small accents only — the progress ring, the bucket pill, the page-thumbnail dot — never a full-bleed background wash.

| Token | Value | Use |
|---|---|---|
| `paper` | `#FBF5E9` | page background, everywhere |
| `ink` | `#3A3229` | task text |
| `inkMuted` | `#9A9187` | completed text, secondary labels |
| `todayFill` | `#FCE8B8` | Today bucket accent fill |
| `todayInk` | `#8B5E2B` | Today bucket accent ink |
| `weekFill` | `#A8CBEB` | This week bucket accent fill |
| `weekInk` | `#3D6A87` | This week bucket accent ink |
| `somedayFill` | `#B8E0C4` | Someday bucket accent fill |
| `somedayInk` | `#3F7A52` | Someday bucket accent ink |
| `seal` | `#4C9A6A` | completion checkmark, all buckets |
| `custom1Fill` / `custom1Ink` | `#F0C7D8` / `#8B4A63` | blush — first custom bucket |
| `custom2Fill` / `custom2Ink` | `#D6C7EE` / `#5F4A80` | lilac — second custom bucket |
| `custom3Fill` / `custom3Ink` | `#F5D9B8` / `#8B5E33` | peach — third custom bucket |

Custom bucket colors come from a **curated set, not an open color picker** — personalization without breaking the palette.

**Dark mode is required.** Invert to a warm dark paper (not pure black, not blue-gray); keep accents at the same hue with adjusted luminance. Verify contrast in both themes.

## Typography

- **Task text:** rounded, warm, highly legible. Default preset plus curated alternatives plus user upload (see PRD).
- **UI chrome:** neutral structured sans, kept quiet so task text is the visual lead. Never overridden by a user font.
- Respect the system font-scale setting. Task text must remain readable and unclipped at large accessibility sizes — on the widget, show fewer tasks rather than shrinking or truncating text.

## Components

**Progress ring** — fills by task count in discrete segments, not a smooth percentage arc. With 3-7 tasks, visible steps read more honestly than a percentage. On completion the ring becomes a checkmark.

**Task row** — a completion circle (outlined when open, filled when done), the task text, and an optional bell icon on the right. Completed text gets a strike-through and drops to `inkMuted`.

**Strike-through** — draws across in roughly 200ms, slightly imperfect rather than a robotic straight line, with a light haptic tick. In-app this is a real animation; on the widget it is a transition between two rendered states.

**Write-a-task line** — a faded, dashed row at the end of the list. Tapping it places the cursor there. Not a floating action button, not a modal.

**Page dots** — small, quiet indicators of how many pages exist in the bucket. Enough signal to swipe, not enough detail to turn the widget into an index.

**Bucket pills** — rounded, filled in the bucket's accent when active, outlined when not.

## Screens

**Home:** bucket pills (top) / page card: title + progress ring, task list, write-a-task line (middle) / archive icon, page dots, settings gear (bottom). The page is the visual hero; functional chrome is pushed to the edges.

**Archive:** month-grouped grid of miniature page thumbnails — title, faint strike-lines, bucket-color dot, seal checkmark. Recognizable by shape and color before you read the title. Read-only.

**Settings:** conventional grouped list. Sections: Pages, Archive, Appearance, Reminders. Brand shows up only through the accent color on toggles.

**Font picker:** each preset renders the **same sample phrase** in its actual typeface — comparing font *names* tells the user nothing. Upload sits in its own section with the preview promise stated up front.

## Icon

Spiral-bound coil notebook silhouette; cream page, sky-blue coil binding on the left, soft pink page outline, and at the center a sage progress ring that reads as transitioning into a checkmark. Flat — no gradient mesh, no glossy 3D bevel.

Test at true launcher size (roughly 40-60px) before finalizing: the coil detail and the ring are competing for very little space. If they mush together, reduce to 2-3 coil circles implying the binding rather than depicting it fully.

## Accessibility (not optional)

- Content descriptions on every interactive element, including widget tap targets.
- Minimum 48dp touch targets.
- 4.5:1 contrast for body text in both light and dark themes — verify `inkMuted` on `paper` specifically, muted-on-cream is the likeliest failure.
- Never use color alone to convey bucket identity; pair it with the label.
- Respect reduced-motion settings: if animations are disabled system-wide, complete transitions instantly rather than animating.