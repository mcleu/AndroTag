package evans.dave.androtag.server;

import java.io.*; 
import java.net.*; 
import java.util.ArrayList;

import evans.dave.androtag.common.*;

class TCPClient {  
	public static void main(String argv[]) throws Exception {   
		String sentence;   
		String modifiedSentence;
		ArrayList<GameInfo> gameList = new ArrayList<>();
		BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));   
		Socket clientSocket = new Socket("localhost", 7000);   
		ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());   
		ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());   
		while (true){
			sentence = inFromUser.readLine();
			if (sentence.isEmpty()) continue;
			byte code = (byte) ((byte) sentence.charAt(0) - (byte)'0');
			sentence = sentence.substring(1);
			
			if (code == TCPMessage.STRING_MSG){
				oos.writeByte(code);
				oos.writeObject(sentence + '\n'); 
				oos.flush();
			} else if (code == TCPMessage.GET_GAME_LIST){
				oos.writeByte(code);
				oos.flush();
				code = ois.readByte();
				if (code != TCPMessage.GET_GAME_LIST){
					System.out.println("Unexpected code from server");
				} else {
					gameList = (ArrayList<GameInfo>) ois.readObject();
					for (GameInfo g:gameList){
						long startTime = (g.startTime-System.currentTimeMillis())/60000;
						System.out.printf("%d\t %d\t %s\n",g.id, startTime, g.creator.name);
					}
					
				}
			}
		}
		//clientSocket.close();  
	} 
} 