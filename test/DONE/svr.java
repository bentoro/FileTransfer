import java.io.*;
import java.net.*;

public class svr {
  private static ServerSocket listeningSocket;
	private static Socket clientSocket;
	private static Socket client;
	private static InputStream cmdins;
	private static DataInputStream cmdIn;
	private static OutputStream outsvr;
	private static DataOutputStream cmdOut;
	private static DataInputStream dataIn;
	private static String msg;
	private static String[] cmd;

	public static void main(String args[]) throws IOException, ClassNotFoundException {

		if (args.length != 2) {
			System.out.println("Error : java server <port> <port for data>");
			System.exit(0);
		}
		int cltport = Integer.parseInt(args[0]);
		int dataport = Integer.parseInt(args[1]);

		cmdSocketStart(cltport);
		// get the host name for the data socket
		cmdins = clientSocket.getInputStream();
		cmdIn = new DataInputStream(cmdins);

		msg = cmdIn.readUTF();
		System.out.println("server name: " + msg);
		dataSocketStart(msg, dataport);

		// store message from client
		msg = cmdIn.readUTF();
		System.out.println("message from client:" + msg);
		cmd = msg.trim().split(" ");
		outsvr = client.getOutputStream();
		cmdOut = new DataOutputStream(outsvr);
		dataIn = new DataInputStream(client.getInputStream());

		System.out.println(msg.contains("exit"));
		while (msg.toLowerCase().contains("exit") != true) {
			if (msg.toLowerCase().contains("get")) {
        System.out.println("GET FUNCTION CALLED");
				GET();
        //msg = null;
			} else if (msg.toLowerCase().contains("send")) {
        System.out.println("SEND FUNCTION CALLED");
				SEND();
        //msg = null;
			}
			msg = cmdIn.readUTF();
      parseFileName();
		}
		System.out.println("disconnecting");
		// disconnect();

	}

	public static void cmdSocketStart(int port) throws UnknownHostException, IOException {
		System.out.println("Connecting to port " + port);
		listeningSocket = new ServerSocket(port);
		System.out.println("Waiting for client to connect!");
		clientSocket = listeningSocket.accept();
		System.out.println("Client has connected to the server");
	}

	public static void dataSocketStart(String host, int port) throws IOException {
		client = new Socket(host, port);
		System.out.println("Connected to " + host + " and port " + port);
	}

	public static void GET() throws IOException {
		File myFile = new File(cmd[1]);
		boolean exists = myFile.exists();
		System.out.println("FILE EXISTS?" + myFile.exists());
		if ((myFile.exists()) == true) {
			cmdOut.writeUTF("1");
			System.out.println("File Exists");
			int size = (int) myFile.length();
			System.out.println("File name:" + cmd[1]);
			FileInputStream fis = new FileInputStream(cmd[1]);
			byte[] buffer = new byte[size];
			// send size of file to server
			cmdOut.writeUTF(Integer.toString(size));
			// While stream has data write to buffer
			fis.read(buffer);
			cmdOut.write(buffer);
		} else {
			cmdOut.writeUTF("0");
			System.out.println("File Doesn't Exist");
		}
	}

	public static void SEND() throws IOException {
		msg = cmdIn.readUTF();
		System.out.println("message from client");
		if (msg.matches("1")) {
			try {
				FileOutputStream file1 = new FileOutputStream(cmd[1]);
				msg = cmdIn.readUTF();
				byte[] buffer = new byte[Integer.parseInt(msg)];
				System.out.println("MESSAGE FROM CLIENT FILE SIZE= " + Integer.parseInt(msg));
				dataIn.read(buffer);
				file1.write(buffer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
		}

	}

	public static void command() {
		System.out.println("get <filename.extension> - Retreive a file from the server");
		System.out.println("send <filename.extension> - Send a file to the server");
		System.out.println("exit - close the connection");
		System.out.println("Please enter a command: ");
	}
  public static void parseFileName(){
    cmd = msg.trim().split(" ");
  }
	public static void disconnect() throws IOException {
		System.out.println("Closing connection");
		listeningSocket.close();
		clientSocket.close();
		client.close();
		cmdins.close();
		cmdIn.close();
		outsvr.close();
		cmdOut.close();
		dataIn.close();
	}
}
