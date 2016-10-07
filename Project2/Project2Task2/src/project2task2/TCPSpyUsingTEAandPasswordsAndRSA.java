package project2task2;

import java.net.*;
import java.io.*;
import java.math.BigInteger;
import java.util.Random;

// this is client
public class TCPSpyUsingTEAandPasswordsAndRSA {

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
            
            tea=new TEA(br.readLine().getBytes());

            // generate key. 
            Random rnd = new Random();
            BigInteger key = new BigInteger(16 * 8, rnd);
            BigInteger n = new BigInteger("2119748904166709409243290358410807820808581827052084601149139982309968602158071144945899641381472325867645883931435994871892742640324649543872540694192205129788278697898849001867194342017688743483956494826016338422370812398607767968684454599");
            // Step 4: Select a small odd integer e that is relatively prime to phi(n).
            // By convention the prime 65537 is used as the public exponent.
            BigInteger e = new BigInteger("65537");

            // send key to server
            BigInteger m = new BigInteger(key.toString().getBytes()); // m is the original clear text
            // encrypt
            BigInteger c = m.modPow(e, n);
            byte[] key_mes = c.toByteArray();
            out.write(key_mes);
            out.flush();
            
            tea=new TEA(key.toString().getBytes());
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
