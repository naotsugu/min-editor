
## How to create an icon file

Edit the source image of the icon file `min-editor.fig`.

Export png images.

Get the icons from [this](https://github.com/naotsugu/icons) and unzip.

Grant execution privileges.

```shell
chmod 755 icons
```

Generate icon files.

```shell
./icons ./
```

| OS      | icon file |
|---------|-----------|
| macos   | icon.icns |
| linux   | icon.png  |
| windows | icon.ico  |

```shell
cp icon.png ../../modules/ui-fx/src/main/resources/icon.png
```

