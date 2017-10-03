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
|        This program acts as a server in a File Transfer.
|        This program connects to the server with the commands socket ,
|        and creates a listening socket for the data socket.
|
|
--------------------------------------------------------------*/

import java.io.*;
import java.net.*;

public class clt {
  private static Socket client;
	private static ServerSocket ListeningSocket;
	private static Socket ClientSocket;
	private static DataInputStream dataIn;
	private static DataOutputStream cmdOut;
	private static DataOutputStream dataOut;
	private static BufferedReader input;
	private static FileOutputStream fileOut;
	private static FileInputStream fileIn;
	private static String[] cmd;
	private static String msg;
  private static String server;
  private static int svrport;
  private static int dataport;
  private static String cltip;
  /*-----------------------------------------------------------
  |
  |    FUNCTION:    main(String args[] )
  \
  |    DESIGNER:        Benedict Lo
  |    PROGRAMMER:        Benedict Lo
  |
  |    DATE:            Oct. 2, 2017
  |
  |    DESCRIPTION:
  |                This is the main entry to the program and connects
  |                to the command socket and data socket. Gets user input and
  |                starts user input loop.
  |
  |
  |    RETURNS: VOID
  |
  |
  |------------------------------------------------------------*/
	public static void main(String args[]) throws UnknownHostException, IOException {
		if (args.length != 3) {
			System.out.println("Error : java client <host> <port> <data socket port>");
			System.exit(0);
		}
		server = args[0];
		svrport = Integer.parseInt(args[1]);
		dataport = Integer.parseInt(args[2]);

    //connect to command socket
		cmdSocketStart(server, svrport);
    dataSocketStart(dataport);

		cmdOut = new DataOutputStream(client.getOutputStream());

		// Read commands from client
		input = new BufferedReader(new InputStreamReader(System.in));
		command();
		//Store user input to string
		msg = input.readLine();
    //parse the command from input and send user input to server
		parseFileName();
		cmdOut.writeUTF(msg);

		// if command is not exit
        while (msg.toLowerCase().contains("exit") != true) {
          if (cmd[0].equalsIgnoreCase("get") == false && cmd[0].equalsIgnoreCase("send") == false){
            System.out.println("Invalid command");
          } else if(cmd[0].equalsIgnoreCase("get")){
            GET();
          } else if (cmd[0].equalsIgnoreCase("send")) {
            SEND();
          }
          //output commands
          command();
          msg = input.readLine();
          //send user input to server and parse input for next command
          cmdOut.writeUTF(msg);
          parseFileName();
        }
        //diconnect to client
         disconnect();
    }
/*-----------------------------------------------------------
|
|    FUNCTION:    cmdSocketStart()
\
|    DESIGNER:        Benedict Lo
|    PROGRAMMER:        Benedict Lo
|
|    DATE:            Oct. 2, 2017
|
|    DESCRIPTION:
|                This method creates a socket to connect for commands and connects to
|                the server.
|
|    RETURNS: VOID
|
|
|------------------------------------------------------------*/
	public static void cmdSocketStart(String host, int port) throws UnknownHostException, IOException {
    System.out.println("------------------------------------------------------");
		// creates a socket connecting to the server
		client = new Socket(host, port);
		System.out.println("Connected to " + host + " on port " + port);
	}
  /*-----------------------------------------------------------
  |
  |    FUNCTION:    dataSocketStart()
  \
  |    DESIGNER:        Benedict Lo
  |    PROGRAMMER:        Benedict Lo
  |
  |    DATE:            Oct. 2, 2017
  |
  |    DESCRIPTION:
  |                This method creates a listening socket for data and accepts
  |                connections from the server.
  |
  |
  |    RETURNS: VOID
  |
  |
  |------------------------------------------------------------*/
	public static void dataSocketStart(int port) throws IOException {
    System.out.println("------------------------------------------------------");
		//creating listening socket
		ListeningSocket = new ServerSocket(port);
		System.out.println("connecting to port  " + port);
    //accepts connection from socket
		ClientSocket = ListeningSocket.accept();
    System.out.println("Client is connected on port "+ port);
    System.out.println("------------------------------------------------------");
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
  |                This method reads from the server to see if the file
  |                exists. If the file exists the method reads the size of the |                file from the server. Reads the buffer from the server and
  |                writes the buffer to the client.
  |
  |
  |    RETURNS: VOID
  |
  |
  |------------------------------------------------------------*/
	public static void GET() throws IOException {
    System.out.println("------------------------------------------------------");
    System.out.println("-                  GET FUNCTION                      -");
    System.out.println("------------------------------------------------------");
		dataIn = new DataInputStream(ClientSocket.getInputStream());
		//store message from server
		msg = dataIn.readUTF();
    //if the file exists
		if (msg.matches("0")) {
			System.out.println("File: "+cmd[1] +" Doesn't Exist");
		} else {
			try {
				System.out.println("File: "+cmd[1] + " is transfering to Client");
        //create new file in stream
				fileOut = new FileOutputStream(cmd[1]);
				//receive size of file from server
				msg = dataIn.readUTF();
        int size = Integer.parseInt(msg);
				byte[] buffer = new byte[size];
        int read=0;
        //stores the position
        int left = size;
        //while left (the position) is greater than zero
        while((read = dataIn.read(buffer, 0, left)) > 0){
          left-=read;
          fileOut.write(buffer, 0, read);
        }
        System.out.println("The size of: " + cmd[1] + " is "+ Integer.parseInt(msg)+"bytes");
        System.out.println("File: "+cmd[1]+" is done transferring to client");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
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
  |                This method checks if a file locally exists if it exists
  |                the method puts the file into a buffer and sends it to the
  |                server.
  |
  |
  |    RETURNS: VOID
  |
  |
  |------------------------------------------------------------*/
	public static void SEND() throws IOException {
    System.out.println("------------------------------------------------------");
    System.out.println("-                 SEND FUNCTION                      -");
    System.out.println("------------------------------------------------------");
		dataOut = new DataOutputStream(ClientSocket.getOutputStream());
		File myFile = new File(cmd[1]);
		// if file exists send file to server
		if (myFile.exists() == true) {
			try {
				cmdOut.writeUTF("1");
				System.out.println("File: " + cmd[1]+" exists and is being transferred to the server");
        //create file
				fileIn = new FileInputStream(cmd[1]);
				int size = (int) myFile.length();
				byte[] buffer = new byte[size];
        //send the size of the file to the server
				cmdOut.writeUTF(Integer.toString(size));
				System.out.println("The size of: " + cmd[1] + " is "+ size+"bytes");
        //while buffer is greater than zero write file to buffer
        while (fileIn.read(buffer)>0){
          dataOut.write(buffer);
        }
        System.out.println("File: " + cmd[1] + " has sucessfully sent to the server");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// if file doesn't exist let server know and print line
			cmdOut.writeUTF("0");
			System.out.println("File: " +cmd[1]+ " does not exist");
		}
	}
  /*-----------------------------------------------------------
  |
  |    FUNCTION:    command()
  \
  |    DESIGNER:        Benedict Lo
  |    PROGRAMMER:        Benedict Lo
  |
  |    DATE:            Oct. 2, 2017
  |
  |    DESCRIPTION:
  |                This methods prints the commands of the program.
  |
  |
  |    RETURNS: VOID
  |
  |
  |------------------------------------------------------------*/
	public static void command() {
    System.out.println("------------------------------------------------------");
		System.out.println("get <filename.extension> - Retreive a file from the server");
		System.out.println("send <filename.extension> - Send a file to the server");
		System.out.println("exit - close the connection");
    System.out.println("------------------------------------------------------");
		System.out.print("Please enter a command: ");
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
    //disconnect to streams and sockets
      client.close();
      cmdOut.close();
      input.close();
      ListeningSocket.close();
      ClientSocket.close();
      client.close();
      cmdOut.close();
      input.close();
	}
}
