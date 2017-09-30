import java.io.*;
import java.net.*;

public class clt {
  private static Socket client;
	private static ServerSocket ListeningSocket;
	private static Socket ClientSocket;
	private static DataInputStream cmdIn;
	private static DataOutputStream cmdOut;
	private static DataOutputStream dataOut;
	private static BufferedReader input;
	private static FileOutputStream fileOut;
	private static FileInputStream fileIn;
	private static String[] cmd;
	private static String msg;

	public static void main(String args[]) throws UnknownHostException, IOException {
		if (args.length != 3) {
			System.out.println("Error : java client <host> <port> <data socekt port>");
			System.exit(0);
		}
		String server = args[0];
		int svrport = Integer.parseInt(args[1]);
		int dataport = Integer.parseInt(args[2]);

		cmdSocketStart(server, svrport);
		cmdOut = new DataOutputStream(client.getOutputStream());
		// Send command to server

		// send servername for data socket to connect to
		cmdOut.writeUTF(server);
		dataSocketStart(dataport);

		// Read commands from client
		input = new BufferedReader(new InputStreamReader(System.in));
		command();
		// Store input to string
		msg = input.readLine();
		cmdIn = new DataInputStream(ClientSocket.getInputStream());

		parseFileName();

		cmdOut.writeUTF(msg);

		dataOut = new DataOutputStream(ClientSocket.getOutputStream());
		// if GET command is used
		while (msg.toLowerCase().contains("exit") != true) {
			if (msg.toLowerCase().contains("get")) {
				System.out.println("GET FUNCTION CALLED");
				GET();
				// msg = null;

			} else if (msg.toLowerCase().contains("send")) {
				System.out.println("SEND FUNCTION CALLED");
				SEND();
				// msg = null;

			}
			command();
			msg = input.readLine();
			cmdOut.writeUTF(msg);
			parseFileName();
		}
		System.out.println("disconnecting");
		// disconnect();
	}

	public static void cmdSocketStart(String host, int port) throws UnknownHostException, IOException {
		// creates a socket connecting to the server
		client = new Socket(host, port);
		System.out.println("Connected to " + host + " and port " + port);
	}

	public static void dataSocketStart(int port) throws IOException {
		// second socket
		ListeningSocket = new ServerSocket(port);
		System.out.println("waiting for connection on port " + port);
		ClientSocket = ListeningSocket.accept();
		System.out.println("CONNECTED");
	}

	public static void GET() throws IOException {
		// creates a server socket

		// if file exists
		msg = cmdIn.readUTF();
		if (msg.matches("0")) {
			System.out.println("File Doesn't Exist");
		} else {
			try {
				System.out.println("File is transfering");
				fileOut = new FileOutputStream(cmd[1]);
				// size of file
				msg = cmdIn.readUTF();
				byte[] buffer = new byte[Integer.parseInt(msg)];
				cmdIn.read(buffer);
				fileOut.write(buffer);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void SEND() throws IOException {

		File myFile = new File(cmd[1]);
		System.out.println("does file exist?" + myFile.exists());
		// if file exists send file to server
		if (myFile.exists() == true) {
			try {
				cmdOut.writeUTF("1");
				System.out.println("File name:" + cmd[1]);
				fileIn = new FileInputStream(cmd[1]);
				int size = (int) myFile.length();
				System.out.println("FILE SIZE IN BYTES" + size);
				byte[] buffer = new byte[size];
				cmdOut.writeUTF(Integer.toString(size));
				System.out.println("sending size of file to server");
				fileIn.read(buffer);
				dataOut.write(buffer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			// if file doesn't exist let server know and print line
			cmdOut.writeUTF("0");
			System.out.println("File Does Not Exist");
		}

	}

	public static void command() {
		System.out.println("get <filename.extension> - Retreive a file from the server");
		System.out.println("send <filename.extension> - Send a file to the server");
		System.out.println("exit - close the connection");
		System.out.println("Please enter a command: ");
	}

	public static void parseFileName() {
		cmd = msg.trim().split(" ");
	}

	public static void disconnect() throws IOException {
		System.out.println("Disconnecting");
		client.close();
		ListeningSocket.close();
		ClientSocket.close();
		cmdIn.close();
		cmdOut.close();
		dataOut.close();
		input.close();
		fileOut.close();
		fileIn.close();

	}
}
