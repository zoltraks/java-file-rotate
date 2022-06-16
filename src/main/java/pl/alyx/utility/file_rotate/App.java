package pl.alyx.utility.file_rotate;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class App implements Runnable {
    final String[] args;
    private boolean optionVerbose;
    private boolean optionRelative;
    private boolean optionModified;
    private boolean optionAccessed;
    private List<String> arguments = new ArrayList<>();
    private List<String> excludes = new ArrayList<>();
    private List<String> files = new ArrayList<>();
    private String format = "{name}.{i}{ext}";
    private String directory = "";
    private boolean parameterFormat;

    public App(String[] args) {
        this.args = args;
    }

    @Override
    public void run() {
        try {
            if (!parseArgs()) {
                return;
            }
            if (!gatherFiles()) {
                return;
            }
            if (!moveFiles()) {
                return;
            }
        } catch (Exception x) {
            System.out.println(x.getMessage());
            if (optionVerbose) {
                x.printStackTrace();
            }
        }
    }

    private String formatName(String format, Bag bag, int number) throws Exception {
        Pattern p = Pattern.compile("\\{([^\\}]*)\\}");
        Matcher m = p.matcher(format);
        StringBuffer sb = new StringBuffer();
        DateTimeFormatter formatter;
        while (m.find()) {
            String replacement = null;
            String group = m.group(1);
            switch (group) {
                case "name":
                    replacement = bag.name;
                    break;
                case "ext":
                    replacement = bag.ext;
                    break;
                case "full":
                    replacement = bag.name + bag.ext;
                    break;
                case "dir":
                    replacement = bag.directory;
                    break;
                case "number":
                case "i":
                    replacement = "" + number;
                    break;
                case "YYYY":
                case "YEAR":
                    formatter = DateTimeFormatter.ofPattern("yyyy");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "MM":
                case "MONTH":
                    formatter = DateTimeFormatter.ofPattern("MM");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "DD":
                case "DAY":
                    formatter = DateTimeFormatter.ofPattern("dd");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "hh":
                case "HOUR":
                    formatter = DateTimeFormatter.ofPattern("HH");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "mm":
                case "MINUTE":
                    formatter = DateTimeFormatter.ofPattern("mm");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "ss":
                case "SECOND":
                    formatter = DateTimeFormatter.ofPattern("ss");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "ms":
                case "MILLISECOND":
                    formatter = DateTimeFormatter.ofPattern("SSS");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                case "ns":
                case "NANOSECOND":
                    formatter = DateTimeFormatter.ofPattern("n");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(bag.instant);
                    break;
                default:
                    throw new Exception(String.format("Unknown placeholder {%s}", group));
            }
            if (replacement != null) {
                m.appendReplacement(sb, replacement);
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    private boolean parseArgs() throws Exception {
        String parameterName = "";
        for (String arg : args) {
            if (parameterName.length() > 0) {
                try {
                    switch (parameterName) {
/*
                        case "exclude":
                            this.excludes.add(arg);
                            continue;
*/
                        case "directory":
                            if (this.directory.length() > 0) {
                                throw new Exception(String.format("Parameter %s specified more than once", parameterName));
                            }
                            this.directory = arg;
                            continue;
                        case "format":
                            if (this.parameterFormat) {
                                throw new Exception(String.format("Parameter %s specified more than once", parameterName));
                            }
                            this.format = arg;
                            this.parameterFormat = true;
                            continue;
                        default:
                            throw new Exception(String.format("Argument handler for %s not present", parameterName));
                    }
                }
                finally {
                    parameterName = "";
                }
            }
            switch (arg) {
                case "--help":
                case "-h":
                case "/?":
                    printHelp();
                    return false;
                case "--version":
                case "-V":
                    printVersion();
                    return false;
/*
                case "--exclude":
                case "-x":
                    parameterName = "exclude";
                    continue;
*/
                case "--directory":
                case "-d":
                    parameterName = "directory";
                    continue;
                case "--verbose":
                case "-v":
                    this.optionVerbose = true;
                    continue;
                case "--relative":
                case "-r":
                    this.optionRelative = true;
                    continue;
                case "--modified":
                case "-m":
                    this.optionModified = true;
                    continue;
                case "--accessed":
                case "-a":
                    this.optionAccessed = true;
                    continue;
                default:
                    if (arg.startsWith("-")) {
                        throw new Exception("Unrecognized option " + arg);
                    }
                    this.arguments.add(arg);
                    continue;
            }
        }
        if (parameterName.length() > 0) {
            throw new Exception(String.format("Value not present for parameter %s", parameterName));
        }
        if (optionModified && optionAccessed) {
            throw new Exception("Either last modified (-m) or last accessed (-a) options can be used at once");
        }
        return true;
    }

    private boolean gatherFiles() throws Exception {
        if (this.arguments.isEmpty()) {
            System.out.println("One or more files must be provided.");
            System.out.println("Use --help option to print help screen.");
            throw new Exception("No arguments.");
        }
        if (this.optionVerbose) {
            System.out.println("Gathering files...");
        }
        for (String search : this.arguments) {
            if (!Utility.hasWildcards(search)) {
                String absolutePath = FileSystems.getDefault().getPath(search).normalize().toAbsolutePath().toString();
                Path path = Paths.get(absolutePath);
                if (Files.exists(path) && Files.isRegularFile(path)) {
                    if (!this.files.contains(absolutePath)) {
                        this.files.add(absolutePath);
                    }
                    continue;
                }
            }
            if (Utility.hasWildcards(search)) {
                throw new Exception("Wildcards are not supported");
            }
        }
        if (this.optionVerbose) {
            for (String file : this.files) {
                System.out.println(file);
            }
        }
        return true;
    }

    private boolean moveFiles() throws Exception {
        boolean success = true;
        for (String file : this.files) {
            if (!moveFile(file)) {
                success = false;
            }
        }
        return success;
    }

    private boolean moveFile(String file) throws Exception {
        try {
            String destination = "";
            String directory = this.directory;

            Path path = Paths.get(file);
            String base = path.getParent().toString();
            String full = path.getFileName().toString();
            String name = full;
            String ext = "";
            if (full.contains(".")) {
                int p = full.lastIndexOf(".");
                name = full.substring(0, p);
                ext = full.substring(p);
            }

            Bag bag = new Bag();
            bag.name = name;
            bag.ext = ext;
            bag.instant = Instant.now();
            bag.directory = base;

            if (optionModified) {
                BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
                bag.instant = attr.lastModifiedTime().toInstant();
            }

            directory = createDirectory(directory, bag);

            boolean constant = !this.format.contains("{i}") && !this.format.contains("{number}");
            if (constant) {
                destination = formatName(this.format, bag, 0);
                Path check = Paths.get(directory, destination);
                if (Files.exists(check)) {
                    System.out.println(String.format("Destination file exists %s", check.toString()));
                    return false;
                }
            } else {
                int n = 1;
                while (true) {
                    destination = formatName(this.format, bag, n);
                    if (!Files.exists(Paths.get(directory, destination))) {
                        break;
                    }
                    if (this.optionVerbose) {
                        System.out.println(String.format("File exists %s", Paths.get(directory, destination)));
                    }
                    n++;
                }
            }
            if (this.optionVerbose) {
                System.out.println(String.format("Moving file %s to %s", file, Paths.get(directory, destination)));
            }
            Path source = Paths.get(file);
            Path target = Paths.get(directory, destination);
            try {
                Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException _AtomicMoveNotSupportedException) {
                if (optionVerbose) {
                    System.out.println("ATOMIC_MOVE not supported for this move, falling back to non atomic");
                    Files.move(source, target);
                }
            }
            return true;
        } catch (IOException|SecurityException x) {
            System.out.println(x.getMessage());
            if (optionVerbose) {
                x.printStackTrace();
            }
            return false;
        } catch (Exception x) {
            throw x;
        }
    }

    private String createDirectory(String directory, Bag bag) throws Exception {
        String current = directory;
        if (current.length() == 0) {
            current = FileSystems.getDefault().getPath(".").normalize().toAbsolutePath().toString();
            if (this.optionVerbose) {
                System.out.println(String.format("Using current directory %s", current));
            }
        } else {
            if (current.contains("{")) {
                current = formatName(current, bag, 0);
            }
            String absolutePath = "";
            if (Paths.get(current).isAbsolute()) {
                absolutePath = current;
            } else if (this.optionRelative) {
                absolutePath = Paths.get(bag.directory, current).normalize().toAbsolutePath().toString();
            } else {
                absolutePath = FileSystems.getDefault().getPath(current).normalize().toAbsolutePath().toString();
            }
            Path path = Paths.get(absolutePath);
            if (Files.exists(path)) {
                if (!Files.isDirectory(path)) {
                    throw new Exception(String.format("Path exists and is not directory %s", absolutePath));
                }
            } else {
                Files.createDirectories(path);
                if (optionVerbose) {
                    System.out.println(String.format("Created directory %s", absolutePath));
                }
            }
            current = absolutePath;
        }
        return current;
    }

    private void printVersion() {
        System.out.println("Version: " + Global.PROGRAM_VERSION);
    }

    private void printHelp() {
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
        System.out.println("    Filip Golewski 2022");
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
        System.out.println("    -m, --modified   Use file last modification time for DATE/TIME placeholders");
        System.out.println("    -a, --accesssed  Use file last accessed time for DATE/TIME placeholders");
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
