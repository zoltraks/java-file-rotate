package pl.alyx.utility.file_rotate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;

public class Bag {

    public Instant time;

    public Path path;

    public String name;

    public String ext;

    public String directory;

    private Instant modifiedTime;

    public Instant getModifiedTime() throws IOException {
        if (modifiedTime == null && name != null) {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            modifiedTime = attr.lastModifiedTime().toInstant();
        }
        return modifiedTime;
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    private Instant accessTime;

    public Instant getAccessTime() throws IOException {
        if (accessTime == null && name != null) {
            BasicFileAttributes attr = Files.readAttributes(path, BasicFileAttributes.class);
            accessTime = attr.lastAccessTime().toInstant();
        }
        return accessTime;
    }

    public void setAccessTime(Instant accessTime) {
        this.accessTime = accessTime;
    }

}
