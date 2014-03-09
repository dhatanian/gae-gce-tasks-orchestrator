package hatanian.david.gaegceorchestrator.controller;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.GCEConfiguration;
import hatanian.david.gaegceorchestrator.domain.UserScript;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendException;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

//TODO replace with cloud endpoints
public class ReportServlet extends HttpServlet {

    private static final long serialVersionUID = -3899740007874060169L;

    private StorageManager<Execution> executionRepository = new StorageManager<>(Execution.class);
    private Gson gson = new Gson();
    Logger log = Logger.getLogger(ReportServlet.class.getName());
    private UserService userService = UserServiceFactory.getUserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //TODO
        GCEConfiguration configuration = new GCEConfiguration();
        UserScript script = new UserScript();
        script.setBucket("test-orchestrator-scripts");
        script.setPath("test.sh");
        script.setTimeoutMs(60 * 60 * 1000);

        Execution execution = new Execution();
        execution.setGceConfiguration(configuration);
        execution.setProjectId("revevol-cloudplatform-training");
        execution.setRequester("david.hatanian@gmail.com");
        execution.setResultBucket("test-orchestrator-results");
        execution.setUserScript(script);

        GCEBackendService service = new GCEBackendService();
        try {
            log.info("Starting execution "+execution.getId());
            service.startExecution(execution);
            log.info("Started execution "+execution.getId());
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted when waiting for an operation to finish", e);
        } catch (GCEBackendException e) {
            throw new ServletException("Unable to run the execution", e);
        }
        executionRepository.save(execution);
        log.info("Saved execution "+execution.getId());
    }
}
