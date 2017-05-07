import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by od on 7.05.2017.
 */
public class Client extends UnicastRemoteObject implements Handshake{

    private static String name;
    private static String matched;
    private static boolean first;
    public Client() throws Exception{

    }

    public static void main(String[] args) {

        name = args[0];
        first = false;

        try {
            System.out.println("Z:" + name);
            Registry registry = LocateRegistry.getRegistry(2020);
            Handshake client = new Client();
            String s = "server";
            Match server = (Match) registry.lookup(s);
            registry.rebind(name, client);

            System.out.println("Y");
            matched = server.match(name,10);

            if(matched != null) {
                System.out.println("Matched with " + matched);

                if(name.compareTo(matched) > 0 ) {
                    Handshake handshake = (Handshake) registry.lookup(matched);
                    first = true;
                    handshake.handshake(name);
                }
            }
            else {
                registry.unbind(name);
                System.exit(0);
            }

        } catch (Exception e) {

        }

    }
    @Override
    public void handshake(String name) throws RemoteException {
        Registry registry = LocateRegistry.getRegistry(2020);
        //If this process is the first one, it takes the second handshake.
        if (first)
        {
            System.out.println("second message.");
            try {
                registry.unbind(this.name);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        else
        {
            System.out.println("First message.");

            try {
                Handshake handshake = (Handshake) registry.lookup(matched);
                UnicastRemoteObject.unexportObject(this,true);
                handshake.handshake(name);
            } catch (NotBoundException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
