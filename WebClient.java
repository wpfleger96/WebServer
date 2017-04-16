import java.util.*;
import java.io.*;
import java.net.*;

public final class WebClient{

	public static void main(String[] args){
		if(args.length != 2){
			System.out.println("Usage: java WebClient hostname port:");
			System.exit(1);
		}

		String hostname = args[0];
		int port = Integer.parseInt(args[1]);
		Socket socket = null;

		try{
			socket = openClientSocket(hostname, port);
		}
		catch(Exception e){
			System.out.println("Error creating client socket");
		}




	}

	public static Socket openClientSocket(String hostname, int port){
		Socket socket = null;

		try{
			InetAddress inetAddr = InetAddress.getByName(hostname);
			SocketAddress sockAddr = new InetSocketAddress(inetAddr, port);

			socket = new Socket();

			socket.connect(sockAddr);

		}
		catch(Exception e){
			System.out.println("Error in socket setup function");
		}

		return socket;
	}


}