package hatanian.david.gaegceorchestrator;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskHandle;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class TaskStarterServlet extends HttpServlet {

	private static final long serialVersionUID = -3999085760254679834L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String queueString = req.getParameter("queue");
		String url = req.getParameter("url");
		String method = req.getParameter("method");
		String paramsString = req.getParameter("params");

		Queue queue = QueueFactory.getQueue(queueString);
		TaskOptions to = TaskOptions.Builder.withUrl(url).method(Method.valueOf(method));

		Properties params = new Properties();
		params.load(new StringReader(paramsString));
		for (Object key : params.keySet()) {
			String keyString = (String) key;
			to.param(keyString, params.getProperty(keyString));
		}
		TaskHandle taskHandle = queue.add(to);

		resp.setContentType("text/html");
		resp.getWriter().println("<html><body>");
		resp.getWriter().println("<p>The task is added. Name : " + taskHandle.getName() + "</p>");
		resp.getWriter().println("<a href='/admin/taskstarter'>Click here to go back to the task starter interface</a>");
		resp.getWriter().println("</html></body>");
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html");
		resp.getWriter().print("<html>"+
			"			<body>"+
			"				<h1>Task Starter</h1>"+
			"				<form action='/admin/taskstarter' method='post'>"+
			"					<label for='queue'>Task queue</label>"+
			"					<input type='text' id='queue' name='queue' value='default'></input>"+
			"					<br/>"+
			"					<label for='url'>URL</label>"+
			"					<input type='text' id='url' name='url' value='/'></input>"+
			"					<br/>"+
			"					<label for='method'>Method</label>"+
			"					<select id='method' name='method'>"+
			"						<option selected='selected'>GET</option>"+
			"						<option>POST</option>"+
			"						<option>PULL</option>"+
			"					</select>"+
			"					<br/>"+
			"					<label for='params'>Parameters (in java.properties format)</label>"+
			"					<textarea rows='5' cols='30' id='params' name='params'></textarea>"+
			"					<br/>"+
			"					<button type='submit'>Start the task</button>"+
			"				</form>"+
			"			</body>"+
			"</html>");
	}
}
