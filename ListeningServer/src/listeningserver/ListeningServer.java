package listeningserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;

/**
 * ListeningServer Class.
 * Creates a new server to listen to the messages the GestureUI and the 
 * application sends.
 *
 * @author Jessica H. Colnago
 */
public class ListeningServer {
    
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        HashMap<String, Integer> map = new HashMap<>();
        boolean listening = true;
        
        if (args.length==0) {
            System.out.println("Select a port number");
            System.exit(1);
        }
        
        int port = Integer.parseInt(args[0]);
                
        try {
            serverSocket = new ServerSocket(port);
            while (listening) {
                new ListeningServerThread(map, serverSocket.accept()).start();
            }
            
            serverSocket.close();
            
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + port);
            System.exit(-1);
        }
    }
}

