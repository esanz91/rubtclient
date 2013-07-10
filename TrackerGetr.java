package rubtclient;

import java.net.*;
import java.io.*;

public class TrackerGetr {

    /**
     * The Constant requestSize.
     */
    public static final int requestSize = 16000;
    /**
     * The infohash.
     */
    public byte[] infohash;
    /**
     * The peerid.
     */
    public byte[] peerid;
    /**
     * The uploaded.
     */
    public static int uploaded;
    /**
     * The downloaded.
     */
    public static int downloaded;
    /**
     * The left.
     */
    public int left;
    /**
     * The port.
     */
    private int port;
    /**
     * The announce.
     */
    private URL announce;
    /**
     * The event.
     */
    private String event;
    /**
     * The request string.
     */
    private URL getrequestURL;
    
    
    TrackerGetr(TorrentInfo torrent, final byte[] peerId, final int port) {
		this.infohash = torrent.info_hash.array();
		this.peerid = peerId;
		this.left = torrent.file_length;
		this.port = port;
		this.event = null;
		this.announce = torrent.announce_url;
		this.getrequestURL = newURL(torrent.announce_url);
	}

    public static byte[] Httpconnect(TorrentInfo torrent) throws IOException {

        try {
            HttpURLConnection tracker = (HttpURLConnection) torrent.announce_url.openConnection();
            DataInputStream fromtracker = new DataInputStream(tracker.getInputStream());
            byte[] response;
            int size = tracker.getContentLength();
            response = new byte[size];
            fromtracker.readFully(response);
            return response;
        } catch (IOException e) {
            System.out.println("Caught IOException: " + e.getMessage());
        }
        return null;
    }

    public URL newURL(URL announceURL) {
        String newURL = announceURL.toString();
        newURL += "?info_hash=" + Helper_Methods.toHexString(this.infohash)
                + "&peer_id=" + Helper_Methods.toHexString(this.peerid) + "&port="
                + this.port + "&uploaded=" + this.uploaded + "&downloaded="
                + this.downloaded + "&left=" + this.left;
        if (this.event != null) {
            newURL += "&event=" + this.event;
        }

        try {
            return new URL(newURL);
        } catch (MalformedURLException e) {
            System.out.println("Unable to create URL");
            return null;
        }
    }
}
