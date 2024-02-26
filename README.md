# Min Editor

Minimal text editor, **under development**.


![screenshot1](docs/images/screenshot-01.png)


## Installing

Download the latest [min-editor release](https://github.com/naotsugu/min-editor/releases) and unzip.
You can use the application by launching the executable file.

### Launching app on macOS

By default, macOS allows you to open apps from the official Mac App Store only.
If you have this still set as your default, you will be seeing the warning when you try to open an app for the first time.

The easiest way to allow an app from an unidentified developer to run on macOS is to ⌘-click the app and click Open.

1. Press ⌘ while clicking the app
2. Click the Open button from the shortcut menu.
3. Click Open again.

After that, the app will be saved as a safe app to open, and you can double-click to use it in the future as you can with any registered apps.


## Building

To create the distribution, run the following command.

```
git clone --recursive https://github.com/naotsugu/min-editor.git
cd min-editor
./gradlew clean pkg
```

The distribution archive will be created in the `/modules/bootstrap/build/distributions/` directories.


To run the application directly, execute the following command.

```console
./gradlew run
```

If a debug run is required, do the following.

```console
./gradlew run -Ddebug
```

This repository contains git submodules.
Therefore, to update a submodule, do the following.

```
git submodule update --remote --merge
```


## Usage

| Operation   | Key Combinations                                  |
|-------------|---------------------------------------------------|
| Edit        | `Ctrl+C` `Ctrl+V` `Ctrl-X` `Ctrl-A`               |
| File        | `Ctrl+O` `Ctrl+S` `Ctrl+Shift+S`                  |
| Undo Redo   | `Ctrl+Z` `Ctrl+Y` `Ctrl+Shift+Z`                  |
| Window      | `Ctrl+N` `Ctrl+W`                                 |
| Find        | `Ctrl+F`                                          |
| upper case  | `Ctrl+U`                                          |
| multi caret | `Ctrl+Click` Ctrl + click on the left line number |



