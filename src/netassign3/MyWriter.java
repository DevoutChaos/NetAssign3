
import java.util.*;
import java.io.*;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

/**
 *
 * @author chaos_000
 */
public class MyWriter {

    Chord chord;

    /**
     *
     * @param _port
     * @param id
     * @throws RemoteException
     * @throws UnknownHostException
     */
    public MyWriter(int _port, int id) throws RemoteException, UnknownHostException {
        chord = new Chord(id, _port);
        Timer timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Scanner scan = new Scanner(System.in);
                while (true) {
                    // TODO User interface: join, put, get, print, remove from Chord
                    // print must show the state (all the variables of chord) of the system
                }
            }
        }, 500, 500);
    }

    /**
     *
     * @param args
     */
    static public void main(String args[]) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Parameter: <guid> <port>");
        }
        try {
            MyWriter writer = new MyWriter(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
