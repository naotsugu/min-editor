
## Update version

Update the version numbers of the following files.

```
com.mammb.code.editor.platform.AppVersion
```

```
com.mammb.code.editor.platform.UpdaterTest
```

Commit changes.

```shell
git add -A
git commit -m "Release v0.5.4"
git push origin main:main
```

By pushing a tag, the github action creates a release.

```shell
git tag v0.5.4
git push origin v0.5.4
```
