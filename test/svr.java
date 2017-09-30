import java.io.*;
import java.net.*;

public class svr {
<<<<<<< HEAD
  private static ServerSocket ListeningSocket;
  private static Socket ClientSocket;
  private static Socket client;
  private static InputStream ofs;
  private static DataInputStream in;
  private static OutputStream outsvr;
  private static DataOutputStream out;
  private static DataInputStream ins;
  private static String msg;
  private static String[] cmd;

  public static void main(String args[]) throws IOException, ClassNotFoundException {

    if(args.length != 2)
    {
        System.out.println("Error : java server <port> <port for data>");
        System.exit(0);
    }
    int cltport = Integer.parseInt(args[0]);
    int dataport = Integer.parseInt(args[1]);

    cmdSocketStart(cltport);
    //get the host name for the data socket
    ofs = ClientSocket.getInputStream();
    in = new DataInputStream(ofs);

    msg = in.readUTF();
    System.out.println("server name: " + msg);
    dataSocketStart(msg,dataport);



    // store message from client
    msg = in.readUTF();
    System.out.println("message from client:" + msg);
    cmd = msg.trim().split(" ");
    outsvr = client.getOutputStream();
    out = new DataOutputStream(outsvr);
    ins = new DataInputStream(client.getInputStream());

    System.out.println(msg.contains("exit"));
    //while (msg.toLowerCase().contains("exit") != true){
      if (cmd[0].equalsIgnoreCase("GET")) {
        GET();
      } else if (cmd[0].equalsIgnoreCase("SEND")) {
        SEND();

      }
    //}
    disconnect();

  }

  public static void cmdSocketStart(int port) throws UnknownHostException, IOException {
    System.out.println("Connecting to port "+ port);
    ListeningSocket = new ServerSocket(port);
    // ServerSocket svr = new ServerSocket(7005);
    System.out.println("Waiting for client to connect!");
    ClientSocket = ListeningSocket.accept();
    System.out.println("Client has connected to the server");
  }

  public static void dataSocketStart(String host,int port) throws IOException {
    client = new Socket(host, port);
    System.out.println("Connected to "+ host+" and port "+port);
  }

  public static void GET() throws IOException {
    File myFile = new File(cmd[1]);
    boolean exists = myFile.exists();
    System.out.println("FILE EXISTS?" + myFile.exists());
    if ((myFile.exists()) == true) {
      out.writeUTF("1");
      System.out.println("File Exists");
      int size = (int) myFile.length();
      System.out.println("File name:" + cmd[1]);
      FileInputStream fis = new FileInputStream(cmd[1]);
      byte[] buffer = new byte[size];
      // send size of file to server
        out.writeUTF(Integer.toString(size));
      // While stream has data write to buffer
        fis.read(buffer);
        out.write(buffer);
    } else {
        out.writeUTF("0");
      System.out.println("File Doesn't Exist");
    }
  }

  public static void SEND() throws IOException {
    msg = in.readUTF();
    System.out.println("message from client");
    if(msg.matches("1")) {
      try {
        FileOutputStream file1 = new FileOutputStream("totoro1.jpg");
        msg = in.readUTF();
        byte[] buffer = new byte[Integer.parseInt(msg)];
        System.out.println("MESSAGE FROM CLIENT FILE SIZE= " + Integer.parseInt(msg));
        ins.read(buffer);
        file1.write(buffer);
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
    }

  }

  public static void disconnect() throws IOException {
    System.out.println("Closing connection");
    ListeningSocket.close();
    ClientSocket.close();
    client.close();
    ofs.close();
    in.close();
    outsvr.close();
    out.close();
    ins.close();
  }
=======
  private static ServerSocket svr;
	private static Socket socket;
	private static Socket clt;

	public static void main(String args[]) throws IOException, ClassNotFoundException {
		cmdSocketStart();
		dataSocketStart();

		// clients input stream
		InputStream ofs = socket.getInputStream();
		DataInputStream in = new DataInputStream(ofs);
		// store message from client
		String msg = in.readUTF();
		System.out.println("message from client:" + msg);

		OutputStream outsvr = clt.getOutputStream();
		DataOutputStream out = new DataOutputStream(outsvr);

		if (msg.matches("GET")) {

			String file = "totoro.jpg";
			File myFile = new File("totoro.jpg");
			int siz = (int) myFile.length();
			System.out.println("File name:" + file);

			FileInputStream fis = new FileInputStream(file);
			byte[] buffer = new byte[siz];

			out.writeUTF(Integer.toString(siz));
			// copy file to output
			while (fis.read(buffer) > 0) {
				out.write(buffer);
			}
		} else if (msg.matches("SEND")) {

			try {
				DataInputStream ins = new DataInputStream(clt.getInputStream());
				FileOutputStream file1 = new FileOutputStream("totoro1.jpg");
				msg = in.readUTF();

				byte[] buff = new byte[Integer.parseInt(msg)];
				int filesize = Integer.parseInt(msg);
				System.out.println("MESSAGE FROM CLIENT FILE SIZE= " + Integer.parseInt(msg));
				int read = 0;
				int totalRead = 0;
				int remaining = filesize;

				while ((read = ins.read(buff, 0, Math.min(buff.length, remaining))) > 0) {
					totalRead += read;
					remaining -= read;
					System.out.println("read " + totalRead + " bytes.");
					file1.write(buff, 0, read);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public static void cmdSocketStart(/* String host, int port */) throws UnknownHostException, IOException {
		System.out.println("Connecting to port 7005");
		svr = new ServerSocket(4444);
		// ServerSocket svr = new ServerSocket(7005);
		System.out.println("======================================");
		System.out.println("Waiting for client to connect!");
		socket = svr.accept();
		System.out.println("======================================");
		System.out.println("Client has connected to the server");
		System.out.println("======================================");
	}

	public static void dataSocketStart(/* int port */) throws IOException {
		clt = new Socket("127.0.0.1", 7005);
		System.out.println("Connected to 127.0.0.1 and port 4444!");
		System.out.println("======================================");
	}

	public static void disconnect() throws IOException {
		System.out.println("Closing connection");
		// in.close();
		// out.close();
		svr.close();
		socket.close();
	}
>>>>>>> b3e3918cc874ccb9e1d13c8c4ac2bb22f065205f
}
