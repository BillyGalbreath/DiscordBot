package net.pl3x.forge.discord.util;

import net.pl3x.forge.discord.DiscordBot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static void saveResource(String name, File file) {
        try (InputStream in = DiscordBot.class.getClassLoader().getResourceAsStream(name);
             OutputStream out = new FileOutputStream(file)) {
            if (in == null) {
                return;
            }
            int readBytes;
            byte[] buffer = new byte[4096];
            while ((readBytes = in.read(buffer)) > 0) {
                out.write(buffer, 0, readBytes);
            }
            int r;
            byte[] buf = new byte[2048];
            while ((r = in.read(buf)) != -1) {
                out.write(buf, 0, r);
            }
        } catch (Exception ignore) {
        }
    }
}
