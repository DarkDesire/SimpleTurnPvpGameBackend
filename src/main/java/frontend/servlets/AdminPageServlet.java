package frontend.servlets;

import base.AccountService.AccountService;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by darkDesire on 20.09.2015.
 */
public class AdminPageServlet extends HttpServlet {
    public static final String adminPageURL = "/api/v1/admin";
    static final Logger logger = LogManager.getLogger(AdminPageServlet.class.getName());

    private AccountService accountService;

    public AdminPageServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - GET - Started");

        HttpSession session = request.getSession();
        String sessionID = session.getId();


        if (accountService.isAuthorised(sessionID)) {
            UserDataSet userDataSet = accountService.getUserBySession(sessionID);
            if(userDataSet.isAdmin()){ // 200
                logger.info("User with name: " + userDataSet.getLogin() + " logged into admin panel");
                Map<String, Object> pageVariables = new HashMap<>();

                String timeString = request.getParameter("shutdown");
                if (timeString != null) {
                    int timeMS = Integer.valueOf(timeString);
                    logger.info("Server will be down after: " + timeMS + " ms");
                    TimeHelper.sleep(timeMS);
                    logger.info("\nShutdown");
                    System.exit(0);
                }

                pageVariables.put("status", "run");
                pageVariables.put("users", accountService.getCountUsers());
                pageVariables.put("online", accountService.getCountLoggedInUsers());

                response.setContentType("text/html;charset=utf-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(PageGenerator.getPage("adminpage.html", pageVariables));
            } else { // 400
                String message = "You are not admin";
                logger.error(message);
                response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_BAD_REQUEST, "Auth problem", message));
            }
        } else { // 401
            String message = "You shall not pass. You do not have permission to access";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_UNAUTHORIZED, "Auth problem", message));
        }
    }
}
