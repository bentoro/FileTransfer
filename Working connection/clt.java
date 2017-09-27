import java.io.*;
import java.net.*;

public class clt {
  public static void main(String args[]) throws UnknownHostException, IOException {

		//creates a socket connecting to the server
	Socket socket = new Socket("LOCALHOST",4444);
  //Socket socket = new Socket("192.168.0.17",7005);
System.out.println("======================================");
	System.out.println("Connected to 127.0.0.1 and port 4444!");
System.out.println("======================================");



//Get input from user from command line
  BufferedReader input = new BufferedReader (new InputStreamReader(System.in));
  //Store user message into string
  String msg = input.readLine();
System.out.println("Entered Message: " + msg);
// Write data to socket
  OutputStream outsvr = socket.getOutputStream();
  DataOutputStream out = new DataOutputStream(outsvr);
//send message to the server socket
out.writeUTF(msg);
//displays message in command line


//Read data from server socket
  InputStream svrin = socket.getInputStream();
  DataInputStream in = new DataInputStream(svrin);
//display what the server echos
System.out.println("======================================");
  System.out.println(in.readUTF());
if (msg.matches("GET")) {
	System.out.println("Connecting to port 7005");
	ServerSocket svr1 = new ServerSocket(7005);
	System.out.println("======================================");
	System.out.println("Waiting for client to connect!");
	Socket socket1 = svr1.accept();
	System.out.println("======================================");
	System.out.println("Client has connected to the server");
	System.out.println("======================================");
}

  /*String file = "totoro.jpg";
System.out.println("File name:" + file);
  FileInputStream fis = new FileInputStream(file);
  byte[] buffer = new byte[4096];
//copy file to output
  while (fis.read(buffer)>0) {
	  out.write(buffer);
  }*/



//close connection, streams and socket
  System.out.println("disconnecting");
  out.close();
  in.close();
  socket.close();


	//Read server reply

}
}
