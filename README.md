<DOCTYPE html>
<html>
<head>
  <style>
    .centered {
      display: flex;
      flex-direction: row;
      justify-content: center;
    }
    table {
      width: 100%;
    }
  </style>
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
  <p>When data needs to be transfered into the HTTP request body (i.e. POST, PUT requests) it must be defined using the <a href="https://www.json.org" target="_blank">JSON</a> language.</p>

  <h3>Authentication</h3>
  <p>To make a request the user must be authenticated by the web service server. In order to do so, the user must provide its credentials into the HTTP <b>Authorization</b> header. The header type is Basic and the credentials have to be specified after the "Basic" string separeted by a column (username:password). This string (without "Basic") must be encoded with a base 64 encoding and the password mustn't be sent in clear, but its digest has to be provided, instead.</p>

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
    <table id="get_requests">
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
  <p>If the id provided doesn't identify any resource or if the resource identified cannot be returned (i.e. if the resource belongs to another user) then the response status code will be <b>404 Not Found</b>, otherwise the resource's JSON encoding is returned and the status code will be <b>200 OK</b>.</p>
  
  <h3>POST requests</h3>
  <div class="centered">
    <table id="post_requests">
      <thread>
        <tr>
          <th>Path</th>
          <th>Parameters</th>
          <th>Result</th>
        </tr>
        <tr>
          <td>/user/</td>
          <td>Only login request</td>
          <td>Test the users credentials</td>
        </tr>
      </thread>
    </table>
  </div>
</body>
</html>