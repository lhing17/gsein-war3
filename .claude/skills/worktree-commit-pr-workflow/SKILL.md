---
name: worktree-commit-pr-workflow
description: Use when finishing feature work in a git worktree and needing to commit changes, push the branch, and open a pull request. Use when the user explicitly asks to commit, push, or create a PR from isolated worktree work.
---

# Worktree Commit & PR Workflow

## Overview

Standardized workflow for landing work done in an isolated git worktree: commit, push, and open a PR without polluting the main workspace.

## When to Use

- Feature work completed in a `.worktrees/` or `worktrees/` directory
- User says "commit," "push," or "create a PR" after worktree development
- Need to move worktree changes back to the main repository via GitHub PR

## Core Workflow

### 1. Verify State

Before committing, always check:

```bash
git status          # confirm you're in the worktree
git diff --stat     # understand change scope
```

**Red flag:** If `git status` shows the parent repo instead of the worktree, `cd` to the worktree root first.

### 2. Stage and Commit

Follow conventional commits. Do NOT use `git add -A` blindly if `.env` or secrets might exist.

```bash
# Prefer explicit paths
git add <specific-files>

# Or if safe: stage all tracked changes
git add -A

git commit -m "type(scope): description"
```

### 3. Push

```bash
git push -u origin <branch-name>
```

### 4. Create PR

Use `gh pr create` with a descriptive body:

```bash
gh pr create --title "type(scope): subject" --body "..."
```

Include:
- Summary of changes
- Test verification steps
- Known risks or follow-ups

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| Committing from wrong directory (parent repo) | Always `pwd` and verify `git status` shows worktree paths |
| Using `git add -A` and accidentally committing secrets | Stage files by name; scan for `.env`, credentials, tokens |
| Empty or vague commit message | Use conventional commits: `feat(frontend): add user auth` |
| Pushing to `master`/`main` instead of feature branch | Verify current branch with `git branch` before push |
| PR body missing test/verification info | Include checklist: build steps, manual tests, screenshots |
| Forgetting to set upstream (`-u`) on first push | Always use `git push -u origin <branch>` |

## Red Flags — STOP and Verify

- `git status` shows files outside the intended worktree
- Uncommitted changes exist in the parent repo
- Commit includes binary files, lockfiles, or secrets unexpectedly
- PR target branch is `master`/`main` without explicit user approval

## Rollback

If something goes wrong before push:

```bash
git reset --soft HEAD~1    # undo last commit, keep changes staged
git reset HEAD             # unstage everything
```

If already pushed:

```bash
git revert HEAD            # safe: creates anti-commit, preserves history
git push
```

**Never force-push (`--force`) to shared branches without explicit user approval.**
