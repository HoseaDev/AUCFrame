import org.gradle.api.Task;

public class TaskInfo {
    Task task;
    long exeDuration;
    TaskInfo(Task task, long exeDuration) {
        this.task = task;
        this.exeDuration = exeDuration;
    }
}
