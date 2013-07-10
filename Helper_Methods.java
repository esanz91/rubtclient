package rubtclient;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Random;
import java.net.ServerSocket;

/**
 *
 * @author Jason
 */
public class Helper_Methods {

    public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String toHexString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        if (bytes.length == 0) {
            return "";
        }

        StringBuilder hex = new StringBuilder(bytes.length * 3);

        for (byte b : bytes) {
            byte hi = (byte) ((b >> 4) & 0x0f);
            byte lo = (byte) (b & 0x0f);

            hex.append('%').append(HEX_CHARS[hi]).append(HEX_CHARS[lo]);
        }
        return hex.toString();
    }
    public static int getPort(){

    
        for (int i = 6881; i <= 6889; i++) {
                  try {
                     ServerSocket port= new ServerSocket(i);
                        int listenport;
                       return listenport = i;
                      } catch (IOException e) {
                      System.out.println("Unable to create sSocket at port " + i);
                      }
                }
        System.out.println("Unable to create Socket. Stopping Now!");
            return -1;
    }
}
