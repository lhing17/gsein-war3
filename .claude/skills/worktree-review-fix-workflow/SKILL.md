---
name: worktree-review-fix-workflow
description: Use when addressing code review feedback on a branch in a git worktree. Use when the user asks to fix review comments, handle reviewer feedback, or make post-review changes before merging.
---

# Worktree Review Fix Workflow

## Overview

Structured workflow for addressing code review feedback in an isolated worktree: read each comment, fix in order, verify with build/tests, commit, and push without disturbing the main workspace.

## When to Use

- PR receives review comments requiring fixes
- User says "fix the review issues" or "address feedback"
- Need to make post-review changes on an existing feature branch in a worktree

## Core Workflow

### 1. Read All Comments First

Before touching code, read every review comment to understand the full scope. Do not fix one at a time without seeing the rest.

### 2. Fix in Order

Address comments in logical dependency order (backend contract changes before frontend consumption, data format changes before parsing changes).

### 3. Verify After Each Logical Group

Run relevant builds/tests after completing a group of related fixes:

```bash
# Frontend changes
npm run build

# Clojure changes
lein test
lein uberjar   # if Tauri bundles the jar

# Rust/Tauri changes
cargo build
```

**Red flag:** If the build fails, stop and fix before committing.

### 4. Commit Fixes Separately

Use a dedicated commit for review fixes with a clear message:

```bash
git add -A
git commit -m "fix(scope): address review feedback - JSON output, tower name parsing

- Detail 1
- Detail 2"
git push
```

### 5. Update PR Description (if needed)

If fixes are substantial, add a comment to the PR summarizing what changed.

## Common Data Format Pitfalls

| Symptom | Root Cause | Fix |
|---------|-----------|-----|
| `eval(res.stdout)` throws on successful response | Backend emits EDN, frontend expects JS | Change backend to output JSON (cheshire), frontend to `JSON.parse` |
| Backend rejects data with "invalid format" | Frontend sends JSON to EDN parser | Either serialize as EDN on frontend, or parse JSON on backend |
| `null`/`undefined` where string expected | Backend uses keyword key (`:name`), data has string key (`"Name"`) | Normalize data map to include expected keyword before consuming |
| Some pages work, others fail after backend change | Not all frontend call sites updated | Grep for all consumers of the changed contract and update together |

## Red Flags — STOP and Verify

- Backend output format changed but only one frontend page was updated
- Review fix introduces a new dependency without checking `project.clj`
- `eval()` used to parse backend responses (security risk, fragile)
- Review fix committed without running the build step
- Force-push suggested as a "quick fix" for review changes

## Rollback

If a review fix breaks the build:

```bash
git log --oneline -3          # identify the bad commit
git revert HEAD               # safest: create anti-commit
git push
```

**Never amend commits already pushed to the PR branch** — this breaks review history.
