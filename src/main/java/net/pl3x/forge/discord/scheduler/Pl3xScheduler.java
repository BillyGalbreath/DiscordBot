package net.pl3x.forge.discord.scheduler;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Pl3xScheduler {
    private final AtomicInteger ids = new AtomicInteger(1);
    private final ConcurrentHashMap<Integer, Pl3xTask> tasks = new ConcurrentHashMap<>();
    private int currentTick = -1;

    private int nextId() {
        return ids.incrementAndGet();
    }

    public Pl3xTask runTaskLater(Runnable runnable, long delay) {
        if (delay < 0L) {
            delay = 0;
        }
        Pl3xTask task = new Pl3xTask(runnable, nextId(), -1L);
        task.setNextRun(currentTick + delay);
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public Pl3xTask runTaskTimer(Runnable runnable, long delay, long period) {
        if (delay < 0L) {
            delay = 0;
        }
        if (period == 0L) {
            period = 1L;
        } else if (period < -1L) {
            period = -1L;
        }
        Pl3xTask task = new Pl3xTask(runnable, nextId(), period);
        task.setNextRun(currentTick + delay);
        tasks.put(task.getTaskId(), task);
        return task;
    }

    public void cancelTask(int taskId) {
        Pl3xTask task = tasks.get(taskId);
        if (task != null) {
            task.setPeriod(-1);
            task.cancel();
        }
        tasks.remove(taskId);
    }

    public void tick() {
        currentTick++;

        Set<Integer> toRemove = new HashSet<>();
        tasks.forEach((id, task) -> {
            if (id > 0 && task.getNextRun() <= currentTick) {
                task.run();
                if (task.getPeriod() > 0) {
                    task.setNextRun(currentTick + task.getPeriod());
                } else {
                    toRemove.add(id);
                }
            }
        });

        toRemove.forEach(tasks::remove);
    }
}
