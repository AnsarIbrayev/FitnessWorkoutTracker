# Release Checklist

1. App builds successfully in Release configuration
2. VersionCode incremented
3. VersionName updated
4. No debug logs in release build (or logs are minimized)
5. Secrets removed from repo (no API keys in code)
6. Firebase rules reviewed (user-scoped access where needed)
7. App handles offline mode gracefully
8. Empty state UI works (no data)
9. Error state UI works (shows message + retry)
10. Retry works for transient errors
11. Crash-free basic manual flow verified
12. Unit tests: at least 10 passing
13. README updated with setup instructions
14. Docs updated: architecture, QA log, performance note, release notes
15. Signed build generated (.aab preferred)
16. Signed build evidence added (screenshots or GitHub release)
