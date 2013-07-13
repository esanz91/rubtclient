package RUBTClient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.BitSet;

public class Peer {
  
    /**
     *It has methods to perform handshake, alive, interested, and other messages.
     */

    private String client_id = "";
    private String peer_id = "";
    private byte[] info_hash;
    private String peer_ip = "";
    private int peer_port;
    private Socket peer_socket;
    private DataOutputStream client_to_peer;
    private DataInputStream peer_to_client;
    private boolean client_choking;
    private boolean client_interested;
    private boolean peer_choking;
    public boolean have_bitfield;
    private boolean peer_interested;
    private boolean hand_shake_sent;
    private boolean hand_shake_received;
    /**
     * Key for choke message
     */
    final static int KEY_CHOKE = 0;
    /**
     * Key for unchoke message
     */
    final static int KEY_UNCHOKE = 1;
    /**
     * Key for interested message
     */
    final static int KEY_INTERESTED = 2;
    /**
     * Key for uninterested message
     */
    final static int KEY_UNINTERESTED = 3;
    /**
     * Key for have message
     */
    final static int KEY_HAVE = 4;
    /**
     * Key for bitfield message
     */
    final static int KEY_BITFIELD = 5;
    /**
     * Key for request message
     */
    final static int KEY_REQUEST = 6;
    /**
     * Key for piece message
     */
    final static int KEY_PIECE = 7;
    /**
     * Key for cancel message
     */
    final static int KEY_CANCEL = 8;
    /**
     * Key for port message
     */
    final static int KEY_PORT = 9;
    /*
     * Set the byte arrays for the static peer messages 
     * ie the messages that don't ever change
     */
    final static byte[] interested = {0, 0, 0, 1, 2};
    final static byte[] uninterested = {0, 0, 0, 1, 3};
    final static byte[] choke = {0, 0, 0, 1, 0};
    final static byte[] unchoke = {0, 0, 0, 1, 1};
    final static byte[] empty_bitfield = {0, 0, 0, 2, 5, 0};
    final static byte[] keep_alive = {0, 0, 0, 0};
    
    private double bytes_dld = 0;
    private double bytes_upld = 0;
    /* 
     * bit_set represents the number of pieces downloaded
     */
    BitSet bit_set;

    public Peer(int no_pieces, String cid, String pid, String ip, int port, byte[] infohash) {

        client_id = cid;
        peer_id = pid;
        peer_ip = ip;
        peer_port = port;
        info_hash = infohash;
        bit_set = new BitSet(no_pieces);

        peer_socket = null;
        client_to_peer = null;
        peer_to_client = null;
        hand_shake_sent = false;
        hand_shake_received = false;

        // All conncections choked by default until unchocked by client
        client_choking = true;
        client_interested = false;
        peer_choking = true;
        peer_interested = false;
        have_bitfield = false;
    }
}
