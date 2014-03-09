package hatanian.david.gaegceorchestrator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hatanian.david.gaegceorchestrator.domain.Execution;
import hatanian.david.gaegceorchestrator.domain.State;
import hatanian.david.gaegceorchestrator.gcebackend.GCEBackendService;

public class WatchTaskServlet extends HttpServlet {

	private static final long serialVersionUID = -3899740007874060169L;
	private StorageManager<Execution> executionRepository = new StorageManager<>(Execution.class);

	private static final Logger logger = Logger.getLogger(WatchTaskServlet.class.getName());
	// 4 hours max of execution
	private static final long EXECUTION_TIMEOUT_MS = 4 * 60 * 60 * 1000;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String executionId = req.getParameter("executionId");
		// 2 modes : all tasks (in a cron) and one specific task
		GCEBackendService service = new GCEBackendService();
		
		try {
			if (executionId != null) {
				Execution execution = executionRepository.get(executionId);
				if (execution == null) {
					logger.log(Level.SEVERE, "The execution " + executionId + " cannot be found");
				} else {
					if(checkExecution(execution,service)){
						logger.info("Execution " + execution.getId() + " is under the timeout limit, we'll start another task in 4 minutes");
						Thread.sleep(4 * 60 * 1000L);
						GCEBackendService.startExecutionWatchingTask(execution.getId());
					}
				}
			} else {
				checkAllExecutions(service);
			}
		} catch (InterruptedException e) {
			throw new ServletException("Interrupted while sleeping", e);
		}
	}

	private void checkAllExecutions(GCEBackendService service) throws InterruptedException {
		List<Execution> executions = executionRepository.getBy("done", false);
		for (Execution execution : executions) {
			checkExecution(execution, service);
		}
	}

	private boolean checkExecution(Execution execution, GCEBackendService service) throws InterruptedException {
		logger.info("Checking execution " + execution.getId());
		// check if it is done. if done, turn off GCE instance and delete disk.
		// if more that 4 hours of execution, turn it off anyway
		boolean isTimeout = (System.currentTimeMillis() - execution.getStartDate().getTime() > EXECUTION_TIMEOUT_MS); 
		if (!execution.getDone() && !isTimeout) {
			logger.info("Execution " + execution.getId() + " is not done yet, nothing else to do");
			return true;
		} else {
			if(isTimeout){
				logger.warning("Execution " + execution.getId() + " has gone over the timeout limit, deleting disk and instance");
				execution.setState(State.FAILED);
			}else{
				logger.info("Execution " + execution.getId() + " is done. Cleaning up resources");
				execution.setState(State.DONE);
			}
			
			try {
				service.deleteDisk(execution.getDiskAndInstanceName(),execution.getGceConfiguration().getZone(), execution.getProjectId());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Unable to delete disk for execution " + execution.getId());
			}
			try {
				service.deleteInstance(execution.getDiskAndInstanceName(),execution.getGceConfiguration().getZone(), execution.getProjectId());
			} catch (Throwable t) {
				logger.log(Level.WARNING, "Unable to delete instance for execution " + execution.getId());
			}
			execution.setDone(true);
			executionRepository.save(execution);
			return false;
		}

	}

}
