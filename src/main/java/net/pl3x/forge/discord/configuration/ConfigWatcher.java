package net.pl3x.forge.discord.configuration;

import net.pl3x.forge.discord.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ConfigWatcher implements Runnable {
    private Path dir;

    public ConfigWatcher(Path dir) {
        this.dir = dir;
    }

    @Override
    public void run() {
        Logger.info("Config directory currently being monitored: " + dir);
        try (WatchService watcher = dir.getFileSystem().newWatchService()) {
            dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
            while (true) {
                WatchKey key = watcher.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    String fileName = event.context().toString();
                    if (event.kind() == ENTRY_CREATE || event.kind() == ENTRY_MODIFY) {
                        if (fileName.equals(Configuration.FILE_NAME)) {
                            Logger.warn("Config file changed: " + fileName);
                            Configuration.reload();
                        } else if (fileName.equals(Lang.FILE_NAME)) {
                            Logger.warn("Config file changed: " + fileName);
                            Lang.reload();
                        }
                    } else if (event.kind() == ENTRY_DELETE) {
                        if (fileName.equals(Configuration.FILE_NAME)) {
                            Logger.warn("Config file deleted: " + fileName);
                            Configuration.reload();
                        } else if (fileName.equals(Lang.FILE_NAME)) {
                            Logger.warn("Config file deleted: " + fileName);
                            Lang.reload();
                        }
                    }
                }
                if (!key.reset()) {
                    Logger.error("Could not reset watch key!");
                    key.cancel();
                    watcher.close();
                    break;
                }
            }
        } catch (InterruptedException ignore) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        Logger.warn("ConfigWatcher has stopped");
    }
}
