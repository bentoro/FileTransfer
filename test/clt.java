import java.io.*;
import java.net.*;

public class clt {
<<<<<<< HEAD
  private static Socket client;
	private static ServerSocket ListeningSocket;
	private static Socket ClientSocket;
	private static DataInputStream in;
	private static OutputStream ots;
	private static DataOutputStream out;
	private static OutputStream ops;
	private static DataOutputStream outs;
	private static BufferedReader input;
	private static FileOutputStream file1;
	private static FileInputStream fis;
	private static String[] cmd;
	private static String msg;

	public static void main(String args[]) throws UnknownHostException, IOException {
    if(args.length != 3)
  	{
  	    System.out.println("Error : java client <host> <port> <data socekt port>");
  	    System.exit(0);
  	}
  	String server = args[0];
  	int svrport = Integer.parseInt(args[1]);
    int dataport = Integer.parseInt(args[2]);


    cmdSocketStart(server,svrport);
    ots = client.getOutputStream();
    out = new DataOutputStream(ots);
    // Send command to server

    //send servername for data socket to connect to
    out.writeUTF(server);
		dataSocketStart(dataport);

		// Read commands from client
		input = new BufferedReader(new InputStreamReader(System.in));
		// Store input to string
		msg = input.readLine();
		in = new DataInputStream(ClientSocket.getInputStream());

		cmd = msg.trim().split(" ");


		out.writeUTF(msg);

		ops = ClientSocket.getOutputStream();
		outs = new DataOutputStream(ops);
		// if GET command is used
		// while (msg.toLowerCase().contains("exit") != true){
		if (cmd[0].equalsIgnoreCase("GET")) {
			GET();
			// msg = GET();

		} else if (cmd[0].equalsIgnoreCase("SEND")) {
			SEND();
			msg = null;

		}
		// }
		// disconnect();
	}

	public static void cmdSocketStart(String host, int port) throws UnknownHostException, IOException {
		// creates a socket connecting to the server
		client = new Socket(host, port);
		System.out.println("Connected to "+host+" and port "+ port);
	}

	public static void dataSocketStart(int port) throws IOException {
		// second socket
		ListeningSocket = new ServerSocket(port);
		System.out.println("waiting for connection on port " +port);
		ClientSocket = ListeningSocket.accept();
		System.out.println("CONNECTED");
	}

	public static void GET() throws IOException {
		// creates a server socket

		// if file exists
		msg = in.readUTF();
		if (msg.matches("0")) {
			System.out.println("File Doesn't Exist");
		} else {
			try {
				System.out.println("File is transfering");
				file1 = new FileOutputStream("totoro1.jpg");
				// size of file
				msg = in.readUTF();
				byte[] buffer = new byte[Integer.parseInt(msg)];
				in.read(buffer);
				file1.write(buffer);
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void SEND() throws IOException {


		File myFile = new File(cmd[1]);
    System.out.println("does file exist?" + myFile.exists());
    //if file exists send file to server
		if (myFile.exists() == true) {
			try {
				out.writeUTF("1");
        System.out.println("File name:" + cmd[1]);
    		fis = new FileInputStream(cmd[1]);
				int size = (int) myFile.length();
				System.out.println("FILE SIZE IN BYTES" + size);
				byte[] buffer = new byte[size];
				out.writeUTF(Integer.toString(size));
				System.out.println("sending size of file to server");
				fis.read(buffer);
				outs.write(buffer);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
      //if file doesn't exist let server know and print line
			out.writeUTF("0");
			System.out.println("File Does Not Exist");
		}

	}
  public static void command(){
    System.out.println("Error : java jclient <host> <port>");
    System.out.println("");
    System.out.println("");

  }
	public static void disconnect() throws IOException {
		System.out.println("Disconnecting");
		client.close();
		ListeningSocket.close();
		ClientSocket.close();
		in.close();
		ots.close();
		out.close();
		ops.close();
		outs.close();
		input.close();
		file1.close();
		fis.close();

	}
=======
  private static Socket socket;
	private static ServerSocket socket1;
	private static Socket clt;

	public static void main(String args[]) throws UnknownHostException, IOException {

		cmdSocketStart();
		dataSocketStart();

		// Read commands from client
		BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
		// Store input to string
		String msg = input.readLine();

		// Send command to server
		OutputStream ots = socket.getOutputStream();
		DataOutputStream out = new DataOutputStream(ots);
		out.writeUTF(msg);

		// if GET command is used
		if (msg.matches("GET")) {
			// creates a server socket

			DataInputStream in = new DataInputStream(clt.getInputStream());
			FileOutputStream file1 = new FileOutputStream("totoro1.jpg");
			msg = in.readUTF();
			int filesize = Integer.parseInt(msg);
			byte[] buff = new byte[filesize];
			int read = 0;
			int totalRead = 0;
			int remaining = filesize;

			while ((read = in.read(buff, 0, Math.min(buff.length, remaining))) > 0) {
				totalRead += read;
				remaining -= read;
				System.out.println("read " + totalRead + " bytes.");
				file1.write(buff, 0, read);
			}

		} else if (msg.matches("SEND")) {
			OutputStream ops = clt.getOutputStream();
			DataOutputStream outs = new DataOutputStream(ops);

			String file = "totoro.jpg";
			System.out.println("File name:" + file);

			FileInputStream fis = new FileInputStream(file);
			File myFile = new File("totoro.jpg");

			int siz = (int) myFile.length();
			System.out.println("FILE SIZE IN BYTES" + siz);
			byte[] buffer = new byte[siz];
			out.writeUTF(Integer.toString(siz));
			System.out.println("sending size of file to server");

			// copy file to output
			while (fis.read(buffer) > 0) {
				outs.write(buffer);
			}

		}
	}

	public static void cmdSocketStart(/* String host, int port */) throws UnknownHostException, IOException {
		// creates a socket connecting to the server
		socket = new Socket("LOCALHOST", 4444);
		// Socket socket = new Socket("192.168.0.17",7005);
		System.out.println("======================================");
		System.out.println("Connected to 127.0.0.1 and port 7005!");
		System.out.println("======================================");
	}

	public static void dataSocketStart(/* int port */) throws IOException {
		// second socket
		socket1 = new ServerSocket(7005);
		System.out.println("waiting for connection on port 7005");
		clt = socket1.accept();
		System.out.println("CONNECTED");
	}
>>>>>>> b3e3918cc874ccb9e1d13c8c4ac2bb22f065205f
}
