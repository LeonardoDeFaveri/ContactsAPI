<DOCTYPE html>
<html>
<body style="text-align: justify;">
  <h1>ContactsAPI</h1>
  <p>This is a REST web service that can be used to manage a rubric through the net.</p>

  <h2>Documentation</h2>
  <p>
    <b>Endpoint:</b>
    <i>http://truecloud.ddns.net:9000/contacts/api/</i>
  </p>
  <p>It is important to remember that the web service doesn't support the HTTPS protocol, so it is not allowed to use <i>https://truecloud.ddns.net:9000/contacts/api/</i> as the endpoint.</p>

  <h3>Data transmission</h3>
  <p>When data needs to be transfered into the HTTP request body (e.g. POST, PUT requests) it must be defined using the <a href="https://www.json.org" target="_blank">JSON</a> language.</p>

  <h3>Authentication</h3>
  <p>To make a request the user must be authenticated by the web service server. In order to do so, the user must provide its credentials into the HTTP <b>Authorization</b> header. The header type is Basic and the credentials have to be specified after the "Basic" string separeted by a column (username:password). This string (without "Basic") must be encoded with a base 64 encoding and the password mustn't be sent in clear, but its digest, calculated with a <b>SHA-256</b> function, has to be provided, instead.</p>

  <h3>Allowed HTTP methods</h3>
  <ul>
    <li><b>GET:</b> to get data from the service</li>
    <li><b>POST:</b> to create new resources</li>
    <li><b>PUT:</b> to modify an existing resource</li>
    <li><b>DELETE:</b> to delete a resource</li>
    <li><b>OPTION:</b> to get the list of all supported HTTP methods</li>
  </ul>
  <h3>GET requests</h3>
  <div class="centered">
    <table class="requests" id="get_requests">
      <thead>
        <tr>
          <th>Path</th>
          <th>Parameters</th>
          <th>Result</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>/contacts/</td>
          <td></td>
          <td>Returns all the contacts owned by the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}</td>
          <td><b>contactId:</b> the id of a contact</td>
          <td>Returns a specific contact, but only if that contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/</td>
          <td></td>
          <td>Returns all the groups owned by the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupid}</td>
          <td><b>groupId:</b> id of a specific group</td>
          <td>Returns a specific group, but only if that group belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupId}/contacts/</td>
          <td><b>groupId:</b> id of a specific group</td>
          <td>Returns all the contats of a specific group, but only if the group belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/calls/</td>
          <td></td>
          <td>Returns all the calls made and received by the user who is making the request</td>
        </tr>
        <tr>
          <td>/calls/{callId}/</td>
          <td><b>callId:</b> id of a specific call</td>
          <td>Returns a specific call, but only if it has been made or received by the user who is making the request</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>If the id provided doesn't identify any resource or if the resource identified cannot be returned (e.g. if the resource belongs to another user) then the response status code will be <b>404 Not Found</b>, otherwise the resource's JSON encoding is returned and the status code will be <b>200 OK</b>.</p>
  
  <h3>POST requests</h3>
  <div class="centered">
    <table class="requests" id="post_requests">
      <thread>
        <tr>
          <th>Path</th>
          <th>Parameters</th>
          <th>Result</th>
        </tr>
      </thread>
      <tbody>
        <tr>
          <td>/users/</td>
          <td>Only login request</td>
          <td>Test the user's credentials</td>
        </tr>
        <tr>
          <td>/users/</td>
          <td>Contact definition</td>
          <td>Creates a new user and the associated contact</td>
        </tr>
        <tr>
          <td>/contacts/</td>
          <td>Contact definition</td>
          <td>Creates a new contact and sets as owner the user who is making the reqest</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/phoneNumbers/</td>
          <td>
            <b>contactId:</b> id of a specific user <hr>
            List of phone numbers
          </td>
          <td>Creates new phone numbers and associates them to the contact identified by the id, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/emails/</td>
          <td>
            <b>contactId:</b> id of a specific user <hr>
            List of emails
          </td>
          <td>Creates new email addresses and associates them to the contact identified by the id, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/</td>
          <td>Group definition</td>
          <td>Creates a new group, but only if the group owner specified is the same user that is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupId}/contacts/</td>
          <td>
            <b>groupId:</b> id of a specific group <hr>
            List of contacts to insert into the group
          </td>
          <td>Inserts some contacts into a specific group, but only if the group and the contacts belong to the user who is making the request</td>
        </tr>
        <tr>
          <td>/calls/</td>
          <td>Call definition</td>
          <td>Inserts a call made by a contact, but only if that contact is associated to the user who is making the request</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>When the resource has been created it id is returned into the <b>Location</b> header of the response message and <b>210 Created</b> will be the status code.</p>
  <p>If an error occurs when the user tries to create a new contact or add new phone numbers or email addresses to a contact, then a list of all numbers or email addresses not entered successfully is returned.</p>
  
  <h3>PUT requests</h3>
  <div class="centered">
    <table class="requests" id="put_requests">
      <thead>
        <tr>
          <th>Path</th>
          <th>Parameters</th>
          <th>Result</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>/users/</td>
          <td>New user's credentials</td>
          <td>Modifies the credentials of the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/</td>
          <td>Contact definition</td>
          <td>Substitutes the old contact with a new one, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/phoneNumbers/{phoneId}</td>
          <td>
            <b>contactId:</b> id of a specific contact <hr>
            <b>phoneId:</b> id of a specific phoneNumber <hr>
            Phone number definition
          </td>
          <td>Substitutes one of the phone numbers of a contact with another, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/emails/{email}</td>
          <td>
            <b>contactId:</b> id of a specific contact <hr>
            <b>email:</b> old email<hr>
            Email definition
          </td>
          <td>Substitutes one of the emails of a contact with another, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupId}</td>
          <td>
            <b>groupId:</b> id of a specific group <hr>
            Group definition
          </td>
          <td>Modifies the name of a group, but only if the group belongs to the user who is making the request</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>When the email of the phone number of a contact is modified the resource is not really modified, it is subtituted with an already existing one o with a new one created in the moment of the request, so the resource id has to be updated with a GET request.</p>

  <h3>DELETE requests</h3>  
  <div class="centered">
    <table class="requests" id="delete_requests">
      <thead>
        <tr>
          <th>Path</th>
          <th>Parameters</th>
          <th>Result</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>/users/</td>
          <td></td>
          <td>Deletes the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contacId}</td>
          <td><b>contactId:</b> id of a specific contact</td>
          <td>Deletes a specific contact, but only if it belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contacId}/phoneNumbers/{phoneId}</td>
          <td>
            <b>contactId:</b> id of a specific contact <hr>
            <b>phoneId:</b> id of a specific phone number
          </td>
          <td>Deletes a phone number associated to a contact, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/emails/{email}</td>
          <td>
            <b>contactId:</b> id of a specific contact <hr>
            <b>email:</b> email
          </td>
          <td>Deletes an email address associated to a contact, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupId}</td> <hr>
          <td><b>groupId:</b> id of a specific group</td>
          <td>Deletes a group, but only if it belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/groups/{groupId}/contacts/{contactId}</td>
          <td>
            <b>groupId:</b> id of a specific group <hr>
            <b>contactId:</b> id of a specific contact
          </td>
          <td>Removes a contact by a group, but only if the group belongs to the user who is making the request</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>When a resource is successfully deleted, <b>204 No Content</b> is returned.</p>

  <h3>Resources properties</h3>
  <p>The json representation of all the resources uses the properties name as the key. When providing a resource definition it must not be put into the root of the json document, instead, it must be assigned to a field that has the same name of the resource (e.g. if creating a group, the group definition must be assigned to a field named <code>group</code>), or the plural name if a collection of resources is being provided (e.g. if adding phone numbers to a contact, every phone number definition must be put into an array and the array must be assigned to a field named <code>phoneNumbers</code>).</p>

  <h4>User</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <th>Property</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>email</td>
          <td>The emaild used to identify the user</td>
        </tr>
        <tr>
          <td>password</td>
          <td>The digest calculated with a SHA-256 function of the password used by the user to authenticate</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>Except when registering a new user, <code>password</code> field can be omitted.</p>

  <h4>PhoneNumber</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <th>Property</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>id</td>
          <td>The resource identifier of the phone number</td>
        </tr>
        <tr>
          <td>countryCode</td>
          <td>The national identifier of the phone number (e.g. for Italy it is +39)</td>
        </tr>
        <tr>
          <td>areaCode</td>
          <td>Reading the phone number from left to right this represent the first 3 digits</td>
        </tr>
        <tr>
          <td>prefix</td>
          <td>Reading the phone number from left to right this represent the second group of 3 digits</td>
        </tr>
        <tr>
          <td>phoneLine</td>
          <td>The last 4 digits of the phone number</td>
        </tr>
        <tr>
          <td>description</td>
          <td>A word or a short phrase that describes the phone number (e.g. Office, school, house, ...)</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>The country code must be provided without the <code>+</code> character.</p>
  <p>When creating a phone number all the fields must be provided except for <code>id</code> and <code>description</code>.</p>

  <h4>Email</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <th>Property</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>email</td>
          <td>The email address</td>
        </tr>
        <tr>
          <td>description</td>
          <td>A word or a short phrase that describes the email address (e.g. Office, school, personal ...)</td>
        </tr>
      </tbody>
    </table>
  </div>

  <h4>Contact</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <th>Property</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>id</td>
          <td>The resource identifier of the contact</td>
        </tr>
        <tr>
          <td>firstName</td>
          <td>The first name of the contact</td>
        </tr>
        <tr>
          <td>familyName</td>
          <td>The family name or surname of the contact</td>
        </tr>
        <tr>
          <td>secondName</td>
          <td>The second name of the contact</td>
        </tr>
        <tr>
          <td>owner</td>
          <td>The user who this contact belongs to</td>
        </tr>
        <tr>
          <td>associatedUser</td>
          <td>If this contact is the representation of a user this property holds the user's credentials</td>
        </tr>
        <tr>
          <td>phoneNumbers</td>
          <td>An array that holds all the phone numbers associated to the contact</td>
        </tr>
        <tr>
          <td>emails</td>
          <td>An array that holds all the email addressed associated to the contact</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>To the fields <code>owner</code> and <code>associateUser</code> it must be assigned an object of type <code>User</code>. In the same way to <code>phoneNumbers</code> and <code>emails</code> it must be assigned and array of objects of type <code>PhoneNumber</code> and <code>Email</code>.</p> 
  <p>When creating a new contact <code>firstName</code>, <code>familyName</code> and <code>owner</code> fields must be specified, while the other can be null. Of course, when creating a new user the <code>id</code> field doesn't have a value.</p>

  <h4>Group</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <th>Property</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>id</td>
          <td>The resource identifier of the group</td>
        </tr>
        <tr>
          <td>name</td>
          <td>The name of the group</td>
        </tr>
        <tr>
          <td>owner</td>
          <td>The user who has created this group that is the user who this group belongs to</td>
        </tr>
        <tr>
          <td>contacts</td>
          <td>An array that holds all the contacts of the group</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>As before, <code>owner</code> is an object of type <code>User</code> and <code>contacts</code> is an array of objects of type <code>Contact</code>.</p>
  <p>When creating a group values for <code>name</code> and <code>owner</code> fields must be provided.</p>

  <h4>Call</h4>
  <div class="centered">
    <table class="resource_properties">
      <thead>
        <tr>
          <td>Property</td>
          <td>Description</td>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>Id</td>
          <td>The resource identifier of the call</td>
        </tr>
        <tr>
          <td>callerNumber</td>
          <td>The numbers that has made the call</td>
        </tr>
        <tr>
          <td>callerContact</td>
          <td>The contact who has made the call</td>
        </tr>
        <tr>
          <td>calledNumber</td>
          <td>The number that has been called</td>
        </tr>
        <tr>
          <td>calledContact</td>
          <td>The contact who has been called</td>
        </tr>
        <tr>
          <td>timestamp</td>
          <td>Date and time in which the call has been made</td>
        </tr>
        <tr>
          <td>duration</td>
          <td>Duration in seconds of the call</td>
        </tr>
      </tbody>
    </table>
  </div>

  <h3>Special requests</h3>
  <h4>Login</h4>
  <p>When the user wants to send a request to just test its credentials it has to send a POST request in which the user's credentials are provided via the <b>Authorization</b> header and the body must contain this json code:</p>

  ```json
  {
    "justLogin": true
  }
  ```

  <h4>Registration</h4>
  <p>When registering a new user, credentials don't have to be inserted into the <b>Authorization</b> header, instead, In the body of the request, it must be defined a new contact that has the user's credentials as value for the <code>owner</code> and <code>associatedUser</code> fields.</p>

  ```json
  {
    "contact": {
      "firstName": "Gennaro",
      "familyNam": "Rossi",
      "secondName": null,
      "owner": {
        "email": "gennaro.rossi@mail.com",
        "password": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
      },
      "associatedUser": {
        "email": "gennaro.rossi@mail.com",
        "password": "5e884898da28047151d0e56f8dc6292773603d0d6aabbdd62a11ef721d1542d8"
      },
      "phoneNumber": [],
      "emails":[]
    }
  }
  ```

  <h3>Errors</h3>
  <h4>Possible status codes</h4>
  <div class="centered">
    <table id="status_codes">
      <thead>
        <tr>
          <th>Status code</th>
          <th>Meaning</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>200 OK</td>
          <td>In get requests it indicates that the resource has been returned, while when logging in it indicates that the credentials are valid</td>
        </tr>
        <tr>
          <td>201 Created</td>
          <td>In POST requests it indicates that the resource has been successfully created</td>
        </tr>
        <tr>
          <td>204 No Content</td>
          <td>In PUT and DELETE requests it indicates that the resource has been sucessfully updated or deleted</td>
        </tr>
        <tr>
          <td>400 Bad Request</td>
          <td>The request has not been accepted due to an error</td>
        </tr>
        <tr>
          <td>401 Unauthorized</td>
          <td>The credentials are missing or wrong or the resource belongs to another user so the request cannot be accepted</td>
        </tr>
        <tr>
          <td>404 Not Found</td>
          <td>The request cannot be accepted because it requires one or more resources that don't exist</td>
        </tr>
        <tr>
          <td>409 Conflict</td>
          <td>In PUT and DELETE requests it indicates that the rescource has not been updated or deleted due to an error</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>If the status code indicates that the request has not been successfully resolved an error message will be provided as the body of the response</p>

  <h4>Error message structure</h4>
  <div class="centered">
    <table class="error_structure">
      <thead>
        <tr>
          <th>Field</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>type</td>
          <td>It can be <code>error</code>, <code>warning</code> or <code>info</code> and it indicates the severity of the error</td>
        </tr>
        <tr>
          <td>code</td>
          <td>A numeric value that indicates the type of error returned</td>
        </tr>
        <tr>
          <td>title</td>
          <td>A brief description of the kind of error that has occured</td>
        </tr>
        <tr>
          <td>message</td>
          <td>A more detailed description of the error and probable causes</td>
        </tr>
        <tr>
          <td>suggestion</td>
          <td>A suggestion to resolve the error or find out its causes</td>
        </tr>
        <tr>
          <td>data</td>
          <td>Additional useful information to resolve the error</td>
        </tr>
      </tbody>
    </table>
  </div>
  <p>Every error message has the same structure, only the <code>data</code> filed may not be present because it is used only in certain circumstances.</p>

  <h4>Error codes</h4>
  <div class="centered">
    <table id="error_codes">
      <thead>
        <tr>
          <th>Error code</th>
          <th>Error name</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
        <tr>
          <td>1</td>
          <td>Registration Failure</td>
          <td>An error occured while trying to register a new user</td>
        </tr>
        <tr>
          <td>2</td>
          <td>Duplicated User</td>
          <td>You tried to register a new user, but the email is already assigned to another one</td>
        </tr>
        <tr>
          <td>3</td>
          <td>Missing Authenticaction</td>
          <td>Credentials have not been provided, or the <code>Authorization</code> header type is not <code>Basic</code></td>
        </tr>
        <tr>
          <td>9</td>
          <td>Failed Authentication</td>
          <td>The credentials provided are wrong</td>
        </tr>
        <tr>
          <td>10</td>
          <td>Credentials Mismatch</td>
          <td>The credentials used to authenticate are different from the one indicated in the <code>owner</code> or <code>associatedUser</code> field of the resource</td>
        </tr>
        <tr>
          <td>8</td>
          <td>Invalid Content Type</td>
          <td>The <code>Content-Type</code> header is wrong</td>
        </tr>
        <tr>
          <td>18</td>
          <td>Invalid character encoding</td>
          <td>The charset specified int the <code>Content-Type</code> header is wrong</td>
        </tr>
        <tr>
          <td>19</td>
          <td>Missing Content-Type header</td>
          <td>The <code>Content-Type</code> header has not been specified</td>
        </tr>
        <tr>
          <td>4</td>
          <td>Wrong Syntax</td>
          <td>There are some syntax error in the json text or the needed data has not been provided</td>
        </tr>
        <tr>
          <td>5</td>
          <td>Insertion Failure</td>
          <td>An error has occured while trying to create a new resource</td>
        </tr>
        <tr>
          <td>6</td>
          <td>Missing URL Component</td>
          <td>The URL is incomplete</td>
        </tr>
        <tr>
          <td>7</td>
          <td>Wrong URL component</td>
          <td>One or more components of the URL are wrong or invalid</td>
        </tr>
        <tr>
          <td>11</td>
          <td>Wrong Object Id</td>
          <td>The id provided to identify the resource is invalid and cannot be used</td>
        </tr>
        <tr>
          <td>17</td>
          <td>Inacessible Or Non-existing Resource</td>
          <td>The id provided doesn't identify any resource or you don't have the authorization needed to access that resource (e.g. the resource belongs to another user)</td>
        </tr>
        <tr>
          <td>12</td>
          <td>Data Not Modifiable</td>
          <td>The resource cannot be modifies because the new resouce is identical to the old one</td>
        </tr>
        <tr>
          <td>13</td>
          <td>Data Not Modified</td>
          <td>An error has occured while trying to modify a resource</td>
        </tr>
        <tr>
          <td>14</td>
          <td>Deletion Unathorized</td>
          <td>The deletion cannot be accomplished because the resource belongs to another user</td>
        </tr>
        <tr>
          <td>15</td>
          <td>Deletion Failed</td>
          <td>An error has occured while trying to delete a resource</td>
        </tr>
        <tr>
          <td>16</td>
          <td>Deletion Not Allowed</td>
          <td>The deletion has failed because the resource cannot be deleted (e.g. the contact associated to a user cannot be deleted without deleting the user too)</td>
        </tr>
      </tbody>
    </table>
  </div>
</body>
</html>
