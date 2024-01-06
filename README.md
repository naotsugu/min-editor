# Min Editor

Minimal text editor, under development.


## Usage

```console
$ git clone --recursive https://github.com/naotsugu/min-editor.git
$ cd min-editor
$ ./gradlew run
```

If a debug run is required, do the following

```console
$ ./gradlew run -Ddebug
```

| Operation       | Key                                 |
|-----------------|-------------------------------------|
| Edit            | `Ctrl+C` `Ctrl+V` `Ctrl-X` `Ctrl-A` |
| File            | `Ctrl+O` `Ctrl+S` `Ctrl+Shift+S`    |
| Undo Refo       | `Ctrl+Z` `Ctrl+Y` `Ctrl+Shift+Z`    |
| Window | `Ctrl+N` `Ctrl+W` |


## Screenshots

![screenshot1](docs/images/screenshot-01.png)




## Create jpackage archive

To create an archive for jpackage distribution, run the following command.

```
./gradlew clean pkg
```

The distribution archive will be created in the `/modules/bootstrap/build/distributions/`

