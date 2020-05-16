package utils;

import base.LongId;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import services.AccountService.UserDataSet;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;

public class PageGenerator {
    private static final String HTML_DIR = "server_tml";
    private static final Configuration CFG = new Configuration();

    public static String getPage(String filename, Map<String, Object> data) {
        Writer stream = new StringWriter();
        try {
            Template template = CFG.getTemplate(HTML_DIR + File.separator + filename);
            template.process(data, stream);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
        return stream.toString();
    }
    public static String putOK(int status, LongId<UserDataSet> id, String login, String email)
    {
        JSONObject jsonData = new JSONObject();
        JSONObject jsonBody = new JSONObject();

        jsonData.put("status", status);
        jsonBody.put("id", id);
        jsonBody.put("name", login);
        jsonBody.put("email", email);
        jsonBody.put("password", "");
        jsonData.put("body", jsonBody);

        return jsonData.toJSONString();
    }

    public static String putERROR(int status, String message, String description)
    {
        JSONObject jsonData = new JSONObject();
        JSONObject jsonBody = new JSONObject();

        jsonData.put("status", status);
        jsonBody.put("message", message);
        jsonBody.put("description", description);
        jsonData.put("body", jsonBody);

        return jsonData.toJSONString();
    }

    public static String putOKscores(int status, ArrayList<UserDataSet> users) {

        JSONObject jsonData = new JSONObject();
        JSONObject jsonBody = new JSONObject();


        JSONArray jsonRecords = new JSONArray();

        int t = users.size() - 1;
        for (int i = t; i > -1 && i > t - 5; i--) {
            JSONObject score = new JSONObject();
            score.put("name", users.get(i).getName());
            score.put("games", users.get(i).getGames());
            score.put("score", users.get(i).getScore());
            jsonRecords.add(score);
        }

        jsonData.put("status", status);
        jsonBody.put("scores", jsonRecords);
        jsonData.put("body", jsonBody);

        return jsonData.toJSONString();
    }
}
