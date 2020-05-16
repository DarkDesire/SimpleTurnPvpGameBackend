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
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;

public class SignUpServlet extends HttpServlet {
    public static final String signUpURL = "/api/v1/auth/signup";
    static final Logger logger = LogManager.getLogger(SignUpServlet.class.getName());

    private AccountService accountService;

    public SignUpServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        logger.info("Servlet - POST - Started");

        String login = request.getParameter("login");
        String email = request.getParameter("email");
        String salt = request.getParameter("salt");
        String password = request.getParameter("password");
        String prePassword = Crypt.calcSha512(password) + salt;
        String hash = Crypt.calcSha512(prePassword);

        if (accountService.addUser(login, email, hash, salt)) {
            try { //200
                UserDataSet userDataSet = accountService.getUser(login);
                logger.info("User with name: " + login + " signed up");
                response.getWriter().println(PageGenerator.putOK(HttpServletResponse.SC_OK, userDataSet.getId(), login, email));
            } catch (SQLException e) {  //500
                logger.error(e.getMessage());
                response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString(), e.getMessage()));
            }
        } else { // 400
            String message = "User with name: " + login + " already exists";
            logger.error(message);
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_BAD_REQUEST, "This login is busy", message));
        }
    }
}