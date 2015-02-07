package evans.dave.androtag.server;

import android.graphics.Color;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import evans.dave.androtag.common.*;

public class TCPServer {

	static Hashtable<Integer,Game> games = new Hashtable<Integer,Game>();

	// (ArrayList<Game>) Collections.synchronizedList(
	static class ServerThread implements Runnable {
		Socket client = null;

		public ServerThread(Socket c) {
			this.client = c;
		}

		public void run() {
			try {
				System.out.println("Connected to client : "
						+ client.getInetAddress().getHostName());

				ObjectOutputStream oos = new ObjectOutputStream(
						client.getOutputStream());
				ObjectInputStream ois = new ObjectInputStream(
						client.getInputStream());
				
				byte code;
				byte gid;
				byte tid;
				Game game;
				
				// Try to read client message
				read_stream: while (true) {
					// Check first character
					code = TCPMessage.CLOSE;
					try {
						code = ois.readByte();
					} catch (EOFException e) {
						break read_stream;
					}
					;

					System.out.println("Read " + code);

					switch (code) {
					case TCPMessage.CLOSE:
						break read_stream;

					case TCPMessage.STRING_MSG:
						String str = (String) ois.readObject();
						System.out.println("Received: " + str);
						break;
						
					case TCPMessage.GET_GAME_LIST:
						oos.writeByte(TCPMessage.GET_GAME_LIST);
						oos.writeObject(getGameList());
						oos.flush();
						break;
					
					case TCPMessage.GET_GAME:
						// Get the game id that they want
						gid = ois.readByte();
						// Send the data back
						oos.writeByte(TCPMessage.GET_GAME);
						// Automatically returns NO_GAME if game isn't available
						oos.writeObject(findGame(gid)); 
						oos.flush();
						break;
					
					case TCPMessage.REQ_JOIN_GAME:
						// Get the game id that they want
						gid = ois.readByte();
						oos.writeByte(TCPMessage.GET_GAME);
						// Check if we can add a player to the game
						
						// Check game slots
						game = findGame(gid);
						if (game == Game.NO_GAME || game.isFull()){
							oos.writeByte(0); // Deny join
						}else {
							oos.writeByte(1); // Confirm join
							oos.writeObject(game); // Return game info
						}
						oos.flush();
						break;
						
					case TCPMessage.REQ_JOIN_TEAM:
						// Get the game id, and team id, and user who's requesting
						gid = ois.readByte();
						tid = ois.readByte();
						User user = (User) ois.readObject();
						
						// Check if team is full
						game = findGame(gid);
						
						// Try and add to game
                        // TODO: This isn't quite what I want here
						boolean success = game.addPlayerToTeam(new GeneralPlayer(user), tid);
						
						if (!success){
							oos.writeByte(0); // Deny join
						} else {
							oos.writeByte(1); // Allow join
							oos.writeObject(game);
						}
						oos.flush();
						break;
						
					case TCPMessage.REQ_CREATE_GAME:
						
						// Get the game they want to create
						game = (Game) ois.readObject();
						
						// Check if it conflicts with a game already made
                        // TODO: Create game
						break;
						
						
					}
				}
				System.out.println("Closed client : "
						+ client.getInetAddress().getHostName());
				client.close();

			} catch (Exception e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/** Main program **/
	public static void main(String args[]) {

		// Set up some example games
		GeneralPlayer davek = new GeneralPlayer("Davek");
		GeneralPlayer studley = new GeneralPlayer("Studley Hungwell");
		GeneralPlayer dfarce = new GeneralPlayer("DFarce");

        Team[] teams = new Team[] {
                        new Team("Red",Color.RED),
                        new Team("Blue Blazers", Color.BLUE),
                        new Team("Hwhite Hwpower", Color.WHITE) };

		games.put(1, new Game(1, 2, 10, System.currentTimeMillis(), System
				.currentTimeMillis() + 60 * 1000 * 5,
                new Team[] {teams[0],teams[1]},
                25,
                dfarce));

		games.put(2, new Game(2, 3, 255,
				System.currentTimeMillis() + 60 * 1000 * 5, System
						.currentTimeMillis() + 60 * 1000 * 15,
                teams,
                100,
				studley));

		games.put(4, new Game(4, 4, 255,
				System.currentTimeMillis() + 60 * 1000 * 5, System
						.currentTimeMillis() + 60 * 1000 * 15,
                new Team[] {teams[1],teams[2]},
                5,
                new User("Nick knack paddy whack", 2)));

		teams[0].add(davek);
		teams[0].add(studley);
		teams[1].add(dfarce);

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

	private static Hashtable<Integer,GameInfo> getGameList() {
		Hashtable<Integer,GameInfo> gameList = new Hashtable<Integer,GameInfo>();
        Enumeration<Integer> enumKey = games.keys();
        while(enumKey.hasMoreElements()) {
            Integer id = enumKey.nextElement();
			gameList.put(id, games.get(id).getSuper());
		}
		return gameList;
	}
	
	private static Game findGame(byte gid){
		if (games.containsKey(gid))
            return games.get(gid);
		return Game.NO_GAME;
	}
}