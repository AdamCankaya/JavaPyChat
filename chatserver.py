# Adam Cankaya, cankayaa@onid.oregonstate.edu
# CS 372 Project 1
# Due Feb 8, 2015

# References: 
#   Boiler plate socket code from CS372 class lecture videos
#   SIGINT handler help from http://zguide.zeromq.org/py:interruptimport sys

# Client-server turn based chat program
#   Run on flip.engr.oregonstate.edu
#   Compile Java server: javac chatserver.java
#   Run Java server: java chatserver <PORTNUM>
#   Run Python server: python chatserver.py <PORTNUM>
#   Run Python client: python chatclient.py <HOST> <PORTNUM>
#   Send '\quit' to end connection and close client
#   Send SIG_INT (Ctrl+C) to shut down server

import signal
import sys
from socket import *

buf_size = 2048 # 2048 bytes / 4 bytes per character = up to 512 characters per message

# Start server, listen for a client
def serverStart():
    global serverSocket, serverPort, handle, connectionSocket, addr
    handle = "MrServer"
    serverPort = int(sys.argv[1])
    serverSocket = socket(AF_INET, SOCK_STREAM)
    serverSocket.bind(('', serverPort))

# Wait for client to initiate connection, create socket and exchange introduction
def connectClient():
    global connectionSocket, addr, serverSocket, handle, buf_size
    serverSocket.listen(1)
    connectionSocket, addr = serverSocket.accept()
    response = connectionSocket.recv(buf_size)
    print response
    sentence = "%s> I'm ready to chat on port %s!" % (handle, serverPort)
    print sentence
    connectionSocket.send(sentence)

# Listen for message from client, print it, get message from server user and then send it, repeat
def talk():
    global serverSocket, handle, connectionSocket, addr, buf_size
    signal.signal(signal.SIGINT, signal_handler)
    while 1:
        response = connectionSocket.recv(buf_size)
        if "Connection closed by client" in response:
            connectionSocket.close()
            return
        else:
            print response
        sys.stdout.write(handle)
        sys.stdout.write("> ")
        sentence_raw = raw_input()
        sentence = "%s> %s" % (handle, sentence_raw)
        if "\quit" in sentence:
            connectionSocket.send("Connection closed by server")
            print "Connection closed by server"
            connectionSocket.close()
            return
        else:
            connectionSocket.send(sentence)

# Shutdown server if interupt signal (Ctrl+C) is received
def signal_handler(signum, frame):
    sys.exit("SIG_INT received, shutting down server")

# Continues to listen for new clients until SIGINT is received
serverStart()
print "Waiting on first client to connect"
while 1:
    connectClient()
    talk()
    print "Waiting on a new client connect"