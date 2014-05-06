package hatanian.david.gaegceorchestrator.gcebackend;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;

public class GCEBackendServlet extends HttpServlet {

	private static final long serialVersionUID = -3899740007874060169L;
	private StorageManager<Execution> executionRepository = new StorageManager<Execution>(Execution.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String executionId = req.getParameter("executionId");
		GCEBackendService gceBackendService = new GCEBackendService();
		Execution execution = executionRepository.get(executionId);
		try {
			gceBackendService.startExecution(execution);
		} catch (InterruptedException | GCEBackendException e) {
			throw new ServletException("Unable to start GCE backend", e);
		}
	}

}
