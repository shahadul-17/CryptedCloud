# CryptedCloud
A security layer over cloud storage service to ensure data confidentiality. Improved file sharing support, a friendly user interface, reduced computational complexity, enhanced security and removing the necessity of the server side being trusted entity are the primary goals of this model.

# CryptedCloud-Client
The client side application that performs all the complex cryptographic operations.

**Technologies/APIs/Frameworks used:**
<pre>
1) Java SE 8
2) Swing (for GUI)
3) JavaFX (for cross platform embedded web browser)
4) Google Drive API v3
5) Jackson for JSON parsing
</pre>

# CryptedCloud-Server
The server side application that handles client requests and stores information (e.g. user information, file information etc.). It provides RESTful web service to CryptedCloud-Client.

**Technologies/APIs/Frameworks used:**
<pre>
1) Spring Boot
2) Spring Data JPA with Hibernate
3) MySQL database
</pre>

# Notes
This project is done as part of my final project of MS (CSE) program in North South University (NSU). There are several bugs or lack of features that needs to be resolved.
