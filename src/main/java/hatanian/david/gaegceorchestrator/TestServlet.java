package hatanian.david.gaegceorchestrator;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.GCEConfiguration;
import hatanian.david.gaegceorchestrator.domain.UserScript;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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

        for(int i=0;i<1000;i++){
            Execution e = new Execution();
            e.setDone(false);
            e.setGceConfiguration(gceConfiguration);
            e.setProjectId("test-project");
            e.setRequester("test-requested@gmail.com");
            e.setResultBucket("test-bucket");
            e.setUserScript(userScript);
            executionList.add(e);
        }

        new StorageManager<Execution>(Execution.class).saveAll(executionList);
	}
}
