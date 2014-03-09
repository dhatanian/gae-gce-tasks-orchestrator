package hatanian.david.gaegceorchestrator.cron;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CronServlet extends HttpServlet {

	private static final long serialVersionUID = -3999085760254679834L;
	private static final Logger logger = Logger
			.getLogger(CronServlet.class.getName());
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
        //TODO launch scheduled executions
	}
}
