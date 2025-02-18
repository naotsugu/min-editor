
## Update version

Update the version numbers of the following files.

```
com.mammb.code.editor.ui.app.Version
```

Commit changes.

```shell
git add -A
git commit -m "Release v0.0.11"
git push origin main:main
```

By pushing a tag, the github action creates a release.

```shell
git tag v0.0.11
git push origin v0.0.11
```
