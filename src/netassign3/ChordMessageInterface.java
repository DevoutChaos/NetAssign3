
import java.rmi.*;
import java.io.*;

/**
 *
 * @author chaos_000
 */
public interface ChordMessageInterface extends Remote {

    /**
     *
     * @return
     * @throws RemoteException
     */
    public Finger getPredecessor() throws RemoteException;

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    Finger locateSuccessor(int key) throws RemoteException;

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    Finger closestPrecedingNode(int key) throws RemoteException;

    /**
     *
     * @param Ip
     * @param port
     * @throws RemoteException
     */
    public void joinRing(String Ip, int port) throws RemoteException;

    /**
     *
     * @param j
     * @throws RemoteException
     */
    public void notify(Finger j) throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean isAlive() throws RemoteException;

    /**
     *
     * @return
     * @throws RemoteException
     */
    public int getId() throws RemoteException;

    /**
     *
     * @param guid
     * @param data
     * @throws IOException
     * @throws RemoteException
     */
    public void put(int guid, byte[] data) throws IOException, RemoteException;

    /**
     *
     * @param guid
     * @throws IOException
     * @throws RemoteException
     */
    public void remove(int guid) throws IOException, RemoteException;

    /**
     *
     * @param guid
     * @return
     * @throws IOException
     * @throws RemoteException
     */
    public byte[] get(int guid) throws IOException, RemoteException;

    /**
     *
     * @param guid
     * @param data
     * @throws IOException
     */
    public void write(int guid, byte[] data) throws IOException;

    /**
     *
     * @param guid
     * @return
     * @throws IOException
     */
    public byte[] read(int guid) throws IOException;

    /**
     *
     * @param guid
     * @throws IOException
     */
    public void delete(int guid) throws IOException;
}
