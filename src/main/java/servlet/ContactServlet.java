package servlet;

import models.*;
import utils.*;
import utils.errors.*;
import utils.url_level_values.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;

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

    PathParser pathParser = new PathParser(req.getPathInfo(), req.getParameterMap());
    ArrayList<String> pathTokens = pathParser.getPathTokens();
    if (pathTokens.size() == 0) {
      pathTokens.add(FirstLevelValues.NOT_PROVIDED);
    }

    User user = this.getCredentials(req);
    if (user == null && !pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.MISSING_AUTHENTICATION).toString());
      return;
    }
    if (!this.dbManager.testCredentials(user)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.FAILED_AUTHENTICATION).toString());
      return;
    }

    switch (pathTokens.get(0)) {
      case FirstLevelValues.CONTACTS:
        try {
          int id = Integer.parseInt(pathTokens.get(1));
          Contact contact = this.dbManager.getContact(id, user);
          if (contact == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(contact.toJSON().toString());
          }
        } catch (IndexOutOfBoundsException ex) {
          ArrayList<Contact> contacts = this.dbManager.getContacts(user);
          if (contacts.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            JSONArray JSONContacts = new JSONArray(contacts);
            out.write(JSONContacts.toString());
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        }
        break;

      case FirstLevelValues.GROUPS:
        try {
          int id = Integer.parseInt(pathTokens.get(1));
          Group group = this.dbManager.getGroup(id, user);
          if (group == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            try {
              switch (pathTokens.get(2)) {
                case SecondLevelValues.CONTACTS:
                  resp.setStatus(HttpServletResponse.SC_OK);
                  out.write(new JSONArray(group.getContacts()).toString());
                  break;

                default:
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
                  break;
              }
            } catch (IndexOutOfBoundsException ex) {
              resp.setStatus(HttpServletResponse.SC_OK);
              out.write(group.toJSON().toString());
            }
          }
        } catch (IndexOutOfBoundsException ex) {
          ArrayList<Group> groups = this.dbManager.getGroups(user);
          if (groups.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            JSONArray JSONGroups = new JSONArray(groups);
            out.write(JSONGroups.toString());
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        }
        break;

      case FirstLevelValues.CALLS:
        try {
          int id = Integer.parseInt(pathTokens.get(1));
          Call call = this.dbManager.getCall(id, user);
          if (call == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.write(call.toJSON().toString());
          }
        } catch (IndexOutOfBoundsException ex) {
          ArrayList<Call> calls = this.dbManager.getCalls(user);
          if (calls.size() == 0) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            JSONArray JSONCalls = new JSONArray(calls);
            out.write(JSONCalls.toString());
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        }
        break;

      // Non è stato specificato nessun componente di primo livello nell'URL
      case FirstLevelValues.NOT_PROVIDED:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        break;

      // Il componente di primo livello specificato è sbagliato
      default:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
        break;
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
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.INVALID_CONTENT_TYPE).toString());
      return;
    }

    JSONparser jsonParser;
    try {
      jsonParser = new JSONparser(req.getReader());
    } catch (IOException ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
      return;
    } catch (JSONException ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
      return;
    }

    PathParser pathParser = new PathParser(req.getPathInfo(), req.getParameterMap());
    ArrayList<String> pathTokens = pathParser.getPathTokens();
    if (pathTokens.size() == 0) {
      pathTokens.add(FirstLevelValues.NOT_PROVIDED);
    }

    // Dato che le interazioni con i web service sono senza stato, è necessario
    // che l'utente venga autenticato ad ogni richiesta
    User user = this.getCredentials(req);
    if (user == null && !pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.MISSING_AUTHENTICATION).toString());
      return;
    }

    // Se l'utente non sta tentando di registrarsi, controlla le credenziali
    if (!pathTokens.get(0).equals(FirstLevelValues.USERS) || jsonParser.isJustLogin()) {
      if (!this.dbManager.testCredentials(user)) {
        resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        out.write(ErrorHandler.getError(ErrorCodes.FAILED_AUTHENTICATION).toString());
        return;
      } else {
        // Può essere che l'utente voglia soltanto testare le sue credenziali
        if (jsonParser.isJustLogin()) {
          resp.setStatus(HttpServletResponse.SC_OK);
          return;
        }
      }
    }

    switch (pathTokens.get(0)) {
      // Creazione di un utente (registrazione)
      case FirstLevelValues.USERS:
        Contact contact = jsonParser.getRegistrationCredentials();
        if (contact == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
        } else {
          int id = this.dbManager.registerUser(contact);
          switch (id) {
            // Errore durante la registrazione
            case -1:
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              out.write(ErrorHandler.getError(ErrorCodes.REGISTRATION_FAILURE).toString());
              break;

            // L'utente esiste già
            case 0:
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              out.write(ErrorHandler.getError(ErrorCodes.DUPLICATED_USER).toString());
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
                JSONObject notInserted = new JSONObject();
                if (notInsertedPhoneNumbers.size() > 0) {
                  notInserted.put("phoneNumbers", new JSONArray(notInsertedPhoneNumbers));
                }
                if (notInsertedEmails.size() > 0) {
                  notInserted.put("emails", new JSONArray(notInsertedEmails));
                }
                out.write(
                    (ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, notInserted)).toString());
              }
              break;
          }
        }
        break;

      // Creazione di un contatto
      case FirstLevelValues.CONTACTS:
        try {
          int id = Integer.parseInt(pathTokens.get(1));
          if (this.dbManager.getContact(id, user) == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(ErrorHandler.getError(ErrorCodes.INACCESSIBLE_OR_NON_EXISTING_RESOURCE).toString());
          } else {
            String secondLevelValue;
            try {
              secondLevelValue = pathTokens.get(2);
            } catch (IndexOutOfBoundsException ex) {
              secondLevelValue = SecondLevelValues.NOT_PROVIDED;
            }

            switch (secondLevelValue) {
              // Aggiunge numeri di telefono ad un contatto
              case SecondLevelValues.PHONE_NUMBERS:
                ArrayList<PhoneNumber> phoneNumbers = jsonParser.getPhoneNumbers();
                ArrayList<PhoneNumber> notInsertedPhoneNumbers = this.dbManager.insertPhoneNumbers(phoneNumbers, id);
                if (notInsertedPhoneNumbers.size() > 0) {
                  resp.setStatus(HttpServletResponse.SC_CONFLICT);
                  JSONObject notInserted = new JSONObject();
                  if (notInsertedPhoneNumbers.size() > 0) {
                    notInserted.put("phoneNumbers", new JSONArray(notInsertedPhoneNumbers));
                  }
                  out.write((ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, notInserted))
                      .toString());
                } else {
                  resp.setStatus(HttpServletResponse.SC_CREATED);
                  resp.setHeader("Location", req.getRequestURL().toString());
                }
                break;

              // Aggiunge indirizzi email ad un contatto
              case SecondLevelValues.EMAILS:
                ArrayList<Email> emails = jsonParser.getEmails();
                ArrayList<Email> notInsertedEmails = this.dbManager.insertEmails(emails, id);
                if (notInsertedEmails.size() > 0) {
                  resp.setStatus(HttpServletResponse.SC_CONFLICT);
                  JSONObject notInserted = new JSONObject();
                  if (notInsertedEmails.size() > 0) {
                    notInserted.put("emails", new JSONArray(notInsertedEmails));
                  }
                  out.write((ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, notInserted))
                      .toString());
                } else {
                  resp.setStatus(HttpServletResponse.SC_CREATED);
                  resp.setHeader("Location", req.getRequestURL().toString());
                }
                break;

              case SecondLevelValues.NOT_PROVIDED:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
                break;

              default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
                break;
            }
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          Contact contact2 = jsonParser.getContact();
          if (contact2 == null) {
            resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
            out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
          } else {
            if (
              user.equals(contact2.getOwner()) &&
              (contact2.getAssociatedUser() == null || user.equals(contact2.getAssociatedUser()))
            ) {
              int id = this.dbManager.insertContact(contact2);
              if (id == -1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write((ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, contact2.toJSON()))
                    .toString());
              } else {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.setHeader("Location", req.getRequestURL().toString() + id);
              }
            } else {
              resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              out.write(ErrorHandler.getError(ErrorCodes.CREDENTIALS_MISMATCH).toString());
            }
          }
        }
        break;

      // Creazione di un gruppo
      case FirstLevelValues.GROUPS:
        try {
          int id = Integer.parseInt(pathTokens.get(1));
          if (this.dbManager.getGroup(id, user) == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(ErrorHandler.getError(ErrorCodes.INACCESSIBLE_OR_NON_EXISTING_RESOURCE).toString());
          } else {
            String secondLevelValue;
            try {
              secondLevelValue = pathTokens.get(2);
            } catch (IndexOutOfBoundsException ex) {
              secondLevelValue = SecondLevelValues.NOT_PROVIDED;
            }

            switch (secondLevelValue) {
              // Aggiugne nuovi contatti al gruppo
              case SecondLevelValues.CONTACTS:
                ArrayList<Contact> contacts = jsonParser.getContacts();
                ArrayList<Contact> notInsertedContacts = this.dbManager.insertContactsInGroup(contacts, id);
                if (notInsertedContacts.size() > 0) {
                  resp.setStatus(HttpServletResponse.SC_CONFLICT);
                  JSONObject notInserted = new JSONObject();
                  if (notInsertedContacts.size() > 0) {
                    notInserted.put("contacts", new JSONArray(notInsertedContacts));
                  }
                  out.write(
                      (ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, notInserted)).toString());
                } else {
                  resp.setStatus(HttpServletResponse.SC_CREATED);
                  resp.setHeader("Location", req.getRequestURL().toString());
                }
                break;

              case SecondLevelValues.NOT_PROVIDED:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
                break;

              default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
                break;
            }
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          Group group = jsonParser.getGroup();
          if (group == null) {
            resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
            out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
          } else {
            if (user.equals(group.getOwner())) {
              int id = this.dbManager.insertGroup(group);
              if (id == -1) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write((ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, group.toJSON()))
                    .toString());
              } else {
                ArrayList<Contact> notInsertedContacts = this.dbManager.insertContactsInGroup(group.getContacts(), id);
                if (notInsertedContacts.size() == 0) {
                  resp.setStatus(HttpServletResponse.SC_CREATED);
                  resp.setHeader("Location", req.getRequestURL().toString() + id);
                } else {
                  resp.setStatus(HttpServletResponse.SC_CONFLICT);
                  JSONObject notInserted = new JSONObject();
                  notInserted.put("contacts", new JSONArray(notInsertedContacts));
                  out.write((ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, notInserted))
                      .toString());
                }
              }
            } else {
              resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
              out.write(ErrorHandler.getError(ErrorCodes.CREDENTIALS_MISMATCH).toString());
            }
          }
        }
        break;

      // Inserimento di una chiamata
      case FirstLevelValues.CALLS:
        Call call = jsonParser.getCall();
        if (call == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
        } else {
          if (user.equals(call.getCallerContact().getAssociatedUser())) {
            int id = this.dbManager.insertCall(call);
            if (id == -1) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              out.write(
                  (ErrorHandler.getError(ErrorCodes.INSERTION_FAILURE).put(ErrorKeys.DATA, call.toJSON())).toString());
            } else {
              resp.setStatus(HttpServletResponse.SC_CREATED);
              resp.setHeader("Location", req.getRequestURL().toString() + id);
            }
          } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.write(ErrorHandler.getError(ErrorCodes.CREDENTIALS_MISMATCH).toString());
          }
        }
        break;

      case FirstLevelValues.NOT_PROVIDED:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        break;

      default:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
        break;
    }

    out.flush();
    out.close();
  }

  @Override
  protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    resp.setCharacterEncoding("UTF-8");

    String contentType = req.getContentType();
    if (contentType == null || !contentType.equals("application/json")) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.INVALID_CONTENT_TYPE).toString());
      return;
    }

    JSONparser jsonParser;
    try {
      jsonParser = new JSONparser(req.getReader());
    } catch (IOException ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
      return;
    } catch (JSONException ex) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
      return;
    }

    PathParser pathParser = new PathParser(req.getPathInfo(), req.getParameterMap());
    ArrayList<String> pathTokens = pathParser.getPathTokens();
    if (pathTokens.size() == 0) {
      pathTokens.add(FirstLevelValues.NOT_PROVIDED);
    }

    // Dato che le interazioni con i web service sono senza stato, è necessario
    // che l'utente venga autenticato ad ogni richiesta
    User user = this.getCredentials(req);
    if (user == null && !pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.MISSING_AUTHENTICATION).toString());
      return;
    }

    if (!this.dbManager.testCredentials(user)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.FAILED_AUTHENTICATION).toString());
      return;
    }

    switch (pathTokens.get(0)) {
      // Modifica delle credenziali di accesso
      case FirstLevelValues.USERS:
        User newUserCredentials = jsonParser.getUser();
        if (newUserCredentials == null) {
          resp.setStatus((HttpServletResponse.SC_BAD_REQUEST));
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
        } else {
          if (user.equals(newUserCredentials)) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIABLE).toString());
          } else {
            if (this.dbManager.updateUser(user, newUserCredentials)) {
              resp.setStatus(HttpServletResponse.SC_OK);
            } else {
              resp.setStatus(HttpServletResponse.SC_CONFLICT);
              out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIED).toString());
            }
          }
        }
        break;

      /**
       * Modifica di un contatto e di tutte le sue proprietà ad eccezione di 'owner',
       * 'associated user' e 'id'.
       */
      case FirstLevelValues.CONTACTS:
        try {
          int contactId = Integer.parseInt(pathTokens.get(1));
          try {
            switch (pathTokens.get(2)) {
              case SecondLevelValues.PHONE_NUMBERS:
                try {
                  int numberId = Integer.parseInt(pathTokens.get(3));
                  PhoneNumber phoneNumber = jsonParser.getPhoneNumber();
                  if (phoneNumber == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
                  } else {
                    if (this.dbManager.updateContactPhoneNumber(contactId, numberId, phoneNumber)) {
                      resp.setStatus(HttpServletResponse.SC_OK);
                    } else {
                      resp.setStatus(HttpServletResponse.SC_CONFLICT);
                      out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIED).toString());
                    }
                  }
                } catch (NumberFormatException ex) {
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
                } catch (IndexOutOfBoundsException ex) {
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
                }
                break;

              case SecondLevelValues.EMAILS:
                try {
                  String base64Email = pathTokens.get(3);
                  String oldEmail = new String(Base64.getDecoder().decode(base64Email), StandardCharsets.UTF_8);
                  Email newEmail = jsonParser.getEmail();
                  if (newEmail == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
                  } else {
                    if (this.dbManager.updateContactEmail(contactId, oldEmail, newEmail)) {
                      resp.setStatus(HttpServletResponse.SC_OK);
                    } else {
                      resp.setStatus(HttpServletResponse.SC_CONFLICT);
                      out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIED).toString());
                    }
                  }
                } catch (NumberFormatException ex) {
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
                } catch (IndexOutOfBoundsException ex) {
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
                }
                break;

              default:
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
                break;
            }
          } catch (IndexOutOfBoundsException ex) {
            Contact newContact = jsonParser.getContact();
            if (newContact == null) {
              resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
              out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
            } else {
              if (this.dbManager.updateContact(contactId, newContact)) {
                resp.setStatus(HttpServletResponse.SC_OK);
              } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIED).toString());
              }
            }
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        }
        break;

      // Modifica del nome del gruppo.
      case FirstLevelValues.GROUPS:
        try {
          int groupId = Integer.parseInt(pathTokens.get(1));
          Group group = jsonParser.getGroup();
          if (group == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(ErrorHandler.getError(ErrorCodes.WRONG_SYNTAX).toString());
          } else {
            if (this.dbManager.updateGroupName(groupId, group.getName())) {
              resp.setStatus(HttpServletResponse.SC_OK);
            } else {
              resp.setStatus(HttpServletResponse.SC_CONFLICT);
              out.write(ErrorHandler.getError(ErrorCodes.DATA_NOT_MODIFIED).toString());
            }
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        }
        break;

      case FirstLevelValues.NOT_PROVIDED:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        break;

      default:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
        break;
    }

    out.flush();
    out.close();
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    PrintWriter out = resp.getWriter();
    resp.setCharacterEncoding("UTF-8");

    PathParser pathParser = new PathParser(req.getPathInfo(), req.getParameterMap());
    ArrayList<String> pathTokens = pathParser.getPathTokens();
    if (pathTokens.size() == 0) {
      pathTokens.add(FirstLevelValues.NOT_PROVIDED);
    }

    // Dato che le interazioni con i web service sono senza stato, è necessario
    // che l'utente venga autenticato ad ogni richiesta
    User user = this.getCredentials(req);
    if (user == null && !pathTokens.get(0).equals(FirstLevelValues.USERS)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.MISSING_AUTHENTICATION).toString());
      return;
    }

    if (!this.dbManager.testCredentials(user)) {
      resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      out.write(ErrorHandler.getError(ErrorCodes.FAILED_AUTHENTICATION).toString());
      return;
    }

    switch (pathTokens.get(0)) {
      // Eliminazione dell'utente
      case FirstLevelValues.USERS:
        try {
          String base64Email = pathTokens.get(1);
          String userEmail = new String(Base64.getDecoder().decode(base64Email), StandardCharsets.UTF_8);
          if (user.getEmail().equals(userEmail)) {
            if (this.dbManager.deleteUser(userEmail)) {
              resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            } else {
              resp.setStatus(HttpServletResponse.SC_CONFLICT);
              out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
            }
          } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write(ErrorHandler.getError(ErrorCodes.DELETION_UNAUTHORIZED).toString());
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        }
        break;

      case FirstLevelValues.CONTACTS:
        try {
          int contactId = Integer.parseInt(pathTokens.get(1));
          Contact contact = this.dbManager.getContact(contactId, user);
          if (contact == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
          } else if (user.equals(contact.getAssociatedUser())) {
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write(ErrorHandler.getError(ErrorCodes.DELETION_NOT_ALLOWED).toString());
          } else {
            try {
              switch (pathTokens.get(2)) {
                case SecondLevelValues.PHONE_NUMBERS:
                  try {
                    // Eliminazione di un solo numero di telefono
                    int numberId = Integer.parseInt(pathTokens.get(3));
                    if (this.dbManager.getPhoneNumber(numberId) == null) {
                      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                      if (this.dbManager.deletePhoneNumber(contactId, numberId)) {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                      } else {
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
                      }
                    }
                  } catch (NumberFormatException ex) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
                  } catch (IndexOutOfBoundsException ex) {
                    // Eliminazione di tutti i numeri di telefono di un contatto
                    if (this.dbManager.deletePhoneNumbers(contactId)) {
                      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                      resp.setStatus(HttpServletResponse.SC_CONFLICT);
                      out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
                    }
                  }
                  break;

                case SecondLevelValues.EMAILS:
                  try {
                    // Eliminazione di un indirizzo email
                    String base64Email = pathTokens.get(3);
                    String emailAddress = new String(Base64.getDecoder().decode(base64Email), StandardCharsets.UTF_8);
                    if (this.dbManager.getEmail(emailAddress) == null) {
                      resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    } else {
                      if (this.dbManager.deleteEmail(contactId, emailAddress)) {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                      } else {
                        resp.setStatus(HttpServletResponse.SC_CONFLICT);
                        out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
                      }
                    }
                  } catch (NumberFormatException ex) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
                  } catch (IndexOutOfBoundsException ex) {
                    // Eliminazione di tutti gli indirizzi email di un contatto
                    if (this.dbManager.deleteEmails(contactId)) {
                      resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                      resp.setStatus(HttpServletResponse.SC_CONFLICT);
                      out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
                    }
                  }
                  break;

                default:
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                  out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
                  break;
              }
            } catch (IndexOutOfBoundsException ex) {
              // Eliminazione di un contatto
              if (this.dbManager.deleteContact(contactId)) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
              } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                out.write(ErrorHandler.getError(ErrorCodes.DELETION_FAILED).toString());
              }
            }
          }
        } catch (NumberFormatException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.WRONG_OBJECT_ID).toString());
        } catch (IndexOutOfBoundsException ex) {
          resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
          out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        }
        break;

      case FirstLevelValues.NOT_PROVIDED:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.MISSING_URL_COMPONENT).toString());
        break;

      default:
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        out.write(ErrorHandler.getError(ErrorCodes.WRONG_URL_COMPONENT).toString());
        break;
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

  /**
   * Estrae le credenziali dall'header HTTP.
   * 
   * @param req richiesta HTTP
   * 
   * @return credenziali dell'utente
   */
  private User getCredentials(HttpServletRequest req) {
    String authString = req.getHeader("Authorization");
    if (authString != null && authString.toUpperCase().startsWith(HttpServletRequest.BASIC_AUTH)) {
      String base64Credentials = authString.substring(HttpServletRequest.BASIC_AUTH.length()).trim();
      byte[] credentialsDecoded = Base64.getDecoder().decode(base64Credentials);
      String credentials = new String(credentialsDecoded, StandardCharsets.UTF_8);
      final String[] values = credentials.split(":", 2);
      return new User(values[0], values[1]);
    } else {
      return null;
    }
  }
}