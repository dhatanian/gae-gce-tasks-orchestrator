package hatanian.david.gaegceorchestrator.cron;

import hatanian.david.gaegceorchestrator.domain.ScheduledExecution;
import hatanian.david.gaegceorchestrator.domain.SchedulingPattern;
import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CronExpressionComparatorTest {

    public static final String EVERY_HOUR_CRON_EXPRESSION = "0 0 * * * ?";

    @Test(expected = ParseException.class)
    public void testCheckCronExpressionFailure() throws Exception {
        CronExpressionComparator.checkCronExpression("notvalid");
    }

    @Test()
    public void testCheckCronExpressionSuccess() throws Exception {
        CronExpressionComparator.checkCronExpression(EVERY_HOUR_CRON_EXPRESSION);
    }

    @Test
    public void testMustRunTask() throws Exception {
        Calendar twoHoursAgoCalendar = GregorianCalendar.getInstance();
        twoHoursAgoCalendar.add(Calendar.HOUR, -2);
        Date twoHoursAgoDate = twoHoursAgoCalendar.getTime();
        Date nowDate = new Date();

        assertFalse(CronExpressionComparator.mustRunTask(createScheduledExecution(nowDate, null, EVERY_HOUR_CRON_EXPRESSION)));
        assertFalse(CronExpressionComparator.mustRunTask(createScheduledExecution(nowDate, nowDate, EVERY_HOUR_CRON_EXPRESSION)));
        assertTrue(CronExpressionComparator.mustRunTask(createScheduledExecution(twoHoursAgoDate, null, EVERY_HOUR_CRON_EXPRESSION)));
        assertTrue(CronExpressionComparator.mustRunTask(createScheduledExecution(nowDate, twoHoursAgoDate, EVERY_HOUR_CRON_EXPRESSION)));
    }

    private ScheduledExecution createScheduledExecution(Date creationDate, Date lastExecutionDate, String cronExpression) {
        ScheduledExecution se = new ScheduledExecution();
        se.setSchedulingPattern(new SchedulingPattern());
        se.getSchedulingPattern().setCronExpression(cronExpression);
        se.setLastExecutionDate(lastExecutionDate);
        se.setCreationDate(creationDate);
        return se;
    }
}