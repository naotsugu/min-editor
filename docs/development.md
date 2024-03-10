
## Update version

Update the version numbers of the following files.

```
com.mammb.code.editor.ui.app.Version
```

Commit changes.

```
git add -A
git commit -m "Release v0.0.4"
git push origin main:main
```

By pushing a tag, the github action creates a release.

```
git tag v0.0.4
git push origin v0.0.4
```
