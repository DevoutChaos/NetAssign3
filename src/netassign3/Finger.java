
import java.io.*;
import java.net.*;

/**
 *
 * @author chaos_000
 */
public class Finger implements Serializable {

    String ip;
    int port;
    int id;

    /**
     *
     * @param Ip
     * @param Port
     * @param Id
     */
    public Finger(String Ip, int Port, int Id) {
        this.ip = Ip;
        this.port = Port;
        this.id = Id;
    }

    /**
     *
     * @return
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @return
     */
    public String getIp() {
        return this.ip;
    }

    /**
     *
     * @return
     */
    public int getPort() {
        return this.port;
    }
}
