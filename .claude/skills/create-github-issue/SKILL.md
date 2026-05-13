---
name: create-github-issue
description: Use when the user wants to create a GitHub issue for this repository. Use when the user says "create issue", "提issue", "创建issue", "开个issue", "写个issue", or describes a bug/feature/enhancement/task and asks to file it as a GitHub issue. Also use when the user provides a description and explicitly asks to create an issue from it.
---

# Create GitHub Issue

## Overview

Standardized workflow for creating well-structured GitHub issues with proper context gathering from the codebase and repository metadata.

## Core Workflow

### 1. Parse Requirements

Understand what the user wants the issue to cover:
- **Issue type**: bug, feature, enhancement, design, refactor, docs, etc.
- **Subject**: what is the core problem or request?
- **Context**: does the user provide a full description, or just a brief hint?
- **Scope**: is this a single task or a multi-part epic?

If the user's description is brief or ambiguous, ask clarifying questions before proceeding.

### 2. Gather Repository Context

Before drafting, collect relevant context from the codebase:

```bash
# Verify GitHub repo info
gh repo view --json url,owner,name
```

If the issue is related to a specific component, explore the codebase:
- Search relevant source files with `Glob` / `Grep`
- Read key files with `Read`
- Check recent commits with `git log --oneline -10`

Use this information to enrich the issue description (tech stack, current state, relevant paths).

### 3. Draft the Issue

Structure the issue body in Chinese (matching the project's language) with the following sections:

```markdown
## 问题描述

[清晰描述问题或需求。如果是 bug：现象、复现步骤、期望 vs 实际结果。如果是 feature：动机和目标。]

## 项目背景 / 现状

[从代码库中提取的相关上下文：技术栈、涉及模块、当前实现状态等]

## 目标

[具体要实现什么，验收标准]

## 建议方案 / 实现思路

[可选。如果有明确方向，列出关键步骤或设计方向]

## 关键任务清单

- [ ] 任务 1
- [ ] 任务 2
- [ ] 任务 3

## 非目标 / 约束

[明确排除的范围，避免需求蔓延]

## 参考信息

[相关的文档链接、技能、设计稿、讨论记录等]
```

**Title format**: `[类型] 简短描述`
- Types: `[Bug]`, `[Feature]`, `[UI/UX]`, `[Refactor]`, `[Docs]`, `[Performance]`, `[Design]`

**Labels suggestion**: Add appropriate labels like `enhancement`, `bug`, `ui/ux`, `design`, `refactor`, `good first issue`, etc.

### 4. Create the Issue

Use `gh issue create` with a heredoc for the body:

```bash
gh issue create --title "[类型] 标题" --body "$(cat <<'EOF'
## 问题描述
...
EOF
)"
```

If the issue has many sections and the command becomes unwieldy, write the body to a temp file first and use `--body-file`.

### 5. Report Back

After creation, report to the user:
- Issue URL
- Issue number
- Summary of what was covered in the issue

## Examples

**Example 1: Feature request**
User: "前端界面太丑，需要用 frontend-design 技能设计一套美观时尚的界面"
→ Create an `[UI/UX]` issue with design goals, suggested directions, task checklist, and constraints.

**Example 2: Bug report**
User: "图片切分工具在处理大文件时会 OOM"
→ Create a `[Bug]` issue with reproduction steps, expected vs actual behavior, and relevant code paths.

**Example 3: Task conversion**
User: "把刚才讨论的方案提一个 issue 跟踪"
→ Create a `[Feature]` issue summarizing the discussed plan with a task checklist.

## Notes
If the user provides a screenshot, include it in the issue body.