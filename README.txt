Adam Cankaya, cankayaa@onid.oregonstate.edu
CS 372 Project 1
Due Feb 8, 2015

Run both client and server on flip.engr.oregonstate.edu

Start Java server by compiling and then providing a port on the command line:
    javac chatserver.java
    java chatserver <PORTNUM>

Or start the Python server by providing a port number on the command line:
    python chatserver.py <PORTNUM>

Start Client and connect to server by providing hostname and port number:
    python chatclient.py <HOST> <PORTNUM>

Once connected, client sends the first message then waits for the server's response.

Either server or client can send '\quit' to end connection. Server will continue to run and wait for next connection on <PORTNUM>.

Server can be shut down by sending a SIG_INT (Ctrl+C).