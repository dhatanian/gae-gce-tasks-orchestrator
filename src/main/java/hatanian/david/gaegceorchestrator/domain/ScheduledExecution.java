package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Unindex;

import java.util.Date;
import java.util.UUID;

@Entity(name = "ScheduledExecution")
@Unindex
public class ScheduledExecution extends ExecutionBase {
    @Id
    protected String id = UUID.randomUUID().toString();
    private SchedulingPattern schedulingPattern;

    private Date creationDate = new Date();
    private Date lastExecutionDate;

    public ScheduledExecution() {
        super();
    }

    public ScheduledExecution(ExecutionRequest request) {
        super(request);
        setSchedulingPattern(request.getSchedulingPattern());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public SchedulingPattern getSchedulingPattern() {
        return schedulingPattern;
    }

    public void setSchedulingPattern(SchedulingPattern schedulingPattern) {
        this.schedulingPattern = schedulingPattern;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastExecutionDate() {
        return lastExecutionDate;
    }

    public void setLastExecutionDate(Date lastExecutionDate) {
        this.lastExecutionDate = lastExecutionDate;
    }
}
