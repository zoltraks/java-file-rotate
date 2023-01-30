package pl.alyx.utility.file_rotate;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App implements Runnable {

    final String[] args;
    private boolean optionVerbose;
    private boolean optionRelative;
    private boolean optionModified;
    private boolean optionAccessed;
    private boolean optionPretend;
    private final List<String> arguments = new ArrayList<>();
    private final List<String> excludes = new ArrayList<>();
    private final List<String> files = new ArrayList<>();
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
        Instant time = bag.time;
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
                    replacement = formatter.format(time);
                    break;
                case "MM":
                case "MONTH":
                    formatter = DateTimeFormatter.ofPattern("MM");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "DD":
                case "DAY":
                    formatter = DateTimeFormatter.ofPattern("dd");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "hh":
                case "HOUR":
                    formatter = DateTimeFormatter.ofPattern("HH");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "mm":
                case "MINUTE":
                    formatter = DateTimeFormatter.ofPattern("mm");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "ss":
                case "SECOND":
                    formatter = DateTimeFormatter.ofPattern("ss");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "ms":
                case "MILLISECOND":
                    formatter = DateTimeFormatter.ofPattern("SSS");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
                    break;
                case "ns":
                case "NANOSECOND":
                    formatter = DateTimeFormatter.ofPattern("n");
                    formatter = formatter.withZone(ZoneId.systemDefault());
                    replacement = formatter.format(time);
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
                } finally {
                    parameterName = "";
                }
            }
            switch (arg) {
                case "--help":
                case "-h":
                case "/?":
                    Help.print();
                    return false;
                case "--version":
                case "-V":
                    Version.print();
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
                case "--format":
                case "-f":
                    parameterName = "format";
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
                case "--pretend":
                case "-p":
                    this.optionPretend = true;
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
                String home = new File(search).getParent();
                if (home == null) {
                    home = Paths.get("").toAbsolutePath().toString();
                } else {
                    home = FileSystems.getDefault().getPath(home).normalize().toAbsolutePath().toString();
                }
                if (Utility.hasWildcards(home)) {
                    throw new Exception("Wildcards are not supported in directory names");
                }
                String expression = Utility.wildToExpression(new File(search).getName());
                for (final File entry : new File(home).listFiles()) {
                    if (entry.isFile()) {
                        String name = entry.getName();
                        if (name.matches(expression)) {
                            this.files.add(Paths.get(home, name).toString());
                        }
                    }
                }
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
        if (this.optionPretend) {
            System.out.println("Moving files...");
        }
        for (String file : this.files) {
            if (this.optionPretend) {
                Bag bag = createBag(Paths.get(file));
                System.out.printf("File %s will be moved as %s to %s%n",
                        bag.path.getFileName(),
                        formatName(format, bag, 0),
                        formatName(directory, bag, 0),
                        null
                );
                continue;
            }
            if (!moveFile(file)) {
                success = false;
            }
        }
        return success;
    }

    private boolean moveFile(String file) throws Exception {
        try {
            String destination;
            String directory = this.directory;

            Path path = Paths.get(file);
            Bag bag = createBag(path);

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
            Path target = Paths.get(directory, destination);
            try {
                Files.move(path, target, StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException _AtomicMoveNotSupportedException) {
                if (optionVerbose) {
                    System.out.println("ATOMIC_MOVE not supported for this move, falling back to non atomic");
                    Files.move(path, target);
                }
            }
            return true;
        } catch (IOException | SecurityException x) {
            System.out.println(x.getMessage());
            if (optionVerbose) {
                x.printStackTrace();
            }
            return false;
        } catch (Exception x) {
            throw x;
        }
    }

    private Bag createBag(Path path) throws IOException {
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
        bag.path = path;
        bag.name = name;
        bag.ext = ext;
        bag.time = Instant.now();
        bag.directory = base;

        if (false) {
        } else if (optionAccessed) {
            bag.time = bag.getAccessTime();
        } else if (optionModified) {
            bag.time = bag.getModifiedTime();
        } else {
            bag.time = Instant.now();
        }

        return bag;
    }

    private String createDirectory(String directory, Bag bag) throws Exception {
        String current = directory;
        if (current.length() == 0) {
            current = FileSystems.getDefault().getPath(".").normalize().toAbsolutePath().toString();
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

}
