package RUBTClient;

import java.io.*;
import java.net.*;
import java.util.*;
import java.nio.*;

public class Peer {
  
    /**
     *It has methods to perform handshake, alive, interested, and other messages.
     */

    public String peerID = null;
    public int peerPort = 0;
    public Socket peerSocket = null;
    public String hostIP = null;
    
    public DataOutputStream client2peer = null;
    public DataInputStream peer2client = null;
    
    public boolean[] booleanBitField = null;
    
    public boolean peerInterested;
    public boolean peerChoking;

    final static int KEY_CHOKE = 0;
    final static int KEY_UNCHOKE = 1;
    final static int KEY_INTERESTED = 2;
    final static int KEY_UNINTERESTED = 3;
    final static int KEY_HAVE = 4;
    final static int KEY_BITFIELD = 5;
    final static int KEY_REQUEST = 6;
    final static int KEY_PIECE = 7;
    final static int KEY_CANCEL = 8;
    final static int KEY_PORT = 9;
    
    /**
     * Set the byte arrays for the static peer messages 
     */
    final static byte[] interested = {0, 0, 0, 1, 2};
    final static byte[] uninterested = {0, 0, 0, 1, 3};
    final static byte[] choke = {0, 0, 0, 1, 0};
    final static byte[] unchoke = {0, 0, 0, 1, 1};
    final static byte[] empty_bitfield = {0, 0, 0, 2, 5, 0};
    final static byte[] keep_alive = {0, 0, 0, 0};

    
    public Peer(String peerIdNum, String IpNum, int peerPortNum) {
        peerID = peerIdNum;
        hostIP = IpNum;
        peerPort = peerPortNum;
        booleanBitField = new boolean [RUBTClient.numPieces];
    }
}
