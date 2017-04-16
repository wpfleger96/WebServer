import java.util.*;
import java.io.*;
import java.net.*;

public final class WebServer{

	public static void main(String[]args) throws Exception{
		if(args.length != 1){
			System.err.println("Usage: java WebServer <port number>");
            System.exit(1);
		}

		int port = Integer.parseInt(args[0]);
		ServerSocket serverSocket = null;

		//Establish listen socket
		try{
			serverSocket = new ServerSocket(port);

		}
		catch(IOException e){
			System.out.println("Error establishing listen socket");
		}

		System.out.println("Server successfully listening on port " + port +"...");

		while(true){
			Socket clientSocket = null;
			try{
				clientSocket = serverSocket.accept();
			}
			catch(IOException e){
				System.out.println("Error establishing client socket");
			}
			System.out.println("Client socket successfully set up...");
			HttpRequest clientRequest = new HttpRequest(clientSocket);
			Thread thread = new Thread(clientRequest);
			thread.start();
		}
	}

}

final class HttpRequest implements Runnable{
	final static String CRLF = "\r\n";
	Socket socket;

	// Constructor
	public HttpRequest(Socket socket) throws Exception 
	{
		this.socket = socket;
	}

	// Implement the run() method of the Runnable interface.
	public void run()
	{
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println("Error processing client HTTP request");
			System.out.println(e.getMessage());
		}
	}

	private void processRequest() throws Exception
	{
		// Get a reference to the socket's input and output streams.
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();

		// Set up input stream filters.
		BufferedReader br = new BufferedReader(
			new InputStreamReader(is));

		//Get request line
		String requestLine = br.readLine();

		//Uncomment this block to print entire HTTP request message for debugging purposes
		System.out.println();
		System.out.println(requestLine);

		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}

		/*StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();

		filename = "." + fileName;

		FileInputStream fis = null;
		boolean fileExists = true;
		try{
			fis = new FileInputStream(fileName);
		}
		catch(FileNotFoundException e){
			fileExists = false;
		}

		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK";
			contentTypeLine = "Content-type: " + 
				contentType( fileName ) + CRLF;
		} 
		else {
			statusLine = "HTTP/1.1 404 NOT FOUND";
			contentTypeLine = ?;
			entityBody = "<HTML>" + 
				"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
				"<BODY>Not Found</BODY></HTML>";
		}		

		os.writeBytes(statusLine);
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);

		if(fileExists){
			sendBytes(fis, os);
			fis.close();
		}	
		else{
			os.writeBytes(entityBody4);
		}



		*/
		is.close();
		os.close();
		socket.close();
	}


}

