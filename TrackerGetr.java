package RUBTClient;

import java.net.*;
import java.io.*;
import java.util.*;

/**
 * TrackerGetr Class
 * Main Functions:
 * 1) Store Tracker Information
 * 2) Connect Client to Tracker through socket
 * 3) Connect Client to Tracker through HTTP Connection
 * 4) Receive Tracker Response 
 * 
 */

public class TrackerGetr {

	/** The Constant requestSize */
	public static final int requestSize = 16000;
	
	/** Torrent Information:  
	 * infoHash
	 * announceURL
	 * torrent_file_bytes
	 * piece_length
	 * piece_hashes */
	private static TorrentInfo torrentData;
	//public byte[] infoHash;
	//private URL announceURL;
	
	/** Client Information: 
	 * destinationFile,
	 * bytesDownloaded, 
	 * bytesUploaded, 
	 * bytesRemaining, 
	 * event; */
	private RUBTClient client;
	//public byte[] peerID;
	//public static int bytesDownloaded;
	//public static int bytesUploaded;
	//public static int bytesRemaining;
	//private String event;
	
	/** Tracker Information */
	private URL trackerUrl;
	private static String trackerIP;
	private static int trackerPort;
	
	/** Connection Information */
	private URL requestedURL;
	private static ArrayList<Peer> peerList ; 
	static int listeningPort = -1;
	
	
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/** Tracker Constructor */
	TrackerGetr(RUBTClient c, TorrentInfo t) {		
		
		/** Fill in Client Information */
		this.client = c; 								/* RUBTClient */
		
		/** Fill in Client Information */
		torrentData = t; 								/* TorrentInfo */
		
		/** Fill in Tracker Information */
		this.trackerUrl = torrentData.announce_url; 	/* URL */
		trackerIP = trackerUrl.getHost(); 				/* String */
		trackerPort = trackerUrl.getPort(); 			/* int */
	}
	
	public static byte[] connect(TorrentInfo torrent) throws IOException {

		/** Variables */
		Socket trkSocket = null;
		URL trkURL = null;
		
		/** Open socket in order to communicate with tracker */
		try
		{
			trkSocket = new Socket(trackerIP, trackerPort);
		}
		catch(Exception e){
			System.err.println("ERROR: Unable to create socket at " + trackerIP + ":" + trackerPort);
			return null;
		}
		
		/** Initiate tracker URL connection */
		try{
			trkURL = newURL(trackerUrl);
			//HELP!!! I'm having trouble is static. Should we make the method void?
		}
		catch(Exception e){
			/* ??? */
		}
		
		/** Commence connection with tracker */
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

	/** Method: Create and return requested URL */
	public URL newURL(URL announceURL) {
		/* Variables */
		String newURL;
		
		/** Find a random port to connect */
		this.listeningPort = GeneratingFunctions.getPort();
		
		/** Create requestedURL */
		newURL = announceURL.toString();
		newURL += "?info_hash=" + GeneratingFunctions.toHexString(this.torrentData.info_hash.array())
		+ "&peer_id=" + GeneratingFunctions.toHexString(this.client.peerID.getBytes()) + "&port="
		+ this.listeningPort + "&uploaded=" + this.client.bytesUploaded + "&downloaded="
		+ this.client.bytesDownloaded + "&left=" + this.client.bytesRemaining;
		
		if (this.client.event != null) {
			newURL += "&event=" + this.client.event;
		}
		
		/** Return requested URL */
		try 
		{
			this.requestedURL = new URL(newURL);
			return this.requestedURL;
		} 
		catch (MalformedURLException e) 
		{
			System.out.println("Unable to create URL");
			return null;
		}
	}
}
