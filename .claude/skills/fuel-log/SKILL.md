---
name: fuel-log
description: Continue work on "Fuel Log", the user's personal nutrition-tracking app published as a claude.ai Artifact. Use whenever the user tells you what they ate (in Hebrew or English) and wants it logged, asks about their calories, protein, vitamins, weight trend or habits, or asks to change, fix or extend the app (pages, language, voice logging, food database, weight/photo tracking, sharing).
---

# Fuel Log — nutrition tracker Artifact

## What it is

- **Live app:** https://claude.ai/artifact/Wk7pqTzScLMwvCgmmmoCDK (title "Fuel Log", private, pinned to the owner's claude.ai sidebar).
- **Source of truth for the page:** `app/fuel-log.html` + `app/foods.json` in this folder. The published version may be newer than the repo if someone edited it in another session, so **always `Artifact action:"read"` the URL first** and diff against `app/fuel-log.html` before republishing.
- The owner speaks **Hebrew**. Reply in Hebrew unless they write in English. Default UI language is Hebrew (RTL).
- One self-contained HTML page (no framework). Fonts: Archivo / Assistant / IBM Plex via Google Fonts.

## Publishing

From a new conversation, read the artifact first (required before any publish with `url`), then:

```
Artifact publish
  url: https://claude.ai/artifact/Wk7pqTzScLMwvCgmmmoCDK
  file_path: <this folder>/app/fuel-log.html
  files: {"foods.json": "<this folder>/app/foods.json"}
```

Omit `capabilities` to keep them. If you must restate them, pass the full set exactly:

```json
{"db": {"rules": [{"path": "", "read": "admin", "write": "admin"},
                  {"path": "data/users/{self}", "read": "interact", "write": "interact"}]},
 "user": {}, "sample": {}, "assets": {"upload": "interact"}, "dictation": {}}
```

After any change: run the tests (below), publish, then copy the published file back into `app/` and commit.

## Pages (hash routes, bottom tab bar)

| Hash | Page | Contents |
|---|---|---|
| `#home` | Home | Big "calories left today", macros left, **next-meal suggestion (auto-loads, cached per user/day/slot/meal-count/lang in localStorage `fuel.ideas`)** with a per-ingredient nutrition table, "Record what you ate" button, quick add/weigh buttons |
| `#meals` | Meals | Date nav, meal cards (edit / delete), composer with 3 tabs: Search foods (local DB), Describe it (text or voice → Claude → auto-logged with Undo), Enter numbers |
| `#nutrition` | Nutrition | "Daily Facts" label, macro split, 7-day chart, 19 vitamins/minerals vs targets |
| `#progress` | Progress | Weekly weigh-ins + SVG chart + trend advice with "Apply ±kcal"; dated progress photos with before/after compare |
| `#me` | Profile | Profile form, computed targets, learned habits, method + sources |

## Data model (per-user, private)

Every signed-in viewer gets a private subtree; nobody else (owner included) can read it. Code uses `udoc(name)` / `ucol(name)`:

| What | Page path | `ArtifactData` (as the owner use `me`) |
|---|---|---|
| Profile | `data/users/<uid>/profile` | collection `data/users/me`, doc `profile` |
| Meals | `data/users/<uid>/log/meals/<id>` | collection `data/users/me/log/meals` |
| User-added foods | `.../log/foods/<id>` | `data/users/me/log/foods` |
| Weigh-ins | `.../log/weights/<YYYY-MM-DD>` | `data/users/me/log/weights` |
| Photos | `.../log/photos/<id>` (`assetId` → `/_blob/<assetId>`) | `data/users/me/log/photos` |

Built-in food DB (137 foods, Hebrew + English names + aliases, per-100 g values) is the published file `foods.json`, shared by everyone. Rebuild it with `tools/build_foods.py` (edit the `RAW` table there; values are typical USDA SR values written from knowledge, since the USDA download host was blocked).

**Profile doc:** `{weight, bodyFat, exercise (workouts/week), sex: "male"|"female", age, goal: "maintain"|"lose"|"gain", kcalAdjust?, adjustedAt?, updatedAt}`.

**Meal doc:**
```json
{"date":"YYYY-MM-DD","time":"HH:MM","name":"Breakfast|Lunch|Dinner|Snack (or Hebrew)",
 "items":[{"name":"...","amount":"4 ביצים (~200 ג׳)","grams":200,"foodId":"egg|null",
           "src":"Food database (USDA values) | source name","url":"https://...|null","n":{...24 keys}}],
 "note":"assumptions","by":"claude|page","createdAt":"ISO"}
```
Doc id convention: `YYYY-MM-DD_<slot><n>` from chat (e.g. `2026-11-26_dinner1`), `YYYY-MM-DD_<base36 time>` from the page.

**Nutrient keys `n` (totals for the portion, not per 100 g):** `kcal`; g: `protein carbs fat satFat fiber sugar`; mg: `sodium potassium calcium iron magnesium zinc vitC vitE b1 b2 b3 b6`; µg: `vitA` (RAE) `vitD vitK b12 folate` (DFE).

**User food doc:** `{en, he, aka[], serving, servingG, per100{24 keys}, src, url, addedAt, addedBy}`.

## Logging a meal the user tells you in chat

1. Parse the items and amounts. Assume typical portions when missing (egg 50 g, slice of yellow cheese 20 g, medium tomato 123 g, Israeli cucumber 120 g) and **state every assumption**.
2. Look each item up in `app/foods.json` first, then in the user's `data/users/me/log/foods`. **Web-search only items that are in neither.**
3. Compute `n` = per100 × grams / 100 (a small Python script is easiest). Keep `foodId` for database items.
4. Write the meal with `ArtifactData` (`set` new docs; for edits pass `if_version`). Batch the meal + any new food.
5. **Add every newly searched food** to `data/users/me/log/foods` with `per100` values and its source URL.
6. Reply in Hebrew: a table per item (amount, kcal, protein, carbs, fat), meal total, day total, calories left vs target, notable flags (e.g. sodium over 2300 mg), assumptions, and a "מקורות" list of the URLs used.

Reading the day: `ArtifactData list data/users/me/log/meals` (and `get data/users/me profile`).

## Targets (also in the page's `targets()`)

- Lean mass = weight × (1 − bodyFat/100); **BMR = 370 + 21.6 × LBM** (Katch-McArdle).
- Activity factor by workouts/week: 0→1.2, 1–2→1.375, 3–5→1.55, 6–7→1.725, 8+→1.9 (our own mapping).
- kcal = TDEE × (0.8 lose / 1.1 gain / 1) + kcalAdjust (clamped ±800, floor 1200).
- Protein 1.6 g/kg (2.0 when losing); fat 30% kcal; carbs = rest; fiber 14 g/1000 kcal; sat fat <10% kcal; sodium <2300 mg; micronutrients = US DRI by sex/age (`rda()`).
- Weight trend: linear fit over the last 35 days (needs ≥2 weigh-ins spanning ≥10 days). Bands: lose −1.0…−0.25 %/wk, gain +0.1…+0.5, maintain ±0.3. Suggested change = (aim − actual kg/wk) × 7700 / 7, clamped ±300, rounded to 50; no new suggestion within 14 days of an applied one.

Owner's profile at time of writing: 98 kg, 28% fat, 3 workouts/week, male, 31, goal lose → about 2,349 kcal and 196 g protein a day.

## How the page talks to Claude

- `sample` capability (`sampleFn.json` / `window.__sm.json`) runs on **each viewer's own claude.ai account** — no API key. Used for: Describe-it/voice parsing (`logFromText`, returns `{meal,time,items[{name,amount,grams,foodId|null,n}]}`; catalog of DB ids is in the prompt so known foods resolve locally via `resolveItems`) and next-meal ideas (`runSuggest`).
- `dictation` = built-in voice input, only in Chromium browsers / desktop app and only for viewers inside the owner's org. On iPhone it is `null`: the Record button then opens the textarea with the keyboard and a tip to use the keyboard 🎙; **Return/Send logs the meal**.
- A standalone app outside Claude was discussed and **rejected by the user**: they want the app to run through Claude accounts only.

## Language layer

English strings are the source; Hebrew comes from the `HE` dictionary (exact matches) and `PATTERNS` (templates with `{0}` placeholders) in the `// ---------- language` block. A MutationObserver translates text nodes and `placeholder/aria-label/title` attributes. Rules:
- A placeholder only matches if its value is numeric or itself translatable, unless the pattern lists it as free (3rd element, e.g. `[0]`).
- Mark user content (food names) with `data-noi18n` so it is never translated.
- When you add UI text, add its Hebrew to `HE` or `PATTERNS`. Long static prose uses `data-lang="en"` / `data-lang="he"` blocks.

## Tests (Playwright, mocked `window.claude`)

```
cd tests && node usertest.js && node ideatest.js && node voicetest.js
```
- `usertest.js`: per-user paths; signed-out state.
- `ideatest.js`: idea card with per-ingredient table (Hebrew).
- `voicetest.js`: voice → logged dinner + new food added; iPhone fallback (no dictation) with Return-to-log.
Tests serve `app/` through `page.route`, so no local server is needed. Don't run a background HTTP server.

## Known limits / open items

- Built-in food values are typical USDA values from knowledge, not the official download. Brand items (Bamba, Bissli, Emek 9%) are estimates.
- The yellow cheese 9% entry is an estimate; ask the user for the package label to correct it.
- Sharing: friends need a Claude account and must be invited (Contributor in a Team/Enterprise org; on a personal plan possibly only Editor). Viewers can't save. Photo upload for non-editors depends on the platform enabling `upload: "interact"`.
- No home-screen install from our side: the user adds it manually (Safari ⬆️ → Add to Home Screen; Chrome ⋮ → Add to Home screen).
