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
			System.out.println("Client socket successfully set up. Starting HTTP request thread...");
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
		DataOutputStream os = new DataOutputStream(socket.getOutputStream());

		// Set up input stream filters.
		BufferedReader br = new BufferedReader(
			new InputStreamReader(is));

		//Get request line
		String requestLine = br.readLine();

		//Uncomment this block to print entire HTTP request message for debugging purposes
		/*
		System.out.println();
		System.out.println(requestLine);

		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}
		*/

		//Extract filename
		StringTokenizer tokens = new StringTokenizer(requestLine);
		tokens.nextToken();
		String fileName = tokens.nextToken();

		fileName = "." + fileName;

		FileInputStream fis = null;
		boolean fileExists = true;
		try{
			fis = new FileInputStream(fileName);
		}
		catch(FileNotFoundException e){
			fileExists = false;
		}

		System.out.println("Requested file is " + fileName);
		if(fileExists) System.out.println("Requested file exists");
		else System.out.println("Requested file does not exist");

		//Set up response headers
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;
		if (fileExists) {
			statusLine = "HTTP/1.1 200 OK";
			contentTypeLine = "Content-type: " + 
				contentType( fileName ) + CRLF;
		} 
		else {
			statusLine = "HTTP/1.1 404 Not Found";
			contentTypeLine = "No contents";
			entityBody = "<HTML>" + 
				"<HEAD><TITLE>Not Found</TITLE></HEAD>" +
				"<BODY>Sorry, WebServer.java could not find the requested file<br>" + 
				"The requested file was  " + fileName + 
				"</HTML>";
		}	

		//Write response headers to output stream 
		System.out.println("Status line: " + statusLine);
		System.out.println("Content line: " + contentTypeLine);
		os.writeBytes(statusLine);
		os.writeBytes(contentTypeLine);
		os.writeBytes(CRLF);

		//Send requested file
		if(fileExists){
			sendBytes(fis, os);
			fis.close();
		}	
		else{
			os.writeBytes(CRLF);
			os.writeBytes(entityBody);
			//os.writeBytes(CRLF);
		}

		is.close();
		os.close();
		socket.close();
	}

	//Write requested file to socket's output stream
	private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception{
	   // Construct a 1K buffer to hold bytes on their way to the socket.
	   byte[] buffer = new byte[1024];
	   int bytes = 0;

	   // Copy requested file into the socket's output stream.
	   while((bytes = fis.read(buffer)) != -1 ) {
	      os.write(buffer, 0, bytes);
	   }
	}

	//Return appropriate value for Content-Type response header for given fileName
	private static String contentType(String fileName){
		if(fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return "text/html";
		}
		if(fileName.endsWith(".gif")) {
			return "image/gif";
		}
		if(fileName.endsWith(".jpg")) {
			return "image/jpeg";
		}
		return "application/octet-stream";
	}


}

