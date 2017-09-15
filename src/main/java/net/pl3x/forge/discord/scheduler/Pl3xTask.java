package net.pl3x.forge.discord.scheduler;

import net.pl3x.forge.discord.DiscordBot;

public class Pl3xTask implements Runnable {
    private final Runnable task;
    private final int id;
    private long nextRun;
    private long period;
    private boolean cancelled = false;

    Pl3xTask(Runnable task, int id, long period) {
        this.task = task;
        this.id = id;
        this.period = period;
    }

    @Override
    public void run() {
        task.run();
    }

    public int getTaskId() {
        return id;
    }

    long getPeriod() {
        return period;
    }

    void setPeriod(long period) {
        this.period = period;
    }

    long getNextRun() {
        return nextRun;
    }

    void setNextRun(long nextRun) {
        this.nextRun = nextRun;
    }

    public void cancel() {
        if (period > 0) {
            DiscordBot.getScheduler().cancelTask(id);
        }
    }
}
