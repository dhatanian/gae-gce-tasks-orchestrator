package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Unindex;

@Entity
@Unindex
public class ExecutionRequest {
    private String resultBucket;
    private SchedulingPattern schedulingPattern;
    private UserScript userScript;
    private GCEConfiguration gceConfiguration;
    private String projectId;

    public ExecutionRequest() {
        super();
    }

    public String getResultBucket() {
        return resultBucket;
    }

    public void setResultBucket(String resultBucket) {
        this.resultBucket = resultBucket;
    }

    public UserScript getUserScript() {
        return userScript;
    }

    public void setUserScript(UserScript userScript) {
        this.userScript = userScript;
    }

    public GCEConfiguration getGceConfiguration() {
        return gceConfiguration;
    }

    public void setGceConfiguration(GCEConfiguration gceConfiguration) {
        this.gceConfiguration = gceConfiguration;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public SchedulingPattern getSchedulingPattern() {
        return schedulingPattern;
    }

    public void setSchedulingPattern(SchedulingPattern schedulingPattern) {
        this.schedulingPattern = schedulingPattern;
    }
}
