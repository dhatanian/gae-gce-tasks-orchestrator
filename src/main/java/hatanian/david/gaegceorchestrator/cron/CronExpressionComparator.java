package hatanian.david.gaegceorchestrator.cron;

import hatanian.david.gaegceorchestrator.domain.ScheduledExecution;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Date;

public abstract class CronExpressionComparator {
    public static void checkCronExpression(String cronExpressionString) throws ParseException {
        new CronExpression(cronExpressionString);
    }

    public static boolean mustRunTask(ScheduledExecution scheduledExecution) throws ParseException {
        Date comparisonDate = scheduledExecution.getLastExecutionDate() == null ? scheduledExecution.getCreationDate() : scheduledExecution.getLastExecutionDate();

        return new CronExpression(scheduledExecution.getSchedulingPattern().getCronExpression()).getNextValidTimeAfter(comparisonDate).before(new Date());
    }
}
