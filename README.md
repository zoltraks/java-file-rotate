
# File rotation utility

This program will rotate file by moving it to new incremental name.

If the target file exists and is a symbolic link, then the symbolic link itself, not the target of the link, is replaced.

This program requires Java >= 8 to run.

[Download JAR →](https://github.com/zoltraks/java-file-rotate/raw/main/download/file-rotate.7z)

[Download OpenJDK Java from OpenLogic →](https://www.openlogic.com/openjdk-downloads)

## Author

```
Filip Golewski 2022,2023
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
    -k, --keep       Keep original file name if it not exists in destination directory
    -m, --modified   Use file last modification time for DATE/TIME placeholders
    -a, --accesssed  Use file last accessed time for DATE/TIME placeholders
    -p, --pretend    Only print what is going to happen
    -V, --version    Print version information
    -h, --help       Print this screen

## Interpolation

Destination directory and filename format options can use following placeholders.

    {file}              Filename with extension
    {name}              Filename without extension
    {ext}               Extension without leading dot
    {dir}               Directory
    {i},{number}        Incremental number
    {YYYY},{YEAR}       4-digit year number
    {MM},{MONTH}        2-digit month number
    {DD},{DAY}          2-digit day number
    {hh},{HOUR}         2-digit hour
    {mm},{MINUTE}       2-digit minute
    {ss},{SECOND}       2-digit second
    {ms},{MILLISECOND}  3-digit milisecond
    {ns},{NANOSECOND}   9-digit nanosecond

## Wildcards

Wildcards are supported only in file names.

## Example

```
file-rotate log.txt
```

```
file-rotate *.log -p
```

```
file-rotate *.log -k -p -d backup
```

```
file-rotate log.txt -f {name}-{YY}{MM}{DD}-{number}.{ext}
```

```
file-rotate log.txt -f {YYYY}-{MM}-{DD}-{file}
```

```
file-rotate log.txt -d {YYYY}-{MM}/{DD} -f {name}_{hh}{mm}{ss}_{ms}.{ext}
```

```
file-rotate dir1/log.txt dir2/log.txt -d old -r
```
