# Min Editor

Minimal text editor, **under development**.


![screenshot1](docs/images/screenshot-01.png)


## Installing

Download the latest [min-editor release](https://github.com/naotsugu/min-editor/releases) and unzip.
You can use the application by launching the executable file.


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



## Usage

| Operation  | Key Combinations                                |
|------------|-------------------------------------|
| Edit       | `Ctrl+C` `Ctrl+V` `Ctrl-X` `Ctrl-A` |
| File       | `Ctrl+O` `Ctrl+S` `Ctrl+Shift+S`    |
| Undo Redo  | `Ctrl+Z` `Ctrl+Y` `Ctrl+Shift+Z`    |
| Window     | `Ctrl+N` `Ctrl+W`                   |
| Find | `Ctrl+F`                   |

