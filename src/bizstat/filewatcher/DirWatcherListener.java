/*
 * Source https://github.com/evanx by @evanxsummers
 * 
 */
package bizstat.filewatcher;

import java.io.File;
import java.util.Collection;

/**
 *
 * @author evan.summers
 */
public interface DirWatcherListener {
    public void dirChanged(DirWatcherTask watcher, Collection<File> files);
    
}
