/*
 * Source https://code.google.com/p/vellum by @evanxsummers
 * 
 */
package bizstat.filewatcher;

import vellum.util.Streams;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vellum.logr.Logr;
import vellum.logr.LogrFactory;

/**
 *
 * @author evan.summers
 */
public class DirWatcherTask implements Runnable {

    Logr logger = LogrFactory.getLogger(DirWatcherTask.class);
    Map<String, FileChecker> fileCheckerMap = new HashMap();
    DirWatcherListener listener;
    File directory;
    
    public DirWatcherTask() {
    }

    public void init(File directory, String extension, DirWatcherListener listener) throws Exception {
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException(directory.getAbsolutePath());
        }
        this.directory = directory;
        for (String fileName : directory.list()) {
            if (fileName.endsWith(extension)) {
                fileCheckerMap.put(fileName, new FileChecker(new File(directory, Streams.baseName(fileName))));
            }
        }
        this.listener = listener;
        if (fileCheckerMap.isEmpty()) {
            throw new IllegalArgumentException(directory.getAbsolutePath());
        }
    }

    @Override
    public void run() {
        logger.trace("run", directory, fileCheckerMap.size());
        List<File> fileList = new ArrayList();
        for (FileChecker fileChecker : fileCheckerMap.values()) {
            logger.trace("run", fileChecker);
            if (fileChecker.isModified()) {
                fileList.add(fileChecker.getFile());
            }
        }
        if (fileList.size() > 0) {
            listener.dirChanged(this, fileList);
        }
    }
}
