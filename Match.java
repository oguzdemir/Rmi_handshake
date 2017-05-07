import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by od on 7.05.2017.
 */
public interface Match extends Remote {
    String match(String name, int timeout) throws RemoteException;
}
