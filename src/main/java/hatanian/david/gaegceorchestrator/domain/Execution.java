package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.*;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendService;

import java.util.Date;
import java.util.UUID;

//TODO add parameters
//TODO add custom timeout, machine type, zone etc
//TODO add password for the backend result code --> HASH
@Entity
@Unindex
public class Execution {
    @Id
    private String id = UUID.randomUUID().toString();
    private Date startDate = new Date();
    private Date endDate;
    private String requester;

    private ExecutionBackendResult backendResult;

    @Index
    private State state = State.STARTED;
    @Index
    private Boolean done = false;
    @Index
    private String diskAndInstanceName;

    public Execution() {
        super();
    }

    public ExecutionBackendResult getBackendResult() {
        return backendResult;
    }

    public void setBackendResult(ExecutionBackendResult backendResult) {
        this.backendResult = backendResult;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Boolean getDone() {
        return done;
    }

    public void setDone(Boolean done) {
        this.done = done;
    }

    public String getDiskAndInstanceName() {
        return diskAndInstanceName;
    }

    public void setDiskAndInstanceName(String diskAndInstanceName) {
        this.diskAndInstanceName = diskAndInstanceName;
    }

    public String buildDiskAndInstanceName() {
        // Weirdly, there is an error in the jvm if we put a name that is too
        // long, so we'll only use the first part of the uuid
        diskAndInstanceName = GCEBackendService.NAME_PREFIX + id.substring(0, id.indexOf("-"));
        return diskAndInstanceName;
    }

    @OnSave
    public void configureDiskAndInstanceName() {
        setDiskAndInstanceName(buildDiskAndInstanceName());
    }
}
