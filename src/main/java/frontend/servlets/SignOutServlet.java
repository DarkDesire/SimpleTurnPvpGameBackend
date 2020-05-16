package frontend.servlets;

import base.AccountService.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import services.AccountService.UserDataSet;
import utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by darkDesire on 23.09.2015.
 */
public class SignOutServlet extends HttpServlet {
    public static final String signOutURL = "/api/v1/auth/signout";
    static final Logger logger = LogManager.getLogger(SignOutServlet.class.getName());

    private AccountService accountService;

    public SignOutServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - POST - Started");

        HttpSession session = request.getSession();
        String sessionID = session.getId();

        if (accountService.isAuthorised(sessionID)) { // 200
            UserDataSet userDataSet = accountService.getUserBySession(sessionID);
            logger.info("User with name: " + userDataSet.getLogin() + " signed out ");
            response.getWriter().println(PageGenerator.putOK(HttpServletResponse.SC_OK, userDataSet.getId(), userDataSet.getLogin(), userDataSet.getEmail()));
            accountService.deleteSession(sessionID);
        } else { // 401
            String message = "You shall not pass. You do not have permission to access";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_UNAUTHORIZED, "Auth problem", message));
        }
    }
}
