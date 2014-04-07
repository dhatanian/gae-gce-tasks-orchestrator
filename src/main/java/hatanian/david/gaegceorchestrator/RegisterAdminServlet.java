package hatanian.david.gaegceorchestrator;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import hatanian.david.gaegceorchestrator.domain.Admin;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RegisterAdminServlet extends HttpServlet {
    private UserService userService = UserServiceFactory.getUserService();
    private StorageManager<Admin> adminStorageManager = new StorageManager<>(Admin.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (userService.isUserAdmin()) {
            Admin admin = adminStorageManager.save(new Admin(userService.getCurrentUser().getEmail()));
            resp.getWriter().println("User " + admin.getEmail() + " successfully registered as an admin. You can close this window.");
        } else {
            resp.getWriter().print("You must be an App Engine administrator to register here. You can also ask an administrator to add you directly from the application console.");
        }
    }
}
