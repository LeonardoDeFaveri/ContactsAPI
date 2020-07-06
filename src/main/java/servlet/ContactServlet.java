package servlet;

import models.Contact;
import models.User;
import utils.Actions;
import utils.JSONparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import org.json.*;

import database_manager.DatabaseManager;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "ContactsServlet", urlPatterns = { "/contacts/api/*" })
public class ContactServlet extends HttpServlet {
    private static final long serialVersionUID = -7554494032704522881L;
    private DatabaseManager dbManager;

    @Override
    public void init(ServletConfig config) throws ServletException {
        try {
            this.dbManager = new DatabaseManager();
        } catch (SQLException ex) {
            System.err.println("ERROR WHILE CONNETTING TO THE DATABASE");
            System.err.println(ex.getMessage());
            System.exit(ex.getErrorCode());
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF-8");

        String path = req.getPathInfo();
        if (path == null || path.equals("/")) {
            Map<String, String[]> query = req.getParameterMap();
            String[] category = query.get("category");
            String[] name = query.get("name");
            String[] qname = query.get("qname");
        }

        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF-8");
        String contentType = req.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            out.write("Invalid content type; it must be application/json");
            return;
        }

        JSONparser jsonParser;
        try {
            jsonParser = new JSONparser(req.getReader());
        } catch (IOException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("Error while reading the request body");
            return;
        }

        switch (jsonParser.getAction()) {
            case Actions.LOGIN:
                User user = jsonParser.getLoginCredentials();
                if (user != null && this.dbManager.testCredentials(user)) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                }
                break;
            
            case Actions.REGISTER:
                Contact contact = jsonParser.getRegistrationCredentials();
                if (contact != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
                }
                break;

            case Actions.CREATE:
                break;
        }

        out.flush();
        out.close();
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF_8");
        String contentType = req.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            resp.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
            out.write("Il contentType non è valido; deve esser application/json");
            return;
        }

        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Non è stato specificato l'id del contatto");
            return;
        }

        try {
            long contactId = Long.parseLong(path.substring(1));
            
            BufferedReader reader = req.getReader();
            StringBuilder jsonString = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonString.append(line);
            }

            Contact newContact = null;
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Location", req.getRequestURL().toString() + contactId);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("L'id non è stato specificato in un formato valido");
            return;
        } catch (JSONException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("La stringa json fornita non è valida");
            return;
        }
        out.flush();
        out.close();
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF-8");
        String path = req.getPathInfo();
        if (path == null || path.length() < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("Non è stato specificato l'id del contatto");
            return;
        }

        try {
            long contactId = Long.parseLong(path.substring(1));
            
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (NumberFormatException ex) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("L'id non è stato specificato in un formato valido");
            return;
        }
        out.flush();
        out.close();
    }

    @Override
    public void destroy() {
        try {
            this.dbManager.close();
        } catch (SQLException ex) {
            System.err.println("ERROR WHILE CLOSING CONNECTION WITH THE DATABASE");
            System.err.println(ex.getMessage());
            System.exit(ex.getErrorCode());
        }
    }
}