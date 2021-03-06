package hatanian.david.gaegceorchestrator.domain;

import com.googlecode.objectify.annotation.Embed;

@Embed
public class ExecutionBackendResult {
    private String executionId;
    private Integer resultCode;

    public ExecutionBackendResult() {
    }

    public String getExecutionId() {
        return executionId;
    }

    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }
}
