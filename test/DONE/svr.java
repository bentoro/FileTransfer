/*-------------------------------------------------------------
|    SOURCE FILE:     svr.java
|
|    DATE:             Oct 1, 2017
|
|    DESIGNER:        Benedict Lo
|
|    PROGRAMMER:        Benedict Lo
|
|    NOTES:
|        This program acts as a client in a File Transfer.
|        This program listens for commands with one socket,
|        and connects to the client to send data with the other
|        socket.
|
--------------------------------------------------------------*/
import java.io.*;
import java.net.*;

public class svr {
private static ServerSocket listeningSocket;
private static Socket clientSocket;
private static Socket client;
private static DataInputStream cmdIn;
private static DataOutputStream dataOut;
private static DataInputStream dataIn;
private static FileInputStream fileIn;
private static FileOutputStream fileOut;
private static String msg;
private static String[] cmd;
private static String hostName;
/*-----------------------------------------------------------
|
|    FUNCTION:    main(String args[])
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This is the main entry to the program and connects
|                to the command socket and data socket. Gets user input
|                from client and starts user input loop.
|
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void main(String args[]) throws IOException, ClassNotFoundException {
  if (args.length != 3) {
    System.out.println("Error : java server <port> <host of client> <port for data>");
    System.exit(0);
  }
  int cltport = Integer.parseInt(args[0]);
  int dataport = Integer.parseInt(args[2]);
  hostName = args[1];
  //start the commmand socket and data socket
  cmdSocketStart(cltport);
  dataSocketStart(hostName,dataport);

  cmdIn = new DataInputStream(clientSocket.getInputStream());
  // store message from client
  msg = cmdIn.readUTF();
  //parse the message for commands and filename
  parseFileName();

    //while command is not exit
      while (msg.toLowerCase().contains("exit") != true) {
        if (cmd[0].equalsIgnoreCase("get") == false && cmd[0].equalsIgnoreCase("send") == false){
          System.out.println("Invalid command");
        } else if(cmd[0].equalsIgnoreCase("get")){
          GET();
        } else if (cmd[0].equalsIgnoreCase("send")) {
          SEND();
        }
        //read message from client and parse
        msg = cmdIn.readUTF();
        parseFileName();
      }
      //diconnect to client
       disconnect();
     }
/*-----------------------------------------------------------
|
|    FUNCTION:    cmdSocketStart(int port)
\                 int port : port of the server is listening on
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method creates a listening socket for commands and accepts
|                connections from the client.
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void cmdSocketStart(int port) throws UnknownHostException, IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("Connecting to port " + port);
  //create listening socket
  listeningSocket = new ServerSocket(port);
  System.out.println("Waiting for client to connect!");
  //accept connection
  clientSocket = listeningSocket.accept();
  System.out.println("Client has connected to the server");
}
/*-----------------------------------------------------------
|
|    FUNCTION:    dataSocketStart(String host, int port)
\                 String host : the host of the data connection socket
\                 int port : port of the client
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method creates a socket for data and connects to
|                the client data socket.
|
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void dataSocketStart(String host, int port) throws IOException {
  System.out.println("------------------------------------------------------");
  //connect to the socket
  client = new Socket(host, port);
  System.out.println("Connected to " + host + " and port " + port);
}
/*-----------------------------------------------------------
|
|    FUNCTION:    GET()
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method reads input from the user
|                and takes the filename and checks if the file exists
|                . If the file exists the method sends the size of the file
|                to the client and the buffer containing the file.
|
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void GET() throws IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("-              CLIENT CALLED GET FUNCTION            -");
  System.out.println("======================================================");
  dataOut = new DataOutputStream(client.getOutputStream());
  File myFile = new File(cmd[1]);
  //check if the file exists
  boolean exists = myFile.exists();
  // if file exists else file doesn't exist
  if ((myFile.exists()) == true) {
    //tell the client that the file exists
    dataOut.writeUTF("1");
    System.out.println("File: "+cmd[1]+" exists and is sending to client");
    //store the size of the file
    int size = (int) myFile.length();
    System.out.println("File name: " + cmd[1]+" is of size " + size+ "bytes");
    //create a file
    fileIn = new FileInputStream(cmd[1]);
    byte[] buffer = new byte[size];
    // send size of file to server
    dataOut.writeUTF(Integer.toString(size));
    // While stream has data write to buffer
    while (fileIn.read(buffer)>0){
      dataOut.write(buffer);
    }
    System.out.println("File sucessfully sent to the client!");
  } else {
    dataOut.writeUTF("0");
    System.out.println("File Doesn't Exist");
  }
  System.out.println("======================================================");
}
/*-----------------------------------------------------------
|
|    FUNCTION:    SEND()
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method receives a message from the client telling the
|                server that the file exists on the client.
|                . If the file exists on the client the method receives file |                 size from the client, take the file from the buffer
|                and creates file
|
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void SEND() throws IOException {
  System.out.println("------------------------------------------------------");
  System.out.println("-              CLIENT CALLED SEND FUNCTION           -");
  System.out.println("======================================================");
  dataIn = new DataInputStream(client.getInputStream());
  //store the message from the client if file exists
  msg = cmdIn.readUTF();
  //if file exists download file else doesn't exist
  if (msg.matches("1")) {
    try {
      //create file
      fileOut = new FileOutputStream(cmd[1]);
      //message from client size of file
      msg = cmdIn.readUTF();
      int size = Integer.parseInt(msg);
      byte[] buffer = new byte[size];
      System.out.println("Receiving file from client of size: " + Integer.parseInt(msg)+"bytes");
      int read=0;
      // stores the position
      int left = size;
      //while no more bytes left to read
      while((read = dataIn.read(buffer, 0, left)) > 0){
        left-=read;
        fileOut.write(buffer, 0, read);
      }
      System.out.println("File:" + cmd[1]+" sucessfully received from client!");
    } catch (Exception e) {
      e.printStackTrace();
    }
  } else {
    System.out.println("File: "+ cmd[1]+" doesn't exist");
  }
  System.out.println("======================================================");
}
/*-----------------------------------------------------------
|
|    FUNCTION:    parseFileName()
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method reads the input of the user and parses it
|                splitting the command and the filename and storing it into
|                a string array. The command is the first word and the second
|                word is the filename.
|
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void parseFileName() {
  //parses the command and the filename by space
  cmd = msg.trim().split(" ");
}
/*-----------------------------------------------------------
|
|    FUNCTION:    disconnect()
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method disconnects all the opened sockets
|                and streams when exiting the client.
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
public static void disconnect() throws IOException {
  //close sockets and streams
  System.out.println("Closing connection");
    listeningSocket.close();
    clientSocket.close();
    cmdIn.close();
    client.close();
  }
}
