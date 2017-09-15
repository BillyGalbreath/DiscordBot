package net.pl3x.forge.discord.configuration;

import net.pl3x.forge.discord.Logger;
import net.pl3x.forge.discord.scheduler.Pl3xRunnable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class ConfigWatcher implements Runnable {
    private final static int RELOAD_DELAY = 20;
    private Set<ConfigType> reloadQueue = new HashSet<>();
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
                        for (ConfigType type : ConfigType.values()) {
                            if (fileName.equals(type.file) && !reloadQueue.contains(type)) {
                                Logger.warn("Config file changed: " + fileName);
                                new ReloadConfig(type).runTaskLater(RELOAD_DELAY);
                            }
                        }
                    } else if (event.kind() == ENTRY_DELETE) {
                        for (ConfigType type : ConfigType.values()) {
                            if (fileName.equals(type.file) && !reloadQueue.contains(type)) {
                                Logger.warn("Config file deleted: " + fileName);
                                new ReloadConfig(type).runTaskLater(RELOAD_DELAY);
                            }
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

    private class ReloadConfig extends Pl3xRunnable {
        ReloadConfig(ConfigType type) {
            reloadQueue.add(type);
        }

        @Override
        public void run() {
            Iterator<ConfigType> iter = reloadQueue.iterator();
            while (iter.hasNext()) {
                ConfigType type = iter.next();
                switch (type) {
                    case CONFIG:
                        Configuration.reload();
                        break;
                    case LANG:
                        Lang.reload();
                        break;
                }
                iter.remove();
            }
        }
    }

    private enum ConfigType {
        CONFIG(Configuration.FILE_NAME),
        LANG(Lang.FILE_NAME);

        String file;

        ConfigType(String file) {
            this.file = file;
        }
    }
}
