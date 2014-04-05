package hatanian.david.gaegceorchestrator.controller;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.config.Nullable;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.appengine.api.users.User;
import com.google.appengine.api.utils.SystemProperty;
import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.ExecutionRequest;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendException;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Api(name = "orchestrator", version = "v1", scopes = {EndPointsConstants.EMAIL_SCOPE}, clientIds = {EndPointsConstants.WEB_CLIENT_ID, com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID})
public class Executions {
    private static final int DEFAULT_LIMIT = 10;
    private StorageManager<Execution> executionStorageManager = new StorageManager<>(Execution.class);

    private void checkAccessRights(User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("You must authenticate");
        } else if (!user.getEmail().equals("david.hatanian@gmail.com")) {
            if (SystemProperty.environment.value().equals(SystemProperty.Environment.Value.Development) && user.getEmail().equals("example@example.com")) {
                //We allow example@example.com as the default user in the development env
            } else {
                throw new UnauthorizedException("You are not allowed to access this API with user " + user.getEmail());
            }
        }
    }

    public Execution get(@Named("id") String id, User user) throws UnauthorizedException, NotFoundException {
        checkAccessRights(user);
        Execution execution = executionStorageManager.get(id);
        if (execution == null) {
            throw new NotFoundException("Execution with id " + id + " was not found");
        }
        return execution;
    }

    @ApiMethod(name = "executions.list", httpMethod = "get")
    public CollectionResponse<Execution> list(@Nullable @Named("fromDate") Date fromDate, @Nullable @Named("toDate") Date toDate, @Nullable @Named("cursor") String cursorString,
                                              @Nullable @Named("limit") Integer limit,User user) throws UnauthorizedException {
        checkAccessRights(user);

        Cursor cursor = null;
        if (cursorString != null) {
            cursor = Cursor.fromWebSafeString(cursorString);
        }

        int actualLimit = limit == null ? DEFAULT_LIMIT : limit;

        List<String> filterColumns = new ArrayList<>();
        List<Object> filterValues = new ArrayList<>();
        if (fromDate != null) {
            filterColumns.add("startDate >=");
            filterValues.add(fromDate);
        }
        if (toDate != null) {
            filterColumns.add("startDate <=");
            filterValues.add(toDate);
        }

        QueryResultIterator<Execution> resultIterator = executionStorageManager.list(cursor, actualLimit, "startDate", filterColumns.toArray(new String[filterColumns.size()]), filterValues.toArray());
        boolean moreDataLeft = resultIterator.hasNext();
        Collection<Execution> executionList = new ArrayList<>(actualLimit);
        while (resultIterator.hasNext()) {
            executionList.add(resultIterator.next());
        }

        CollectionResponse.Builder<Execution> responseBuilder = CollectionResponse.<Execution>builder().setItems(executionList);
        if (moreDataLeft && !(resultIterator.getCursor() == null)) {
            responseBuilder.setNextPageToken(resultIterator.getCursor().toWebSafeString());
        }
        return responseBuilder.build();
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
