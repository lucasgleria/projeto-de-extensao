package utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // Importando pacotes relacionados a concorrência de uma vez
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TaskExecutorManager {
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduler; // Gerenciador de tempo para monitoramento de tarefas
    private final Map<String, TaskStatus> taskStatusMap;
    private final Map<String, Future<?>> taskFutureMap;

    public enum TaskStatus {
        PENDING,    // Tarefa aguardando execução
        RUNNING,    // Tarefa em execução
        COMPLETED,  // Tarefa concluída
        FAILED,     // Tarefa falhou
        TIMEOUT     // Tarefa expirou o tempo limite
    }

    public TaskExecutorManager(int numThreads) {
        this.executorService = Executors.newFixedThreadPool(numThreads);
        this.scheduler = Executors.newScheduledThreadPool(1); // Reutilizar um único scheduler
        this.taskStatusMap = new ConcurrentHashMap<>();
        this.taskFutureMap = new ConcurrentHashMap<>();
    }

    public void execute(Runnable task) {
        executorService.execute(task);
    }

    public void shutdown() {
        executorService.shutdown();
        scheduler.shutdown(); // Fechar o scheduler também
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("O ExecutorService não terminou.");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void submitTask(String taskId, Runnable task, long timeout) {
        taskStatusMap.put(taskId, TaskStatus.PENDING);
        Future<?> future = executorService.submit(() -> {
            try {
                taskStatusMap.put(taskId, TaskStatus.RUNNING);
                task.run();
                taskStatusMap.put(taskId, TaskStatus.COMPLETED);
            } catch (Exception e) {
                taskStatusMap.put(taskId, TaskStatus.FAILED);
                e.printStackTrace();
            }
        });

        taskFutureMap.put(taskId, future);
        scheduleTimeoutCheck(taskId, future, timeout);
    }

    private void scheduleTimeoutCheck(String taskId, Future<?> future, long timeout) {
        scheduler.schedule(() -> {
            if (!future.isDone()) {
                taskStatusMap.put(taskId, TaskStatus.TIMEOUT);
                future.cancel(true); // Interromper a tarefa
            }
        }, timeout, TimeUnit.MILLISECONDS);
    }

    public TaskStatus getTaskStatus(String taskId) {
        return taskStatusMap.getOrDefault(taskId, TaskStatus.PENDING);
    }
}
