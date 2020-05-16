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

/**
 * Created by darkDesire on 26.01.2016.
 */
public class CheckAuthServlet extends HttpServlet {
    public static final String checkAuthURL = "/api/v1/auth/check";
    static final Logger logger = LogManager.getLogger(CheckAuthServlet.class.getName());

    private AccountService accountService;

    public CheckAuthServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - GET - Started");

        HttpSession session = request.getSession();
        String sessionID = session.getId();

        if (accountService.isAuthorised(sessionID)) { // 200
            UserDataSet userDataSet = accountService.getUserBySession(sessionID);
            logger.info("User with name: " + userDataSet.getLogin() + " is authorised ");
            response.getWriter().println(PageGenerator.putOK(HttpServletResponse.SC_OK, userDataSet.getId(), userDataSet.getLogin(), userDataSet.getEmail()));
        } else { // 400
            String message = "You are not authorized";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_BAD_REQUEST, "Auth problem", message));
        }
    }
}