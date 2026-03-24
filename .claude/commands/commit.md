Run the following git commands and follow the steps below:

1. Run `git status` to check changed files
2. Run `git diff` to review the changes
3. Run `git log --oneline -5` to check recent commit message style
4. Stage all changed files with `git add -A`
5. Write a commit message based on the changes:
   - Format: `{type}: {description}` (e.g. `feat: add user api`, `fix: resolve npe in service`)
   - Types: feat / fix / refactor / docs / chore / test
   - Use Korean or English consistently with recent commits
6. Commit using:
```
git commit -m "$(cat <<'EOF'
{commit message}

Co-Authored-By: Claude Sonnet 4.6 <noreply@anthropic.com>
EOF
)"
```
7. Show the final `git status` and `git log --oneline -3` to confirm