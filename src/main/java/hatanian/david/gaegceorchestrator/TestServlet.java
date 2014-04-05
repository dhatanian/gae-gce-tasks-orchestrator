package hatanian.david.gaegceorchestrator;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import hatanian.david.gaegceorchestrator.domain.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class TestServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        GCEConfiguration gceConfiguration = new GCEConfiguration();
        gceConfiguration.setImage("test-image");
        gceConfiguration.setMachineType("test-machine-type");
        gceConfiguration.setZone("test-zone");

        UserScript userScript = new UserScript();
        userScript.setBucket("test-bucket");
        userScript.setPath("test-path");
        userScript.setTimeoutMs(365*24*60*60*1000);

        List<Execution> executionList = new ArrayList<>();

        Random random = new Random();
        for(int i=0;i<1000;i++){
            Execution e = new Execution();
            e.setDone(false);
            e.setGceConfiguration(gceConfiguration);
            e.setProjectId("test-project");
            e.setRequester("test-requested@gmail.com");
            e.setResultBucket("test-bucket");
            e.setUserScript(userScript);

            int nextInt = random.nextInt(3);
            switch (nextInt){
                case 0:
                case 1:
                    //Execution stopped with either 0 or 1 as result code (success or failure)
                    e.setDone(true);
                    e.setState(State.DONE);
                    e.setEndDate(new Date());
                    ExecutionBackendResult executionBackendResult = new ExecutionBackendResult();
                    e.setBackendResult(executionBackendResult);
                    executionBackendResult.setExecutionId(e.getId());
                    executionBackendResult.setResultCode(nextInt);
                    break;
                case 2:
                    //Do nothing, the execution is not done
                    break;
            }

            executionList.add(e);
        }

        new StorageManager<Execution>(Execution.class).saveAll(executionList);
	}
}
