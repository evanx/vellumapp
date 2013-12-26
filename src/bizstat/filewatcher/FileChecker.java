/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.filewatcher;

import vellum.util.Args;
import java.io.File;
import vellumexp.logr.Logr;
import vellumexp.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class FileChecker {

    static Logr logger = LogrFactory.getLogger(FileChecker.class);
    long lastModified;
    File file;
    
    public FileChecker(File file) {
        this.file = file;
        if (!file.exists()) {
            throw new IllegalArgumentException(file.getAbsolutePath());
        }
        lastModified = file.lastModified();
    }

    public File getFile() {
        return file;
    }
    
    public boolean isModified() {
        if (file.lastModified() == lastModified) {
            return false;
        }
        lastModified = file.lastModified();
        return true;
    }

    @Override
    public String toString() {
        return Args.format(file.getAbsolutePath(), lastModified);
    }
    
}
