# Adam Cankaya, cankayaa@onid.oregonstate.edu
# CS 372 Project 1
# Due Feb 8, 2015

# References: 
#   Boiler plate socket code from CS372 class lecture videos

# Client-server turn based chat program
#   Run on flip.engr.oregonstate.edu
#   Compile Java server: javac chatserver.java
#   Run Java server: java chatserver <PORTNUM>
#   Run Python server: python chatserver.py <PORTNUM>
#   Run Python client: python chatclient.py <HOST> <PORTNUM>
#   Send '\quit' to end connection and close client
#   Send SIG_INT (Ctrl+C) to shut down server
import sys
from socket import *

buf_size = 2048 # 2048 bytes / 4 bytes per character = up to 512 characters per message

# Ask user to enter a handle
def getHandle():
    global handle
    raw = raw_input('Please enter your handle: ')
    while len(raw) > 10:
        raw = raw_input('Please enter a handle of 10 characters or less: ')
    handle = raw

# Connect to server host:port, print and send intro sentence, get server response and print it
def connect():
    global serverName, serverPort, clientSocket, handle, buf_size
    serverName = str(sys.argv[1])
    serverPort = int(sys.argv[2])
    print "Connecting to %s:%d..." % (serverName, serverPort)
    clientSocket = socket(AF_INET, SOCK_STREAM)
    clientSocket.connect((serverName, serverPort))
    sentence = "%s> Hello, I'd like to connect on port %d please.\n" % (handle, serverPort)
    print sentence
   # time.sleep(3)
    clientSocket.send(sentence)
    response = clientSocket.recv(buf_size)
    print response

# Query user for a message to server, send it, get server response and print it, repeat
def talk():
    global clientSocket, handle, buf_size
    while 1:
        sys.stdout.write(handle)
        sys.stdout.write("> ")
        sentence_raw = raw_input()
        sentence = "%s> %s\n" % (handle, sentence_raw)
        if "\quit" in sentence:
            clientSocket.send("Connection closed by client\n")
            print "Connection closed by client."
            clientSocket.close()
            return
        else:
            clientSocket.send(sentence)

        response = clientSocket.recv(buf_size)
        if "Connection closed by server" in response:
            clientSocket.close()
            sys.exit(response)
        else:
            print response

getHandle()
connect()
talk()