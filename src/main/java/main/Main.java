package main;

import base.AccountService.AccountService;
import base.DatabaseService.DatabaseService;
import base.GameMechanics;
import base.MatchmakingService;
import base.WebSocketService;
import frontend.servlets.*;
import services.MatchmakingServiceImpl;
import services.WebSocketServiceImpl;
import mechanics.GameMechanicsImpl;
import services.AccountService.AccountServiceImpl;
import services.DatabaseService.DatabaseServiceImpl;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Servlet;

/**
 * Created by darkDesire on 20.09.2015.
 */
public class Main {
    static final Logger logger = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length == 1) {
            String portString = args[0];
            port = Integer.valueOf(portString);
        }

        System.out.append("Starting at port: ").append(String.valueOf(port)).append('\n');

        DatabaseService databaseService = new DatabaseServiceImpl();
        AccountService accountService = new AccountServiceImpl(databaseService);
        WebSocketService webSocketService = new WebSocketServiceImpl();
        MatchmakingService matchmakingService = new MatchmakingServiceImpl(webSocketService);
        GameMechanics gameMechanics = new GameMechanicsImpl(webSocketService, matchmakingService, accountService);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

        Servlet adminPage = new AdminPageServlet(accountService);
        Servlet profilePage = new UserPageServlet(accountService);
        Servlet gamePage = new GamePageServlet(accountService);
        Servlet scoreBoard = new ScoreBoardServlet(accountService);

        Servlet signUp = new SignUpServlet(accountService);
        Servlet signIn = new SignInServlet(accountService);
        Servlet checkAuth = new CheckAuthServlet(accountService);
        Servlet signOut = new SignOutServlet(accountService);

        context.addServlet(new ServletHolder(adminPage), AdminPageServlet.adminPageURL);
        context.addServlet(new ServletHolder(profilePage), UserPageServlet.userPageServletURL);
        context.addServlet(new ServletHolder(gamePage), GamePageServlet.gamePageURL);
        context.addServlet(new ServletHolder(scoreBoard), ScoreBoardServlet.scoreBoardURL);

        context.addServlet(new ServletHolder(signUp), SignUpServlet.signUpURL);
        context.addServlet(new ServletHolder(signIn), SignInServlet.signInURL);
        context.addServlet(new ServletHolder(checkAuth), CheckAuthServlet.checkAuthURL);
        context.addServlet(new ServletHolder(signOut), SignOutServlet.signOutURL);

        context.addServlet(new ServletHolder(new WebSocketGameServlet(accountService, gameMechanics, webSocketService)), WebSocketGameServlet.webSocketGameServletURL);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("public_html");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, context});

        Server server = new Server(port);
        server.setHandler(handlers);

        server.start();

        //run GM in main thread
        gameMechanics.run();
    }
}
