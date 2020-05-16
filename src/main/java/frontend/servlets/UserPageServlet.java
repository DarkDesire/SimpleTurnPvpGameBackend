package frontend.servlets;

import base.AccountService.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.AccountService.UserDataSet;
import utils.PageGenerator;
import utils.TimeHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by darkDesire on 23.09.2015.
 */
public class UserPageServlet extends HttpServlet {
    public static final String userPageServletURL = "/api/v1/user";
    static final Logger logger = LogManager.getLogger(UserPageServlet.class.getName());

    private AccountService accountService;

    public UserPageServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - GET - Started");

        HttpSession session = request.getSession();
        String sessionID = session.getId();

        if (accountService.isAuthorised(sessionID)) { // 200
            UserDataSet userDataSet = accountService.getUserBySession(sessionID);
            logger.info("User with name: " + userDataSet.getLogin() + " logged into user panel");
            Map<String, Object> pageVariables = new HashMap<>();

            pageVariables.put("profileStatus", "Hello, " + userDataSet.getName() + ", email: " + userDataSet.getEmail());
            pageVariables.put("online", 1);
            pageVariables.put("name", userDataSet.getName());
            pageVariables.put("email", userDataSet.getEmail());

            response.setContentType("text/html;charset=utf-8");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().println(PageGenerator.getPage("userpage.html", pageVariables));
        } else { // 401
            String message = "You shall not pass. You do not have permission to access";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_UNAUTHORIZED, "Auth problem", message));
        }
    }
}
