---
name: worktree-merge-cleanup-workflow
description: Use when a pull request from a git worktree branch is approved and ready to merge. Use when the user asks to merge a PR, land a feature branch, or clean up after worktree development is complete.
---

# Worktree Merge & Cleanup Workflow

## Overview

Standardized workflow for merging a completed PR and cleaning up the associated worktree, local branch, and remote branch.

## When to Use

- PR is approved and ready to merge
- User says "merge this PR" or "land the branch"
- Feature work is complete and the worktree/branch should be cleaned up

## Core Workflow

### 1. Merge the PR

Use `gh pr merge` from the **main repository directory** (not the worktree):

```bash
cd /path/to/main-repo
gh pr merge <number> --squash --delete-branch
```

If the command fails because the worktree still has the branch checked out:

```bash
gh pr merge <number> --squash
```

Then clean up manually (see steps 2–4).

### 2. Delete the Remote Branch

If `--delete-branch` succeeded, skip this step. Otherwise:

```bash
git push origin --delete <branch-name>
```

### 3. Remove the Worktree

```bash
git worktree remove .worktrees/<name>
```

If the worktree has uncommitted changes, Git will refuse. Options:
- Commit or stash the changes first
- Force removal: `git worktree remove --force .worktrees/<name>` (destructive)

### 4. Delete the Local Branch

```bash
git branch -D <branch-name>
```

## Common Mistakes

| Mistake | Fix |
|---------|-----|
| Running `gh pr merge` from inside the worktree | `cd` to the main repo directory first |
| `--delete-branch` fails because branch is checked out in worktree | Merge first, then manually delete remote branch |
| `git worktree remove` fails due to uncommitted changes | Commit, stash, or use `--force` if changes are discardable |
| Deleting local branch before removing worktree | Git refuses; remove worktree first |
| Forgetting to delete remote branch | Run `git push origin --delete <branch>` separately |

## Red Flags — STOP and Verify

- PR has not been reviewed or CI is failing
- Worktree contains uncommitted changes you did not intend to lose
- `--force` used on `git worktree remove` without checking contents
- Merging into `master`/`main` without confirming the target branch

## Rollback

If a bad merge lands in `master`/`main`:

```bash
# Revert the merge commit (creates anti-commit, preserves history)
git revert -m 1 <merge-commit-hash>
git push origin master
```

**Never force-push to `master`/`main` without explicit user approval.**
