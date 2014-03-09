package hatanian.david.gaegceorchestrator.gcebackend;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.ExecutionBackendResult;
import hatanian.david.gaegceorchestrator.domain.State;

public class BackendResultServlet extends HttpServlet {

	private static final long serialVersionUID = -3999085760254679834L;
	private static final Logger logger = Logger
			.getLogger(BackendResultServlet.class.getName());
	private StorageManager<Execution> executionRepository = new StorageManager<>(Execution.class);
	private Gson gson = new Gson();

    //TODO needs password
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		ExecutionBackendResult result = gson.fromJson(req.getReader(), ExecutionBackendResult.class);
		logger.info("Got result for execution "+result.getExecutionId());
		Execution execution = executionRepository.get(result.getExecutionId());
		execution.setBackendResult(result);
		execution.setDone(true);
		execution.setState(State.DONE);
		execution.setEndDate(new Date());
		Level level = Level.INFO;
		if(execution.getBackendResult().getResultCode()!=0){
			level = Level.SEVERE;
			execution.setState(State.FAILED);
		}
		logger.log(level, "Execution ended with result : "+execution.getBackendResult().getResultCode());
		executionRepository.save(execution);
		GCEBackendService service = new GCEBackendService();
        //TODO do this in a task queue
		service.deleteDisk(execution.buildDiskAndInstanceName(),execution.getGceConfiguration().getZone(), execution.getProjectId());
	}
}
