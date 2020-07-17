<DOCTYPE html>
<html>
<head>
  <link rel="stylesheet" href="Style.css">
</head>
<body>
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
          <td><b>contactId:</b> the id a contact</td>
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
          <td>/user/</td>
          <td>Only login request</td>
          <td>Test the users credentials</td>
        </tr>
        <tr>
          <td>/user/</td>
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
          <td>Subsitutes one of the phone numbers of a contact with another, but only if the contact belongs to the user who is making the request</td>
        </tr>
        <tr>
          <td>/contacts/{contactId}/emails/{email}</td>
          <td>
            <b>contactId:</b> id of a specific contact <hr>
            <b>email:</b> old email<hr>
            Email definition
          </td>
          <td>Subsitutes one of the emails of a contact with another, but only if the contact belongs to the user who is making the request</td>
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
          <td>The emaild used to identify le user</td>
        </tr>
        <tr>
          <td>password</td>
          <td>The digest calculated with a SHA-256 function of the password used by the user to authenticate</td>
        </tr>
      </tbody>
    </table>
  </div>

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
          <td>Date and time in which tha call has been made</td>
        </tr>
        <tr>
          <td>duration</td>
          <td>Duration in seconds of the call</td>
        </tr>
      </tbody>
    </table>
  </div>

  <h3>JSON syntax</h3>
  <h4>Login</h4>

  ```json
  {
    "justLogin": true
  }
  ```

  <h4>User</h4>

  ```json
  {
    "user": {
      "email": "...",
      "password": "..."
    }
  }
  ```

  <p>Except when registering a new user, <code>password</code> field can be omitted.</p>
  
  <h4>Contact</h4>

  ```json
  {
    "contact": {
      "id": ...,
      "firstName": "...",
      "familyName": "...",
      "secondName": "...",
      "owner": {},
      "associatedUser": {},
      "phoneNumbers": [],
      "emails": []
    }
  }
  ```

  <p>To the fields <code>owner</code> and <code>associateUser</code> it must be assigned an object of type <code>User</code>. In the same way to <code>phoneNumbers</code> and <code>emails</code> it must be assigned and array of objects of type <code>PhoneNumber</code> and <code>Email</code>.</p> 
  <p>When creating a new contact <code>firstName</code>, <code>familyName</code> and <code>owner</code> fields must be specified, while the other can be null. Of course, when creating a new user the <code>id</code> field doesn't have a value.</p>
  
  <h4>Group</h4>

  ```json
  {
    "group": {
      "id": ...,
      "name": "...",
      "owner": {},
      "contacts": []
    }
  }
  ```

  <p>As before, <code>owner</code> is an object of type <code>User</code> and <code>contacts</code> is an array of objects of type <code>Contact</code>.</p>
  <p>When creating a group values for <code>name</code> and <code>owner</code> fields must be provided.</p>

  <h4>PhoneNumber</h4>

  ```json
  {
    "phoneNumber": {
      "id": ...,
      "countryCode": "...",
      "areaCode": "...",
      "prefix": "...",
      "phoneLine": "...",
      "description": "..."
    }
  }
  ```

  <p><code>countryCode</code> is the national identified (e.g. country code for Italy is +39) and it must be provided without the <code>+</code> character. Then reading the number from left to right, <code>areaCode</code> and <code>prefix</code> are the first 2 groups of 3 digits and <code>phoneLine</code> holds the last 4 digits.</p>

  <p>When creating a phone number all the fields must mandatorily be provided except for <code>id</code> and <code>description</code>.</p>

  <h3>Errors</h3>
  <p>If the user tries to modify a resource that doesn't belongs to him a <b>401 Unauthorized</b> status code is returned.</p>
  <p></p>If the resource has not been created due to either an authorization problem (e.g. the user tried to add contacts to a group that doesn't belong to him) or becaues the parent resource doesn't exist, the status code returned will be <b>400 bad request</b>.</pre>
</body>
</html>