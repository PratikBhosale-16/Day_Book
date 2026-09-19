# Accuracy rules — do not hallucinate

These rules exist because a confidently wrong answer costs more time than an admitted uncertainty. Follow them on every task.

## Verify before you write

1. **Never invent an API.** Before using any class, function, annotation, or parameter from Glance, Compose, Room, WorkManager, Hilt, or any library — confirm it exists in the version pinned in `gradle/libs.versions.toml`. If you cannot confirm it, look it up. If you still cannot confirm it, say so and ask.
2. **Glance is not Compose.** Do not assume a Compose modifier, API, or animation capability exists in Glance because it exists in Compose. Glance has a much smaller API surface. Verify every Glance API individually.
3. **Never invent a version number.** Check the version catalog or the library's actual releases. Do not guess "the latest is probably X."
4. **Never reference a file, class, or function you have not opened.** Read it first. Do not assume a file's contents from its name.
5. **Do not invent project structure.** If you need to know where something lives, list the directory. Do not assume.

## When you are unsure

State it plainly and stop. Acceptable and expected:

> "I'm not certain whether `GlanceModifier.clickable` supports this in 1.1.x. I'd rather verify than guess — should I check the docs, or do you know?"

Never acceptable:

- Writing plausible-looking code for an API you did not verify
- Filling a gap in the spec with an invented requirement
- Claiming something works when you have not run it

**"I don't know" is a correct answer. Inventing something is not.**

## Never claim unverified success

- Do not say a build passes unless you ran it and saw it pass.
- Do not say tests pass unless you ran them and saw the output.
- Do not say the widget renders correctly unless it was actually rendered and checked.
- If you could not run something (no device attached, no emulator), say that explicitly instead of assuming the result.

Report what actually happened, including failures. A reported failure is useful; a false success wastes hours.

## Stay inside the spec

- `docs/PRD.md` defines v1 scope. If a request falls in the explicit "Out" list, **stop and ask** rather than building it.
- Do not add features nobody asked for — no "I also added dark mode toggle / sync / tags while I was in there."
- If the specs contradict each other, flag the contradiction. Do not silently pick one.
- If a requirement seems wrong, say why. Do not implement something you believe is a mistake without flagging it.

## Scope discipline

- One task per session where possible. Finish it, verify it, commit it, then take the next one.
- Do not refactor unrelated code while implementing a feature.
- If a task turns out to be much larger than it looked, stop and say so rather than producing a sprawling half-finished change.

## Assumptions

When you must assume something to proceed, write the assumption down — in your response and in the commit body. An assumption that is visible can be corrected; a silent one becomes a bug.
