
import java.rmi.*;
import java.rmi.registry.*;
import java.rmi.server.*;
import java.net.*;
import java.net.UnknownHostException;
import java.util.*;

import java.io.*;

/**
 *
 * @author chaos_000
 */
public class Chord extends java.rmi.server.UnicastRemoteObject implements ChordMessageInterface {

    /**
     *
     */
    public static final int M = 2;

    Registry registry;    // rmi registry for lookup the remote objects.
    Finger successor;
    Finger predecessor;
    Finger[] finger;
    int nextFinger;
    int i;   	//your id	// GUID
    int port;

    /**
     * ***************************************************************
     */
    /* \brief Create an istance of the ChordMessageInterface of the   */
    /* node (ip, port)												  */
    /**
     * ***************************************************************
     * @param ip
     * @param port
     * @return 
     */
    public ChordMessageInterface rmiChord(String ip, int port) {
        ChordMessageInterface chord = null;
        try {
            Registry registry = LocateRegistry.getRegistry(ip, port);
            chord = (ChordMessageInterface) (registry.lookup("Chord"));
            return chord;
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * ***************************************************************
     */
    /* \brief Check if key is in the semi-open interval (key1, key2]  */
    /*  Since it is a ring, key1  can be greater than key2.           */
    /* In that case we verify whether key > key2 or key <= key1       */
    /**
     * ***************************************************************
     * @param key
     * @param key1
     * @param key2
     * @return 
     */
    public Boolean in(int key, int key1, int key2) {
        if (key1 < key2) {
            return (key > key1 && key <= key2);
        } else {
            return (key > key2 || key <= key1);
        }
    }

    /**
     * *********************************************************
     */
    /* \brief Check if key is in the open interval (key1, key2)  */
    /*  Since it is a ring, key1  can be greater than key2.     */
    /* In that case we verify whether key > key2 or key < key1  */
    /**
     * *********************************************************
     * @param key
     * @param key1
     * @param key2
     * @return 
     */
    public Boolean ino(int key, int key1, int key2) {
        if (key1 < key2) {
            return (key > key1 && key < key2);
        } else {
            return (key > key2 || key < key1);
        }
    }

    ////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param guid
     * @param data
     * @throws IOException
     */
        public void put(int guid, byte[] data) throws IOException {
        Finger succ = locateSuccessor(guid);
        if (succ != null) {
            ChordMessageInterface succChord = rmiChord(succ.getIp(), succ.getPort());
            succChord.write(guid, data);// Writes bytes from the specified byte array to this file output stream 

        }
        // TODO: Store data in the closest node.
        // before calling this method you need to find the node using 
        // locateSuccessor. 
    }

    /**
     *
     * @param guid
     * @throws IOException
     */
    public void remove(int guid) throws IOException {
        Finger succ = locateSuccessor(guid);
        if (succ != null) {
            ChordMessageInterface succChord = rmiChord(succ.getIp(), succ.getPort());
            succChord.delete(guid);// Writes bytes from the specified byte array to this file output stream 

        }
        // TODO: remove the file guid.
        // before calling this method you need to find the node using 
        // locateSuccessor. 
    }

    /**
     *
     * @param guid
     * @return
     * @throws IOException
     */
    public byte[] get(int guid) throws IOException {
        Finger succ = locateSuccessor(guid);
        if (succ != null) {
            ChordMessageInterface succChord = rmiChord(succ.getIp(), succ.getPort());
            return succChord.read(guid);// Writes bytes from the specified byte array to this file output stream 	
        }
        // TODO: read the file
        // before calling this method you need to find the node using 
        // locateSuccessor. 
        return null;
    }

    /**
     *
     * @param guid
     * @param data
     * @throws IOException
     */
    public void write(int guid, byte[] data) throws IOException {
        File file = new File(Integer.toString(guid));
        FileOutputStream fos = null;
        fos = new FileOutputStream(file);
        // Writes bytes from the specified byte array to this file output stream 
        fos.write(data);
    }

    /**
     *
     * @param guid
     * @return
     * @throws IOException
     */
    public byte[] read(int guid) throws IOException {

        //read from file, return contents
        File file = new File(Integer.toString(guid));
        FileInputStream fis = null;
        fis = new FileInputStream(file);
        byte[] data = new byte[fis.available()];
        fis.read(data);

        return data;

    }

    /**
     *
     * @param guid
     * @throws IOException
     */
    public void delete(int guid) throws IOException {
        //delete file
        File file = new File(Integer.toString(guid));
        if (file.delete()) {
            System.out.println(file.getName() + " deleted");
        } else {
            System.out.println("Failed to delete " + file.getName());
        }
    }

    ///////////////////////////////////////////////////////////////////////////

    /**
     *
     * @return
     * @throws RemoteException
     */
        @Override
    public int getId() throws RemoteException {

        return i;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public boolean isAlive() throws RemoteException {
        return true;
    }

    /**
     *
     * @return
     * @throws RemoteException
     */
    public Finger getPredecessor() throws RemoteException {
        return predecessor;
    }

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
    public Finger locateSuccessor(int key) throws RemoteException {
        if (key == i) {
            throw new IllegalArgumentException("Key must be distinct that  " + i);
        }
        if (successor != null && successor.getId() != i) {
            if (in(key, i, successor.getId())) {
                return successor;
            }
            Finger jguid = closestPrecedingNode(key);
            ChordMessageInterface j = rmiChord(jguid.getIp(), jguid.getPort());
            if (j == null) {
                return null;
            }
            return j.locateSuccessor(key);
        }
        return successor;
    }

    /////////////////////////////////////////////////////////////////////////////

    /**
     *
     * @param key
     * @return
     * @throws RemoteException
     */
        public Finger closestPrecedingNode(int key) throws RemoteException {
        if (key != i) {
            int count;
            for (count = M - 1; count > 1; count--) {
                if (finger[count] != null && ino(finger[count].getId(), i, key)) {
                    return (finger[count]);
                }
            }

        }
        ///this shouldnt happen
        return successor;

    }

    /**
     *
     * @param ip
     * @param port
     * @throws RemoteException
     */
    public void joinRing(String ip, int port) throws RemoteException {
        ChordMessageInterface j = rmiChord(ip, port);
        predecessor = null;
        successor = j.locateSuccessor(i);
    }

    // Verifies and inform successor

    /**
     *
     * @throws RemoteException
     * @throws UnknownHostException
     */
        public void stabilize() throws RemoteException, UnknownHostException {
        ChordMessageInterface succ = rmiChord(successor.getIp(), successor.getPort());
        Finger x = succ.getPredecessor();
        if (ino(x.getId(), i, successor.getId())) {
            successor = x;
        }
        InetAddress ip = InetAddress.getLocalHost();
        succ = rmiChord(successor.getIp(), successor.getPort());
        succ.notify(new Finger(ip.getHostAddress(), port, i));
    }

    /**
     *
     * @param j
     * @throws RemoteException
     */
    public void notify(Finger j) throws RemoteException {
        if (predecessor == null || in(j.getId(), predecessor.getId(), i)) {
            //transfer keys in the range [j,i) to j
            predecessor = j;
        }
    }

    ///these dont need code until later

    /**
     *
     */
        public void fixFingers() {

    }

    /**
     *
     */
    public void checkPredecessor() {
        // TODO	
    }

    //////////////////////////////////////////////////////////////////////////////////   

    /**
     *
     * @param _port
     * @param id
     * @throws RemoteException
     * @throws UnknownHostException
     */
        public Chord(int _port, int id) throws RemoteException, UnknownHostException {
        finger = new Finger[(1 << M)];    // 1 << M = 2^(M)
        // TODO: set the fingers in the array to null
        i = id;
        port = _port;
        // TODO: determine the current IP of the machine

        predecessor = null;
        InetAddress ip = InetAddress.getLocalHost();
        successor = new Finger(ip.getHostAddress(), i, i);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    stabilize();
                } catch (RemoteException | UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                fixFingers();
                checkPredecessor();
            }
        }, 500, 500);
        try {
            // create the registry and bind the name and object.
            System.out.println("Starting RMI at port=" + port);
            registry = LocateRegistry.createRegistry(port);
            registry.rebind("Chord", this);
        } catch (RemoteException e) {
            throw e;
        }
    }
}
