package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Embed;

//TODO add parameters here
@Embed
public class UserScript {
    private String bucket;
    private String path;
    private long timeoutMs;

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}
