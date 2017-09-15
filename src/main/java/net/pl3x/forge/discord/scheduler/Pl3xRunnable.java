package net.pl3x.forge.discord.scheduler;

import net.pl3x.forge.discord.DiscordBot;

public abstract class Pl3xRunnable implements Runnable {
    private int taskId = -1;

    public int getTaskId() throws IllegalStateException {
        final int id = taskId;
        if (id == -1) {
            throw new IllegalStateException("Not scheduled yet");
        }
        return id;
    }

    public Pl3xTask runTaskLater(long delay) {
        checkState();
        return setupId(DiscordBot.getScheduler().runTaskLater(this, delay));
    }

    public Pl3xTask runTaskTimer(long delay, long period) throws IllegalArgumentException, IllegalStateException {
        checkState();
        return setupId(DiscordBot.getScheduler().runTaskTimer(this, delay, period));
    }

    public void cancel() throws IllegalStateException {
        DiscordBot.getScheduler().cancelTask(getTaskId());
    }

    private void checkState() {
        if (taskId != -1) {
            throw new IllegalStateException("Already scheduled as " + taskId);
        }
    }

    private Pl3xTask setupId(Pl3xTask task) {
        this.taskId = task.getTaskId();
        return task;
    }
}
