
## How to create an icon file

Edit the source image of the icon file `icon.png`.

Get the png2icons from [this](https://github.com/idesis-gmbh/png2icons/releases) and unzip.

Grant execution privileges.

```
chmod 755 icons
```


Generate icon files.

```
./png2icons icon.png icon -icns
./png2icons icon.png icon -ico
```

| OS      | icon file |
|---------|-----------|
| macos   | icon.icns |
| linux   | icon.png  |
| windows | icon.ico  |

