package hatanian.david.gaegceorchestrator.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;

import hatanian.david.gaegceorchestrator.StorageManager;
import hatanian.david.gaegceorchestrator.domain.Execution;

//TODO replace with cloud endpoints
public class ReportServlet extends HttpServlet {

	private static final long serialVersionUID = -3899740007874060169L;

	private StorageManager<Execution> executionRepository = new StorageManager<>(Execution.class);
	private Gson gson = new Gson();
	private UserService userService = UserServiceFactory.getUserService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//TODO
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//TODO
	}
}
