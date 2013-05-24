package listeningserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ListeningServerThread Class.
 * Creates a new thread to deal with the message received.
 *
 * @author Jessica H. Colnago
 */
public class ListeningServerThread extends Thread {
    private Socket socket = null;
    private final HashMap<String, Integer> map;
    private int biggestValue;
    private String key, keyReceived;
    
    /**
     * Class constructor.
     * 
     * @param map hashMap that will store the amount of time a gesture was recognized
     * @param client the client socket
     */
    public ListeningServerThread(HashMap<String, Integer> map, Socket client) {
        super("ListeningServerThread");
        this.socket = client;
        this.map = map;
    }

    /**
     * Implements the behavior for the thread.
     */
    @Override
    public void run() {
	try {
            try (BufferedReader in = new BufferedReader(
                                         new InputStreamReader(
                                         socket.getInputStream()))) {
                String inputLine;
                try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
                    while ((inputLine = in.readLine()) != null) {
                        keyReceived=inputLine.split(";")[0];
                        switch (keyReceived) {
                            case "START":
                                biggestValue=0;
                                synchronized(map){
                                    map.clear();
                                } 
                                break;
                            case "STOP":
                                synchronized(map){
                                    for (Map.Entry me : map.entrySet()) {
                                        if (biggestValue < (int)me.getValue()) {
                                            key = me.getKey().toString();
                                            biggestValue = map.get(key);  
                                        }
                                    }
                                }
                                out.println(key);
                                break;
                            default:
                                synchronized(map){
                                    if (map.containsKey(keyReceived)) {
                                        map.put(keyReceived, map.get(keyReceived)+1);
                                    } 
                                    else {
                                        map.put(keyReceived, 1);
                                    }
                                }
                        }
                    }
                }
                in.close();
            }
	    socket.close();

	} catch (IOException ex) {
            Logger.getLogger(ListeningServerThread.class.getName()).log(Level.SEVERE, null, ex);
	}
    }    
}
