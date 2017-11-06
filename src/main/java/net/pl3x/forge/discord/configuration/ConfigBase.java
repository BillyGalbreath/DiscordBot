package net.pl3x.forge.discord.configuration;

public interface ConfigBase {
    void init();

    void reload();

    String file();
}
