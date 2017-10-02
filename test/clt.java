/*-------------------------------------------------------------
|    SOURCE FILE:     client.java
|
|    DATE:             Oct 1, 2017
|
|    DESIGNER:        Benedict Lo
|
|    PROGRAMMER:        Benedict Lo
|
|    NOTES:
|        This program acts as a client in a client - server
|        relationship. This client connects to 2 message queues.
|        Once connected then it will prompt the user for a
|        file path. The client will send the file path request
|        to the server. If the file doesn't exist the server
|        will echo that back. If the file does exist then the
|        server will send the contents of the file through
|        a message queue in which then the client will read
|        the mesasge queue and will echo the contents to screen
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

	public static void main(String args[]) throws UnknownHostException, IOException {
		if (args.length != 3) {
			System.out.println("Error : java client <host> <port> <data socekt port>");
			System.exit(0);
		}
		server = args[0];
		svrport = Integer.parseInt(args[1]);
		dataport = Integer.parseInt(args[2]);

    //connect to command socket
		cmdSocketStart(server, svrport);
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
    if (!msg.toLowerCase().contains("exit")){
      //start data transfer socket
      dataSocketStart(dataport);
      //while command is not exit
        while (msg.toLowerCase().contains("exit") != true) {
          if (cmd[0].toLowerCase().contains("get") == false && cmd[0].toLowerCase().contains("send") == false){
            System.out.println("Invalid command");
          } else if(cmd[0].toLowerCase().contains("get")){
            GET();
          } else if (cmd[0].toLowerCase().contains("send")) {
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
         disconnect(0);
      } else {
      disconnect(1);
    }
}

	public static void cmdSocketStart(String host, int port) throws UnknownHostException, IOException {
    System.out.println("------------------------------------------------------");
		// creates a socket connecting to the server
		client = new Socket(host, port);
		System.out.println("Connected to " + host + " on port " + port);
	}

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
				System.out.println("File:"+cmd[1] + " is transfering to Client(" + cltip +")");
        //create new file in stream
				fileOut = new FileOutputStream(cmd[1]);
				//receive size of file from server
				msg = dataIn.readUTF();
				byte[] buffer = new byte[Integer.parseInt(msg)];
        //read input and write file
				dataIn.read(buffer);
				fileOut.write(buffer);
        System.out.println("The size of: " + cmd[1] + " is "+ Integer.parseInt(msg)+"bytes");
        System.out.println("File:"+cmd[1]+" is done transferring to client("+cltip+")");
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

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
				System.out.println("File:" + cmd[1]+"exists and is being transferred to the server("+ server+")");
        //put file into stream
				fileIn = new FileInputStream(cmd[1]);
				int size = (int) myFile.length();
				byte[] buffer = new byte[size];
				cmdOut.writeUTF(Integer.toString(size));
				System.out.println("The size of: " + cmd[1] + " is "+ size+"bytes");
        //read file and send to server
				fileIn.read(buffer);
				dataOut.write(buffer);
        System.out.println("File: " + cmd[1] + " has sucessfully sent to the server(" + server+")");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// if file doesn't exist let server know and print line
			cmdOut.writeUTF("0");
			System.out.println("File:" +cmd[1]+ " does not exist");
		}
	}

	public static void command() {
    System.out.println("------------------------------------------------------");
		System.out.println("get <filename.extension> - Retreive a file from the server");
		System.out.println("send <filename.extension> - Send a file to the server");
		System.out.println("exit - close the connection");
    System.out.println("------------------------------------------------------");
		System.out.print("Please enter a command: ");
	}

	public static void parseFileName() {
		cmd = msg.trim().split(" ");
	}

	/*-----------------------------------------------------------
	|
	|    FUNCTION:    disconnect()
	|
	|                mqd_t &mqd            -    specify the message queue
	|                struct Mesg &msg    -    struct to read into
	|                unsigned int &prio     -    priority read
	|
	|
	|    DESIGNER:        Alex Zielinski
	|    PROGRAMMER:        Alex Zielinski
	|
	|    DATE:            Feb 13, 2017
	|
	|    DESCRIPTION:
	|                This function is a wrapper function to read from
	|                the message queue. It reads from a message queue
	|                and stores data to the struct passed in as an arg
	|
	|    RETURNS:
	|                -1 if the CMD ARG is not valid
	|                otherwise returns the value returned by mq_receive
	|
	|------------------------------------------------------------*/
	public static void disconnect(int t) throws IOException {

		System.out.println("Closing connection");
    //disconnect to streams and sockets
    if(t == 1){
      client.close();
      cmdOut.close();
      input.close();
    } else {
      ListeningSocket.close();
      ClientSocket.close();
      client.close();
      cmdOut.close();
      input.close();
    }
	}
}
