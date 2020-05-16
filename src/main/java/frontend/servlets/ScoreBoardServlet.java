package frontend.servlets;

import base.AccountService.AccountService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import services.AccountService.UserDataSet;
import utils.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by darkDesire on 26.01.2016.
 */
public class ScoreBoardServlet extends HttpServlet {
    public static final String scoreBoardURL = "/api/v1/scores";
    static final Logger logger = LogManager.getLogger(ScoreBoardServlet.class.getName());
    private AccountService accountService;

    public ScoreBoardServlet(AccountService accountService) {
        this.accountService = accountService;
    }

    public void doGet(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException, IOException {
        logger.info("ScoreBoardServlet - GET - Started");


        try { // 200
            ArrayList<UserDataSet> userDataSets = accountService.getFiveScoreUsers();
            logger.info("Return all users from DB successfully");
            response.getWriter().println(PageGenerator.putOKscores(HttpServletResponse.SC_OK,userDataSets));
        } catch (SQLException e) { // 500
            logger.error(e.getMessage());
            response.getWriter().println(PageGenerator.putERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.toString(), e.getMessage()));
        }
    }
}