package hatanian.david.gaegceorchestrator.cron;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.ScheduledExecution;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CronServlet extends HttpServlet {

    private static final long serialVersionUID = -3999085760254679834L;
    private static final Logger logger = Logger
            .getLogger(CronServlet.class.getName());
    private static final int MAX_CRON_JOBS = 1000;
    private StorageManager<ScheduledExecution> scheduledExecutionStorageManager = new StorageManager<>(ScheduledExecution.class);
    private StorageManager<Execution> executionStorageManager = new StorageManager<>(Execution.class);

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        QueryResultIterator<ScheduledExecution> scheduledExecutionQueryResultIterator = scheduledExecutionStorageManager.list(null, MAX_CRON_JOBS, null, null, null);
        while (scheduledExecutionQueryResultIterator.hasNext()) {
            ScheduledExecution scheduledExecution = scheduledExecutionQueryResultIterator.next();

            try {
                logger.info("Checking if an execution must be started for execution " + scheduledExecution.getId());
                logger.info("CRON expression is : " + scheduledExecution.getSchedulingPattern().getCronExpression());
                logger.info("Last execution time is : " + scheduledExecution.getLastExecutionDate());
                logger.info("Creation time is : " + scheduledExecution.getCreationDate());

                if (CronExpressionComparator.mustRunTask(scheduledExecution)) {
                    Execution execution = createExecutionFromScheduledExecution(scheduledExecution);
                    enqueueExecution(execution);
                    scheduledExecution.setLastExecutionDate(new Date());
                    scheduledExecutionStorageManager.save(scheduledExecution);
                    logger.info("Started a new execution from scheduled execution " + scheduledExecution.getId() + ", expression was : " + scheduledExecution.getSchedulingPattern().getCronExpression());
                } else {
                    logger.info("No execution this time");
                }
            } catch (ParseException e) {
                logger.log(Level.SEVERE, "Unable to process CRON expression for scheduled execution, " + scheduledExecution.getId() + ", expression was : " + scheduledExecution.getSchedulingPattern().getCronExpression(), e);
            } catch (Throwable t) {
                logger.log(Level.SEVERE, "Unable to start execution for scheduled execution, " + scheduledExecution.getId() + ", expression was : " + scheduledExecution.getSchedulingPattern().getCronExpression(), t);
            }
        }
    }

    private void enqueueExecution(Execution execution) {
        executionStorageManager.save(execution);
        QueueFactory.getDefaultQueue().add(TaskOptions.Builder.withUrl("/admin/tasks/startbackend").method(TaskOptions.Method.GET).param("executionId", execution.getId()));
    }

    private Execution createExecutionFromScheduledExecution(ScheduledExecution scheduledExecution) {
        Execution execution = new Execution();
        execution.setGceConfiguration(scheduledExecution.getGceConfiguration());
        execution.setUserScript(scheduledExecution.getUserScript());
        execution.setResultBucket(scheduledExecution.getResultBucket());
        execution.setRequester(scheduledExecution.getRequester());
        execution.setProjectId(scheduledExecution.getProjectId());
        return execution;
    }
}
