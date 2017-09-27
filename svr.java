import java.io.*;
import java.net.*;

public class svr {
  public static void main(String args[]) throws IOException, ClassNotFoundException {
		  System.out.println("Connecting to port 7005");
		  ServerSocket svr = new ServerSocket(7005);
		  System.out.println("======================================");
		  System.out.println("Waiting for client to connect!");
		  Socket socket = svr.accept();
		  System.out.println("======================================");
		  System.out.println("Client has connected to the server");
		  System.out.println("======================================");
		  //clients input stream



		  //Reading input
		  DataInputStream in = new DataInputStream(socket.getInputStream());
		  String msg = in.readUTF();
		  String first = msg.substring(0, 2);
		  /*System.out.println("Substring:" + first);
		  String get = "get";
		  if (first.equalsIgnoreCase(get)) {
		    System.out.println("GET command");
		  }*/

		  //sending output
		  System.out.println("======================================");
		  System.out.println("Message from client: " + msg);
		  System.out.println("======================================");
		  DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		  System.out.println("======================================");
		  out.writeUTF("server:" + msg);

		  if (msg.matches("GET")) {
			  Socket socket1 = new Socket("192.168.0.16",7005);
			  DataInputStream in1 = new DataInputStream(socket1.getInputStream());
			  DataOutputStream out1 = new DataOutputStream(socket1.getOutputStream());
			  System.out.println("CONNECTED");
		  }


		  /*
		  FileOutputStream file = new FileOutputStream("totoro1.jpg");
		  byte [] buffer = new byte[4096];
		  int filesize = 15123;
		  int read = 0;
		  int totalRead = 0;
		  int remaining = filesize;

		  while((read = in.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
		    totalRead += read;
		    remaining -= read;
		    System.out.println("read " + totalRead + " bytes.");
		    file.write(buffer, 0, read);
		  }*/


		  System.out.println("Closing connection");
		  in.close();
		  out.close();
		  svr.close();
		  socket.close();
		}
		}
