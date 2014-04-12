package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class SchedulingPattern {
    private boolean scheduled;
    private String cronExpression;

    public boolean isScheduled() {
        return scheduled;
    }

    public void setScheduled(boolean scheduled) {
        this.scheduled = scheduled;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }
}
