SMS Message System With Socket

Users will send each other plain text SMS messages using the client module. The server module will mediate the storage and transmission of messages.

Clients will always send messages and initiate the dialog. If it is the server, it will respond to the client appropriately.

The protocol of the simple SMS system will consist of the following messages;

- BEGIN_SESSION \<username> : The client uses this message to log in. The server replies by notifying the number of SMS pending in the user's mailbox.

Server: "You have \<N> SMS messages"

  
- SEND_SMS \<receiving user> \<SMS message> :
    The client uses this message to send SMS.

Server: "Your message has been received"


- POP_SMS : The client uses this message to retrieve a pending SMS message. The server returns the first pending SMS message and deletes it from the pending messages list.

Server: <sending user> <SMS message>

If there are no pending SMS messages:

Server: "You do not have an SMS message"


- END_SESSION : The client uses this message to log out. After this message, the client closes the socket connection.


- \<Invalid message> : When the client sends a message other than the above, the server replies with "Message not understood".


The server will store the sent SMS messages until they are delivered to the recipient. However, since the messages are stored in data structures such as List, Map, the messages will be deleted when the server application is closed.
