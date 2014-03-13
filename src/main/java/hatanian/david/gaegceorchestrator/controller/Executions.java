package hatanian.david.gaegceorchestrator.controller;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.ExecutionRequest;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendException;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendService;

import java.io.IOException;

@Api(name = "orchestrator", version = "v1", scopes = {EndPointsConstants.EMAIL_SCOPE}, clientIds = {EndPointsConstants.WEB_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
public class Executions {
    private StorageManager<Execution> executionStorageManager = new StorageManager<>(Execution.class);

    private void checkAccessRights(User user) throws UnauthorizedException {
        if(user==null){
            throw new UnauthorizedException("You must authenticate");
        }else if(!user.getEmail().equals("david.hatanian@gmail.com")){
            throw new UnauthorizedException("You are not allowed to access this API with user "+user.getEmail());
        }
    }

    public Execution get(@Named("id") String id, User user) throws UnauthorizedException, NotFoundException {
        checkAccessRights(user);
        Execution execution = executionStorageManager.get(id);
        if(execution==null){
            throw new NotFoundException("Execution with id "+id+" was not found");
        }
        return execution;
    }

    @ApiMethod(name = "executions.start", httpMethod = "post")
    public Execution startExecution(ExecutionRequest request, User user) throws InterruptedException, GCEBackendException, IOException, UnauthorizedException {
        checkAccessRights(user);
        Execution result = new Execution(request);
        result.setRequester(user.getEmail());
        //TODO start in task queue
        GCEBackendService backendService = new GCEBackendService();
        backendService.startExecution(result);
        executionStorageManager.save(result);
        return result;
    }

}
