package servlet;

import models.*;
import utils.errors.*;
import utils.Actions;
import utils.JSONparser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
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
        }

        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        JSONObject error;
        PrintWriter out = resp.getWriter();
        resp.setCharacterEncoding("UTF-8");
        String contentType = req.getContentType();
        if (contentType == null || !contentType.equals("application/json")) {
            error = new JSONObject();
            error.put(ErrorKeys.ACTION, Actions.RECEIVE);
            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
            error.put(ErrorKeys.TITLE, "Error while receiving the message");
            error.put(ErrorKeys.CODE, ErrorCodes.INVALID_CONTENT_TYPE);
            error.put(ErrorKeys.MESSAGE, 
                "An error has occured while receiving the message. The contentType is incorrect");
            error.put(ErrorKeys.SUGGESTION, 
                "Try changing the content type to 'application/json' or try sending a JSON file");
            out.write(error.toString());
            return;
        }

        JSONparser jsonParser;
        try {
            jsonParser = new JSONparser(req.getReader());
        } catch (IOException ex) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            error = new JSONObject();
            error.put(ErrorKeys.ACTION, Actions.READ);
            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
            error.put(ErrorKeys.TITLE, "Error while reading the message");
            error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
            error.put(ErrorKeys.MESSAGE, 
                "An error has occured while trying to read the message.");
            error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
            out.write(error.toString());
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
                if (contact == null) {
                    resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
                    error = new JSONObject();
                    error.put(ErrorKeys.ACTION, Actions.READ);
                    error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
                    error.put(ErrorKeys.TITLE, "Error while interpreting the message");
                    error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
                    error.put(ErrorKeys.MESSAGE, 
                        "An error has occured while trying to pull out needed information from the text");
                    error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
                    out.write(error.toString());
                } else {
                    int id = this.dbManager.registerUser(contact);
                    switch (id) {
                        // Errore durante la registrazione
                        case -1:
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            error = new JSONObject();
                            error.put(ErrorKeys.ACTION, Actions.REGISTER);
                            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
                            error.put(ErrorKeys.TITLE, "Error during registration");
                            error.put(ErrorKeys.CODE, ErrorCodes.REGISTRATION_FAILURE);
                            error.put(ErrorKeys.MESSAGE, "An error during registration has occured");
                            error.put(ErrorKeys.SUGGESTION, "Retry later");
                            out.write(error.toString());
                            break;
                    
                        // L'utente esiste già
                        case 0:
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            error = new JSONObject();
                            error.put(ErrorKeys.ACTION, Actions.REGISTER);
                            error.put(ErrorKeys.TYPE, ErrorTypes.WARNING);
                            error.put(ErrorKeys.TITLE, "Error during registration");
                            error.put(ErrorKeys.CODE, ErrorCodes.DUPLICATED_USER);
                            error.put(ErrorKeys.MESSAGE, "A user with the same email has been detected");
                            error.put(ErrorKeys.SUGGESTION, "Try logging in with the same credentials used to register");
                            out.write(error.toString());
                            break;

                        // Registrazione riuscita
                        default:
                            ArrayList<PhoneNumber> notInsertedPhoneNumbers = 
                                this.dbManager.insertPhoneNumbers(contact.getPhoneNumbers(), id);
                            ArrayList<Email> notInsertedEmails = 
                                this.dbManager.insertEmails(contact.getEmails(), id);
                            if (notInsertedPhoneNumbers.size() == 0 && notInsertedEmails.size() == 0) {
                                resp.setStatus(HttpServletResponse.SC_CREATED);
                            } else {

                            }
                            break;
                    }
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