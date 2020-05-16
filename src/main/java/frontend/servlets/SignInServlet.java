package frontend.servlets;

import base.AccountService.AccountService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import services.AccountService.UserDataSet;
import utils.Crypt;
import utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class SignInServlet extends HttpServlet {
    public static final String signInURL = "/api/v1/auth/signin";
    static final Logger logger = LogManager.getLogger(SignInServlet.class.getName());

    private AccountService accountService;

    public SignInServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - POST - Started");

        String login = request.getParameter("login");
        String password = request.getParameter("password");
        HttpSession session = request.getSession();
        String sessionID = session.getId();

        UserDataSet userDataSet = null;
        String hash;
        try {
            userDataSet = accountService.getUser(login);
        } catch (SQLException exception) { // 500
            logger.error(exception.getMessage());
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.toString(), exception.getMessage()));
        }
        if (userDataSet == null) { // 400
            String message = "Account with name: " + login + " doesn't exist";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_BAD_REQUEST, "Auth problem", message));
        } else {
            hash = Crypt.calcSha512(Crypt.calcSha512(password) + userDataSet.getSalt());
            if (userDataSet.getHash().equals(hash)) { // 200
                logger.info("User with name: " + login + " signed in ");
                response.getWriter().println(PageGenerator.putOK(HttpServletResponse.SC_OK, userDataSet.getId(), login, userDataSet.getEmail()));
                accountService.addSession(sessionID, userDataSet.getId());
            } else {  // 400
                String message = "Password isn't correct";
                logger.error(message);
                response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_BAD_REQUEST, "Auth problem", message));
            }
        }
    }
}