import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by od on 7.05.2017.
 */
public class Server extends UnicastRemoteObject implements Match{

    private String lastPeer;
    private Semaphore semaphore;
    private Semaphore semaLock;
    private final Object lock = new Object();

    public Server() throws Exception {
        lastPeer = null;
        semaphore = new Semaphore(0);
        semaLock = new Semaphore(2);
    }

    public static void main (String [] args) {
        try {
            System.out.println("X");
            Registry reg = LocateRegistry.createRegistry(2020);
            Match server = new Server();
            reg.rebind("server", server);
            System.out.println("Binded");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String match(String name, int timeout) {
        System.out.println("Match");
        long time = System.currentTimeMillis();
        boolean locked = false;
        try {
             locked = semaLock.tryAcquire(1,timeout, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return null;
        }

        System.out.println("Lock: " + lock);
        if(locked) {
            long timePassed = System.currentTimeMillis() - time;
            try {

                boolean b;
                synchronized (lock){
                    b = lastPeer == null;
                    if(b) {
                        lastPeer = name;
                    }
                }
                System.out.println( b + " and " + lastPeer);
                //If there is no pending client for match, it will wait for given timeout to acquire semaphore.
                //Once the semaphore is acquired, it is known that lastPeer includes the name of second client.
                if(b){
                    boolean acquire = semaphore.tryAcquire(1,timeout*1000-timePassed, TimeUnit.MILLISECONDS);
                    if(acquire){
                        String returned = lastPeer;
                        lastPeer = null;
                        semaphore.release(1);
                        semaLock.release(2);
                        return returned;
                    } else {
                        lastPeer = null;
                        semaLock.release(1);
                        return null;
                    }
                } else {
                    String returned = lastPeer;
                    lastPeer = name;
                    semaphore.release(1);
                    return returned;
                }

            } catch (InterruptedException e) {
                lastPeer = null;
                return null;
            }
        }
        else
            return null;
    }
}
