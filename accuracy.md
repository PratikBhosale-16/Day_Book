# Accuracy rules — do not hallucinate

These rules exist because a confidently wrong answer costs more time than an admitted uncertainty. Follow them on every task.

## Verify before you write

1. **Never invent an API.** Before using any class, function, annotation, or parameter from Glance, Compose, Room, WorkManager, Hilt, or any library — confirm it exists in the version pinned in `gradle/libs.versions.toml`. If you cannot confirm it, look it up. If you still cannot confirm it, say so and ask.
2. **Glance is not Compose.** Do not assume a Compose modifier, API, or animation capability exists in Glance because it exists in Compose. Glance has a much smaller API surface. Verify every Glance API individually.
3. **Never invent a version number.** Check the version catalog or the library's actual releases. Do not guess "the latest is probably X."
4. **Never reference a file, class, or function you have not opened.** Read it first. Do not assume a file's contents from its name.
5. **Do not invent project structure.** If you need to know where something lives, list the directory. Do not assume.

## A resumed process is not a cold start

`adb shell am start` on an app whose process is already alive typically resumes the existing activity rather than triggering a fresh `onCreate` — meaning a ViewModel's in-memory state survives untouched, including state left over from earlier manual testing. A "verification" screenshot taken this way can show stale interaction state that looks exactly like a bug. Before taking any screenshot meant to prove a specific state (especially an initial/default state), force-stop the app first (`adb shell am force-stop <package>`) or reinstall, so the screenshot reflects an actual cold start rather than whatever was left in memory from earlier poking around.

## A build passing is not the same as a test running

A test task reporting "BUILD SUCCESSFUL" proves nothing on its own — a misconfigured test runner, an empty test source set, or a test class Gradle fails to discover all produce the exact same green result with zero tests actually executed. This has already happened once on this project (`connectedDebugAndroidTest` reported success with the instrumentation runner not properly wired up).

**Always report the actual test count alongside any test result** — "tests=3, failures=0," not just "BUILD SUCCESSFUL." If you cannot produce a real count from the XML/HTML report, say so explicitly rather than reporting the build status as if it were the test result.

## The hedge is the tell

If you catch yourself writing "X (or Y if...)" for a version number, a file path, or an API — stop. That hedge means you didn't actually verify it, you're presenting two guesses and letting the human pick. Look it up and give one answer, or say plainly you couldn't confirm it.

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