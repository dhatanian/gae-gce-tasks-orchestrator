package hatanian.david.gaegceorchestrator.oauth;


import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class OAuthConfiguration {
    public static final String DEFAULT_ID = "default";

    @Id
    private String id = DEFAULT_ID;

    private String projectId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public OAuthConfiguration() {
    }

    public OAuthConfiguration(String projectId) {
        this.projectId = projectId;
    }
}
