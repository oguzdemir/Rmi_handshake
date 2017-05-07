import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by od on 7.05.2017.
 */
public interface Handshake extends Remote {
    void handshake(String name) throws RemoteException;
}