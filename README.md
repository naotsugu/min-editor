# Min Editor

Minimal text editor, **under development**.

* A minimal text editor, not an IDE
* Implementation with JavaFX, because there is no choice in languages with a standard multiplatform UI toolkit
* Uses piecetable data structure, so large files can be handled without memory allocation


![screenshot1](docs/images/screenshot-01.png)


## Features

* Syntax highlighting
* Line wrapping
* Multiple tab panes
* Multiple carets and selections
* Handles huge documents
* CSV/TSV layout mode
* Various editing commands (to open command pallet: `⌘-P` or `Ctrl-P`)


## Installing

Download the latest [min-editor release](https://github.com/naotsugu/min-editor/releases) and unzip.
You can use the application by launching the executable file.

### Launching app on macOS

By default, macOS allows you to open apps from the official Mac App Store only.

If you are unable to run the downloaded file, you can try to remove the quarantine attribute from the application.
This will allow you to open the application without seeing the security warning.

1. Open a Terminal window.
2. Type the following command

```shell
sudo xattr -r -d com.apple.quarantine /Applications/min-editor.app
```

## Uninstalling

Delete executable files and configuration files.

The configuration files for each platform exist in the following

| platform | location                                    |
|----------|---------------------------------------------|
| max os   | `~/Library/Application Support/min-editor/` |
| linux    | `~/.config/min-editor/`                     |
| windows  | `~/AppData/Local/min-editor/`               |


## Building

To create the distribution, run the following command.

```shell
git clone --recursive https://github.com/naotsugu/min-editor.git
cd min-editor
./gradlew clean pkg
```
The generated binary can be executed with the following command.

```shell
./modules/bootstrap/build/jpackage/min-editor/min-editor
```


The distribution archive will be created in the `/modules/bootstrap/build/distributions/` directories.


To run the application directly, execute the following command.

```shell
./gradlew run
```

If a debug run is required, execute the following command.

```shell
./gradlew run -Ddebug
```

This repository contains git submodules.
Therefore, to update a submodule, do the following.

```shell
git submodule update --remote --merge
```

To format the code, execute the following command.

```shell
./gradlew rewriteRun
```
