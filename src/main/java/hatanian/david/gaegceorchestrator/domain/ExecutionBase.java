package hatanian.david.gaegceorchestrator.domain;

public class ExecutionBase {
    private String requester;
    private String resultBucket;
    private UserScript userScript;
    private GCEConfiguration gceConfiguration = new GCEConfiguration();
    private String projectId;

    public ExecutionBase() {
        super();
    }

    public ExecutionBase(ExecutionRequest request) {
        if (request.getGceConfiguration() != null) {
            setGceConfiguration(request.getGceConfiguration());
        }
        setUserScript(request.getUserScript());
        setResultBucket(request.getResultBucket());
        setProjectId(request.getProjectId());
    }

    public GCEConfiguration getGceConfiguration() {
        return gceConfiguration;
    }

    public void setGceConfiguration(GCEConfiguration gceConfiguration) {
        this.gceConfiguration = gceConfiguration;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public UserScript getUserScript() {
        return userScript;
    }

    public void setUserScript(UserScript userScript) {
        this.userScript = userScript;
    }

    public String getResultBucket() {
        return resultBucket;
    }

    public void setResultBucket(String resultBucket) {
        this.resultBucket = resultBucket;
    }
}
