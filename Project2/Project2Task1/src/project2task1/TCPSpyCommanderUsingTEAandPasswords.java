package project2task1;

import java.net.*;
import java.io.*;
import java.util.HashMap;
import project2task1.PasswordHash;

// this is server
public class TCPSpyCommanderUsingTEAandPasswords {

    static String symmetric_key;// = "thisissecretsodon’ttellanyone";

    public static void main(String args[]) {
        try {
            int serverPort = 7896; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            // initilize
            System.out.println("Enter symmetric key for TEA (taking first sixteen bytes):");
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            symmetric_key = br.readLine();
            System.out.println("Waiting for spies to visit...");
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class Connection extends Thread {

    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    String mes_back;
    String kml;
    String symmetric_key = TCPSpyCommanderUsingTEAandPasswords.symmetric_key;
    static String mikem_loc = "-79.948460,40.444501,0.00000";
    static String joem_loc = "-79.945389,40.444216,0.00000";
    static String jamesb_loc = "-79.940450,40.437394,0.00000";
    static String seanb_loc = "-79.945289, 40.44431,0.00000";
    static int count = 0;
    HashMap<String, String> user_map = new HashMap<>();

    public Connection(Socket aClientSocket) {
        try {
            user_map.put("mikem", "61EB2CAC8B871A3E8B9B0869D185485E0AA3C0EA");
            user_map.put("joem", "C499064757D515DB6535E0737F1846538F086593");
            user_map.put("jamesb", "151C72476689D1809CEE681121E76EAB3830B80E");
            user_map.put("seanb", "A5FDB831CAF4D3251CA468C2F75A44B8C96BE97D");

            clientSocket = aClientSocket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        //int count = 0;
        try {			                 // an echo server
            count++;
            // receive message from server
            TEA tea = new TEA(symmetric_key.getBytes());
            byte[] mes = new byte[1024];
            int len = in.read(mes);
            if (len < 0) {
                return;
            }
            byte[] res = new byte[len];
            for (int i = 0; i < len; i++) {
                res[i] = mes[i];
            }
            // decrypt input 
            byte[] decryptRes = tea.decrypt(res);
            // print the input string 
            String decryptStr = new String(decryptRes);

            // check if valid (each character in the decrypted string is ASII)
            if (!isValid(decryptStr)) return;

            // add salt to user password
            String[] n_pwd = decryptStr.split("\t");
            String user_id = n_pwd[0];
            String pwd = n_pwd[1];
            switch (user_id) {
                case "mikem":
                    pwd += "star";
                    break;
                case "joem":
                    pwd += "hello";
                    break;
                case "jamesb":
                    pwd += "world";
                    break;
                case "seanb":
                    pwd += "lele";
                    break;
            }
            PasswordHash hash = new PasswordHash();
            pwd = hash.computeHash(pwd);
            // check user name and password
            //FileWriter fw = new FileWriter("SecretAgents.kml");
            if (user_map.containsKey(user_id) && user_map.get(user_id).equals(pwd)) {
                System.out.println("Got visit " + count + " from " + user_id);
                // update location
                switch (user_id) {
                    case "mikem":
                        mikem_loc = n_pwd[2];
                        break;
                    case "joem":
                        joem_loc = n_pwd[2];
                        break;
                    case "jamesb":
                        jamesb_loc = n_pwd[2];
                        break;
                    case "seanb":
                        seanb_loc = n_pwd[2];
                        break;
                }
                mes_back = "Thank you.Your location was securely transmitted to Intelligence Headquaters.";

                // update kml
                writeKML(seanb_loc, jamesb_loc, joem_loc, mikem_loc);
            } else {
                mes_back = "Not a valid user-id or password.";
                System.out.println("Got visit " + count + " from " + user_id + ". Illegal Password attempt.This may be an attack.");
                writeKML(seanb_loc, jamesb_loc, joem_loc, mikem_loc);
            }
            out.write(mes_back.getBytes());

        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {/*close failed*/

            }
        }

    }

    /**
     * Update .kml file
     *
     * @param seanb_loc
     * @param jamesb_loc
     * @param joem_loc
     * @param mikem_loc
     * @throws IOException
     */
    public void writeKML(String seanb_loc, String jamesb_loc, String joem_loc, String mikem_loc) throws IOException {
        FileWriter fw = new FileWriter("SecretAgents.kml");
        kml = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<kml xmlns=\"http://earth.google.com/kml/2.2\"\n"
                + "><Document>\n"
                + "<Style id=\"style1\">\n"
                + "<IconStyle>\n"
                + "<Icon>\n"
                + "<href>http://maps.gstatic.com/intl/en_ALL/mapfiles/ms/micons/blue- dot.png</href>\n"
                + "</Icon> </IconStyle> </Style> <Placemark>\n"
                + "<name>seanb</name>\n"
                + "<description>Spy Commander</description> <styleUrl>#style1</styleUrl>\n"
                + "<Point>\n"
                + "<coordinates>" + seanb_loc + "</coordinates> </Point>\n"
                + "</Placemark> <Placemark>\n"
                + "<name>jamesb</name> <description>Spy</description> <styleUrl>#style1</styleUrl> <Point>\n"
                + "<coordinates>" + jamesb_loc + "</coordinates> </Point>\n"
                + "</Placemark>\n"
                + "6\n"
                + "<Placemark> <name>joem</name> <description>Spy</description> <styleUrl>#style1</styleUrl> <Point>\n"
                + "<coordinates>" + joem_loc + "</coordinates> </Point>\n"
                + "</Placemark>\n"
                + "<Placemark> <name>mikem</name> <description>Spy</description> <styleUrl>#style1</styleUrl> <Point>\n"
                + "<coordinates>" + mikem_loc + "</coordinates> </Point>\n"
                + "</Placemark>\n"
                + "</Document>\n"
                + "</kml>";
        fw.write(kml);
        fw.flush();
        fw.close();
    }

    /**
     * Check if the key is valid (each character in the decrypted string is
     * ASII)
     *
     * @param decryptStr
     * @return if the key is valid
     */
    public boolean isValid(String decryptStr) {
        // check if valid (each character in the decrypted string is ASII)
        for (int i = 0; i < decryptStr.length(); i++) {
            if (decryptStr.charAt(i) > 128) {
                System.out.println("Got visit " + count + " illegal symmetric key used. This may be an attack.");
                return false;
                //System.exit(-1);
            }
        }
        return true;
    }
}
