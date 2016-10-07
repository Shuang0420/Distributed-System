package project2task1;

import java.net.*;
import java.io.*;

// this is client
public class TCPSpyUsingTEAandPasswords {

    public static void main(String args[]) {
        // arguments supply message and hostname
        Socket s = null;
        TEA tea = null;
        try {
            int serverPort = 7896;
            s = new Socket("localhost", serverPort);
            //System.out.println("Successfully connected");
            DataInputStream in = new DataInputStream(s.getInputStream());
            DataOutputStream out = new DataOutputStream(s.getOutputStream());

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            // get key
            System.out.println("Enter symmetric key for TEA (taking first sixteen bytes):");
            tea = new TEA(br.readLine().getBytes());
            // get user information
            StringBuffer sb = new StringBuffer();
            System.out.print("Enter your ID: ");
            sb.append(br.readLine() + "\t");
            System.out.print("Enter your Password: ");
            sb.append(br.readLine() + "\t");
            System.out.print("Enter your location: ");
            sb.append(br.readLine());
            // encrypt
            byte[] cipher = tea.encrypt(String.valueOf(sb).getBytes());
            // pass to server
            out.write(cipher);
            out.flush();

//                        System.out.println("Checking ");
            // receive message from server
            byte[] mes = new byte[1024];
            int len = in.read(mes);
            if (len < 0) {
                return;
            }
            byte[] res = new byte[len];
            for (int i = 0; i < len; i++) {
                res[i] = mes[i];
            }
            System.out.println(new String(res));

            out.close();
            in.close();
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }
}
