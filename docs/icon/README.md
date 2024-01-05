

```
sips -p 614 614 512x512.png -o 614x614.png
sips -Z 512 614x614.png -o 512x512p.png
rm 614x614.png
```

```
./png2icons 512x512p.png icon -icns
./png2icons 512x512.png icon -ico
```


---


```
sips -p 1229 1229 1024x1024.png -o 1229x1229.png
sips -Z 1024 1229x1229.png -o 1024x1024p.png
rm 1229x1229.png
```

```
./png2icons 1024x1024p.png icon -icns
./png2icons 1024x1024.png icon -ico
```

