
package evans.dave.androtag.server;
import java.io.*; 
import java.net.*; 


public class TCPServerText {

    static class ServerThread implements Runnable {
	    Socket client = null;
	    public ServerThread(Socket c) {
	        this.client = c;
	    }
	    public void run() {
	        try {
	            System.out.println("Connected to client : "+client.getInetAddress().getHostName());

				BufferedReader inFromClient =
						new BufferedReader(new InputStreamReader(client.getInputStream()));
				DataOutputStream outToClient = new DataOutputStream(client.getOutputStream());
	            // Try to read client message
	            while (true){
	    			String str = inFromClient.readLine();
	    			if (str==null) break;
	    			System.out.println("Received: " + str);
	    			outToClient.writeBytes(str.toUpperCase() + '\n');
	            }
	            System.out.println("Connection client : "+client.getInetAddress().getHostName());
	            client.close();
	            
	        } catch (Exception e) {
	            System.err.println(e.getMessage());
	        }
	    }
    }
    
    public static void main(String args[]) {
        try {
            ServerSocket server = new ServerSocket(7000);
            while (true) {
                Socket p = server.accept();
                new Thread(new ServerThread(p)).start();
            }
        } catch (Exception ex) {
            System.err.println("Error : " + ex.getMessage());
        }
    }
}
/*
class TCPServer {    
	public static void main(String argv[]) throws Exception {
		String clientSentence;
		String capitalizedSentence;
		ServerSocket welcomeSocket = new ServerSocket(6789);
		while(true){
			Socket connectionSocket = welcomeSocket.accept();
			BufferedReader inFromClient =
					new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			clientSentence = inFromClient.readLine();
			System.out.println("Received: " + clientSentence);
			capitalizedSentence = clientSentence.toUpperCase() + '\n';
			outToClient.writeBytes(capitalizedSentence);
		}
	}
} */