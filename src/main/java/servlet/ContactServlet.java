package servlet;

import models.*;
import utils.*;
import utils.errors.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;

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
      error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
      error.put(ErrorKeys.TITLE, "Error while receiving the message");
      error.put(ErrorKeys.CODE, ErrorCodes.INVALID_CONTENT_TYPE);
      error.put(ErrorKeys.MESSAGE, "An error has occured while receiving the message. The contentType is incorrect");
      error.put(ErrorKeys.SUGGESTION, "Try changing the content type to 'application/json' or try sending a JSON file");
      out.write(error.toString());
      return;
    }

    JSONparser jsonParser;
    try {
      jsonParser = new JSONparser(req.getReader());
    } catch (IOException ex) {
      resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      error = new JSONObject();
      error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
      error.put(ErrorKeys.TITLE, "Error while reading the message");
      error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
      error.put(ErrorKeys.MESSAGE, "An error has occured while trying to read the message.");
      error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
      out.write(error.toString());
      return;
    } catch (JSONException ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      error = new JSONObject();
      error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
      error.put(ErrorKeys.TITLE, "Error while reading the message");
      error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
      error.put(ErrorKeys.MESSAGE, "An error has occured while trying to read the json message.");
      error.put(ErrorKeys.SUGGESTION, "Try to check if you sent json, or check the syntax");
      out.write(error.toString());
      return;
    }

    PathParser pathParser = new PathParser(req.getPathInfo(), req.getParameterMap());
    ArrayList<String> pathTokens = pathParser.getPathTokens();
    if (pathTokens.size() == 0) {
      pathTokens.add(FirstLevelValues.NOT_PROVIDED);
    }

    // Dato che le interazioni con i web service sono senza stato, è necessario
    // che l'utente venga autenticato ad ogni richiesta
    User user = jsonParser.getLoginCredentials();
    if (user == null && !pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      error = new JSONObject();
      error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
      error.put(ErrorKeys.TITLE, "Missing Authenticatio");
      error.put(ErrorKeys.CODE, ErrorCodes.MISSING_AUTHENTICATION);
      error.put(ErrorKeys.MESSAGE, "Authentication parameters have not been provided");
      error.put(ErrorKeys.SUGGESTION, "Provide authentication parameters");
      out.write(error.toString());
      return;
    }
    // Se l'utente non sta tentando di registrarsi, controlla le credenziali
    if (!pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      if (!this.dbManager.testCredentials(user)) {
        error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Failed Authenticatio");
        error.put(ErrorKeys.CODE, ErrorCodes.FAILED_AUTHENTICATION);
        error.put(ErrorKeys.MESSAGE, "The authentication process has failed");
        error.put(ErrorKeys.SUGGESTION, "Try checking the authentication parameters provided");
        out.write(error.toString());
        return;
      }
  
      // Può essere che l'utente voglia soltanto testare le sue credenziali
      if (jsonParser.isJustLogin()) {
        resp.setStatus(HttpServletResponse.SC_OK);
        return;
      }
    }

    switch (pathTokens.get(0)) {
      // Creazione di un utente (registrazione)
      case FirstLevelValues.USERS:
        Contact contact = jsonParser.getRegistrationCredentials();
        if (contact == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          error = new JSONObject();
          error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
          error.put(ErrorKeys.TITLE, "Error while interpreting the message");
          error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
          error.put(ErrorKeys.MESSAGE,
              "An error has occured while trying to pull out needed information about the contact from the text");
          error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
          out.write(error.toString());
        } else {
          int id = this.dbManager.registerUser(contact);
          switch (id) {
            // Errore durante la registrazione
            case -1:
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              error = new JSONObject();
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
              error.put(ErrorKeys.TYPE, ErrorTypes.WARNING);
              error.put(ErrorKeys.TITLE, "Error during registration");
              error.put(ErrorKeys.CODE, ErrorCodes.DUPLICATED_USER);
              error.put(ErrorKeys.MESSAGE, "A user with the same email has been detected");
              error.put(ErrorKeys.SUGGESTION, "Try logging in with the same credentials used to register");
              out.write(error.toString());
              break;

            // Registrazione riuscita
            default:
              ArrayList<PhoneNumber> notInsertedPhoneNumbers = this.dbManager
                  .insertPhoneNumbers(contact.getPhoneNumbers(), id);
              ArrayList<Email> notInsertedEmails = this.dbManager.insertEmails(contact.getEmails(), id);
              if (notInsertedPhoneNumbers.size() == 0 && notInsertedEmails.size() == 0) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Location", req.getRequestURL().toString() + id);
              } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                error = new JSONObject();
                error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
                error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);

                JSONObject notInserted = new JSONObject();
                if (notInsertedPhoneNumbers.size() > 0) {
                  notInserted.put("phoneNumbers", new JSONArray(notInsertedPhoneNumbers));
                }
                if (notInsertedEmails.size() > 0) {
                  notInserted.put("emails", new JSONArray(notInsertedEmails));
                }
                error.put(ErrorKeys.DATA, notInserted);
                error.put(ErrorKeys.TITLE, "Insertion failure");
                error.put(ErrorKeys.MESSAGE, "Some phone numbers and/or email have not been inserted");
                error.put(ErrorKeys.SUGGESTION, "Try checking the values and retry");
                out.write(error.toString());
              }
              break;
          }
        }
        break;

      // Creazione di un contatto
      case FirstLevelValues.CONTACTS:
        Contact contact2 = jsonParser.getContact();
        if (contact2 == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          error = new JSONObject();
          error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
          error.put(ErrorKeys.TITLE, "Error while interpreting the message");
          error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
          error.put(ErrorKeys.MESSAGE,
              "An error has occured while trying to pull out needed information about the contact from the text");
          error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
          out.write(error.toString());
        } else {
          if (contact2.getOwner().equals(user) &&
            (contact2.getAssociatedUser() == null || contact2.getAssociatedUser().equals(user))
          ) {
            int id = this.dbManager.insertContact(contact2);
            if (id == -1) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              error = new JSONObject();
              error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
              error.put(ErrorKeys.TITLE, "Error during insertion");
              error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);
              error.put(ErrorKeys.MESSAGE, "An error during contact insertion has occured");
              error.put(ErrorKeys.SUGGESTION, "Retry later");
              out.write(error.toString());
            } else {
              resp.setStatus(HttpServletResponse.SC_CREATED);
              resp.setHeader("Location", req.getRequestURL().toString() + id);
            }
          } else {
            // Le credenziali con le quali si è autenticato l'utente devono
            // essere le stesse presenti nel campo 'owner' o, se specificato,
            // nel campo 'associateUser'
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            error = new JSONObject();
            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
            error.put(ErrorKeys.TITLE, "Failed Authentication");
            error.put(ErrorKeys.CODE, ErrorCodes.CREDENTIALS_MISMATCH);
            error.put(ErrorKeys.MESSAGE, 
              "The credentials of the user are different from the credentials of the owner user of the contact and/or from the credentials of the associated user");
            error.put(ErrorKeys.SUGGESTION, 
              "Try checking the authentication parameters provided or the owner and/or associated user sent");
            out.write(error.toString());
          }
        }
        break;
        
      // Creazione di un gruppo
      case FirstLevelValues.GROUPS:
        Group group = jsonParser.getGroup();
        if (group == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          error = new JSONObject();
          error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
          error.put(ErrorKeys.TITLE, "Error while interpreting the message");
          error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
          error.put(ErrorKeys.MESSAGE,
              "An error has occured while trying to pull out needed information about the group from the text");
          error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
          out.write(error.toString());
        } else {
          if (group.getOwner().equals(user)) {
            int id = this.dbManager.insertGroup(group);
            if (id == -1) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              error = new JSONObject();
              error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
              error.put(ErrorKeys.TITLE, "Error during insertion");
              error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);
              error.put(ErrorKeys.MESSAGE, "An error during group insertion has occured");
              error.put(ErrorKeys.SUGGESTION, "Retry later");
              out.write(error.toString());
            } else {
              ArrayList<Contact> notInsertedContacts = this.dbManager.insertContactsInGroup(group.getContacts(), id);
              if (notInsertedContacts.size() == 0) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Location", req.getRequestURL().toString() + id);
              } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                error = new JSONObject();
                error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
                error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);

                JSONObject notInserted = new JSONObject();
                notInserted.put("contacts", new JSONArray(notInsertedContacts));
                error.put(ErrorKeys.DATA, notInserted);
                error.put(ErrorKeys.TITLE, "Insertion failure");
                error.put(ErrorKeys.MESSAGE, "Some contacts have not been inserted");
                error.put(ErrorKeys.SUGGESTION, "Try checking the values and retry");
                out.write(error.toString());
              }
            }
          } else {
            // Le credenziali con le quali si è autenticato l'utente devono
            // essere le stesse presenti nel campo 'owner'
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            error = new JSONObject();
            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
            error.put(ErrorKeys.TITLE, "Failed Authentication");
            error.put(ErrorKeys.CODE, ErrorCodes.CREDENTIALS_MISMATCH);
            error.put(ErrorKeys.MESSAGE, 
              "The credentials of the user are different from the credentials of the owner user of the group");
            error.put(ErrorKeys.SUGGESTION, 
              "Try checking the authentication parameters provided or the owner user sent");
            out.write(error.toString());
          }
        }
        break;
      
      // Inserimento di una chiamata
      case FirstLevelValues.CALLS:
        Call call = jsonParser.getCall();
        if (call == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          error = new JSONObject();
          error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
          error.put(ErrorKeys.TITLE, "Error while interpreting the message");
          error.put(ErrorKeys.CODE, ErrorCodes.WRONG_SYNTAX);
          error.put(ErrorKeys.MESSAGE,
              "An error has occured while trying to pull out needed information about the call from the text");
          error.put(ErrorKeys.SUGGESTION, "Try checking the syntax");
          out.write(error.toString());
        } else {
          if (call.getCaller().getAssociatedUser().equals(user)) {
            int id = this.dbManager.insertCall(call);
            if (id == -1) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                error = new JSONObject();
                error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
                error.put(ErrorKeys.TITLE, "Error during insertion");
                error.put(ErrorKeys.CODE, ErrorCodes.INSERTION_FAILURE);
                error.put(ErrorKeys.MESSAGE, "An error during call insertion has occured");
                error.put(ErrorKeys.SUGGESTION, "Retry later");
                out.write(error.toString());
            } else {
              resp.setStatus(HttpServletResponse.SC_CREATED);
              resp.setHeader("Location", req.getRequestURL().toString() + id);
            }
          } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            error = new JSONObject();
            error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
            error.put(ErrorKeys.TITLE, "Failed Authentication");
            error.put(ErrorKeys.CODE, ErrorCodes.CREDENTIALS_MISMATCH);
            error.put(ErrorKeys.MESSAGE, 
              "The credentials of the user are different from the credentials of the associated user of the caller");
            error.put(ErrorKeys.SUGGESTION, 
              "Try checking the authentication parameters provided or the owner user sent");
            out.write(error.toString());
          }
        }
        break;

      // Non è stato specificato nessun componente di primo livello nell'URL
      case FirstLevelValues.NOT_PROVIDED:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while interpreting the URL");
        error.put(ErrorKeys.CODE, ErrorCodes.MISSING_URL_COMPONENT);
        error.put(ErrorKeys.MESSAGE, "The URL is the endpoint");
        error.put(ErrorKeys.SUGGESTION, "Try specifying a component in the URL");
        out.write(error.toString());
        break;

      // Il componente di primo livello specificato è sbagliato
      default:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        error = new JSONObject();
        error.put(ErrorKeys.TYPE, ErrorTypes.ERROR);
        error.put(ErrorKeys.TITLE, "Error while interpreting the URL");
        error.put(ErrorKeys.CODE, ErrorCodes.WRONG_URL_COMPONENT);
        error.put(ErrorKeys.MESSAGE, "The URL is incorrect");
        error.put(ErrorKeys.SUGGESTION, "Try specifying a valid first level component in the URL");
        out.write(error.toString());
        break;
    }

    out.flush();
    out.close();
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    resp.setCharacterEncoding("UTF-8");

    out.flush();
    out.close();
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    resp.setCharacterEncoding("UTF-8");

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