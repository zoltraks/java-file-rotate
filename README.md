# File rotation utility

This program will rotate file by moving it to new incremental name.

If the target file exists and is a symbolic link, then the symbolic link itself, not the target of the link, is replaced.

This program requires Java >= 8 to run.

## Author

```
Filip Golewski 2022
```

## Usage

Set valid ``JAVA_HOME`` environment variable and path to ``java`` command in ``PATH`` variable.

```
java -jar file-rotate.jar OPTIONS... file
```

```
.\file-rotate.cmd OPTIONS... file
```

```
./file-rotate.sh OPTIONS... file
```

## Options

    -d, --directory  Destination directory for moved file (interpolated)
    -f, --format     Custom filename format (interpolated)
    -r, --relative   Use destination directory relative to file location
    -m, --modified   Use file last modification time for DATE/TIME placeholders
    -a, --accesssed  Use file last accessed time for DATE/TIME placeholders
    -V, --version    Print version information
    -h, --help       Print this screen

## Interpolation

Destination directory and filename format options can use following placeholders.

    {name}              Filename without extension
    {ext}               Extension with leading dot
    {full}              Filename with extension
    {dir}               File directory
    {i},{number}        Incremental number
    {YYYY},{YEAR}       4-digit year number
    {MM},{MONTH}        2-digit month number
    {DD},{DAY}          2-digit day number
    {hh},{HOUR}         2-digit hour
    {mm},{MINUTE}       2-digit minute
    {ss},{SECOND}       2-digit second
    {ms},{MILLISECOND}  3-digit milisecond
    {ns},{NANOSECOND}   9-digit nanosecond

## Example

```
file-rotate log.txt
```

```
file-rotate log.txt -f {name}-{YY}{MM}{DD}-{number}{ext}
```

```
file-rotate log.txt -f {YYYY}-{MM}-{DD}-{full}
```

```
    file-rotate log.txt -d {YYYY}-{MM}/{DD} -f {name}_{hh}{mm}{ss}_{ms}{ext}
```

```
file-rotate dir1/log.txt dir2/log.txt -d old -r
```
