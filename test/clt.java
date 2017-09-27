import java.io.*;
import java.net.*;

public class clt {
  private static Socket socket;
	private static ServerSocket socket1;
	private static Socket clt;
	public static void main(String args[]) throws UnknownHostException, IOException {

		  cmdSocketStart();
		  dataSocketStart();



		  //Read commands from client
		  BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
		  //Store input to string
		  String msg = input.readLine();

		  //Send command to server
		  OutputStream ots = socket.getOutputStream();
		  DataOutputStream out = new DataOutputStream(ots);
		  out.writeUTF(msg);



		  //if GET command is used
		  if (msg.matches("GET")){
		    //creates a server socket

		    DataInputStream in = new DataInputStream(clt.getInputStream());
		    FileOutputStream file1 = new FileOutputStream("totoro1.jpg");
		    byte [] buff = new byte[4096];
		    int filesize = 15123;
		    int read = 0;
		    int totalRead = 0;
		    int remaining = filesize;

		    while((read = in.read(buff, 0, Math.min(buff.length, remaining))) > 0) {
		      totalRead += read;
		      remaining -= read;
		      System.out.println("read " + totalRead + " bytes.");
		      file1.write(buff, 0, read);
		    }

		  } else if(msg.matches("SEND")){
			  	OutputStream ops = clt.getOutputStream();
			  	DataOutputStream outs = new DataOutputStream(ops);
			  	String file = "totoro.jpg";
			    System.out.println("File name:" + file);
			    FileInputStream fis = new FileInputStream(file);
			    byte[] buffer = new byte[4096];
			    //copy file to output
			    while (fis.read(buffer)>0) {
			      outs.write(buffer);
			    }
		}

		  /*
		  // Write data to socket
		  OutputStream outsvr = socket.getOutputStream();
		  DataOutputStream out = new DataOutputStream(outsvr);
		  String file = "totoro.jpg";
		  System.out.println("File name:" + file);
		  FileInputStream fis = new FileInputStream(file);
		  byte[] buffer = new byte[4096];
		  //copy file to output
		  while (fis.read(buffer)>0) {
		    out.write(buffer);
		  }
		  System.out.println("======================================");
		  System.out.println("Disconnecting from data transfer socket!");
		  out.close();
		  socket.close();*/

		}
		public static void cmdSocketStart(/*String host, int port*/) throws UnknownHostException, IOException {
			//creates a socket connecting to the server
			  socket = new Socket("LOCALHOST",4444);
			  //Socket socket = new Socket("192.168.0.17",7005);
			  System.out.println("======================================");
			  System.out.println("Connected to 127.0.0.1 and port 7005!");
			  System.out.println("======================================");
		}

		public static void dataSocketStart(/*int port*/) throws IOException {
			//second socket
			  socket1 = new ServerSocket(7005);
			  System.out.println("waiting for connection on port 7005");
			  clt = socket1.accept();
			  System.out.println("CONNECTED");
		}
		}
