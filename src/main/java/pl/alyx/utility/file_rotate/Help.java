package pl.alyx.utility.file_rotate;

public class Help {

    public static void print() {
        System.out.println("File rotation utility");
        System.out.println("");
        System.out.println("This program will rotate file by moving it to new incremental name.");
        System.out.println("");
        System.out.println("If the target file exists and is a symbolic link, then the symbolic link itself, not the target of the link, is replaced.");
        System.out.println("");
        System.out.println("This program requires Java >= 8 to run.");
        System.out.println("");
        System.out.println("AUTHOR");
        System.out.println("");
        System.out.println("    Filip Golewski 2022 2023");
        System.out.println("");
        System.out.println("USAGE");
        System.out.println("");
        System.out.println("Set valid JAVA_HOME environment variable and path to java command in PATH`` variable.");
        System.out.println("    java -jar file-rotate.jar OPTIONS... file");
        System.out.println("");
        System.out.println("    .\\file-rotate.cmd OPTIONS... file");
        System.out.println("");
        System.out.println("    ./file-rotate.sh OPTIONS... file");
        System.out.println("");
        System.out.println("OPTIONS");
        System.out.println("");
        System.out.println("    -d, --directory  Destination directory for moved file (interpolated)");
        System.out.println("    -f, --format     Custom filename format (interpolated)");
        System.out.println("    -r, --relative   Use destination directory relative to file location");
        System.out.println("    -k, --keep       Keep original file name if not exists in destination directory");
        System.out.println("    -m, --modified   Use file last modification time for DATE/TIME placeholders");
        System.out.println("    -a, --accesssed  Use file last accessed time for DATE/TIME placeholders");
        System.out.println("    -p, --pretend    Only print what is going to happen");
        System.out.println("    -V, --version    Print version information");
        System.out.println("    -h, --help       Print this screen");
        System.out.println("");
        System.out.println("INTERPOLATION");
        System.out.println("");
        System.out.println("Destination directory and filename format options can use following placeholders.");
        System.out.println("");
        System.out.println("    {name}              Filename without extension");
        System.out.println("    {ext}               Extension with leading dot");
        System.out.println("    {full}              Filename with extension");
        System.out.println("    {dir}               File directory");
        System.out.println("    {i},{number}        Incremental number");
        System.out.println("    {YYYY},{YEAR}       4-digit year number");
        System.out.println("    {MM},{MONTH}        2-digit month number");
        System.out.println("    {DD},{DAY}          2-digit day number");
        System.out.println("    {hh},{HOUR}         2-digit hour");
        System.out.println("    {mm},{MINUTE}       2-digit minute");
        System.out.println("    {ss},{SECOND}       2-digit second");
        System.out.println("    {ms},{MILLISECOND}  3-digit milisecond");
        System.out.println("    {ns},{NANOSECOND}   9-digit nanosecond");
        System.out.println("");
        System.out.println("EXAMPLE");
        System.out.println("");
        System.out.println("    file-rotate log.txt");
        System.out.println("");
        System.out.println("    file-rotate log.txt -f {name}-{YY}{MM}{DD}-{number}{ext}");
        System.out.println("    file-rotate log.txt -f {YYYY}-{MM}-{DD}-{full}");
        System.out.println("    file-rotate log.txt -d {YYYY}-{MM}/{DD} -f {name}_{hh}{mm}{ss}_{ms}{ext}");
        System.out.println("");
        System.out.println("    file-rotate dir1/log.txt dir2/log.txt -d old -r");
        System.out.println("");
    }

}
