package RUBTClient;

import java.net.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
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
	public static final char[] HEX_CHARS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
		'A', 'B', 'C', 'D', 'E', 'F'};
	
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
	private static RUBTClient client;
	//public byte[] peerID;
	//public static int bytesDownloaded;
	//public static int bytesUploaded;
	//public static int bytesRemaining;
	//private String event;
	
	/** Tracker Information */
	private static URL trackerUrl;
	private static String trackerIP;
	private static int trackerPort;
	
	/** Connection Information */
	private static URL requestedURL;
	private static String[] peerList ; 
	static int listeningPort = -1;
	
	
	/* ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++ */
	/** Tracker Constructor */
	TrackerGetr(RUBTClient c, TorrentInfo t) {		
		
		/** Fill in Client Information */
		client = c; 										/* RUBTClient */
		
		/** Fill in Client Information */
		torrentData = t; 									/* TorrentInfo */
		
		/** Fill in Tracker Information */
		trackerUrl = torrentData.announce_url; 				/* URL */
		trackerIP = trackerUrl.getHost(); 					/* String */
		trackerPort = trackerUrl.getPort(); 				/* int */
	}
	
	
	
	
	public byte[] connect(int bytesDown, int bytesUp, int bytesRemaining, String event) throws IOException {

		/** Variables */
		Socket trkSocket = null;
		URL trkURL = null;
		HttpURLConnection trkConnection = null;
		byte[] trkByteResponse = null;
 		Map trkResponse = null;
		
		/** Verify Tracker was initialized */
		if (trackerUrl == null)
		{
			System.err.println("Tracker was not created properly. ");
			return null;
		}
		
		/** Open socket in order to communicate with tracker */
		try
		{
			trkSocket = new Socket(trackerIP, trackerPort);
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Unable to create socket at " + trackerIP + ":" + trackerPort);
			return null;
		}
		
		/** Create tracker HTTP URL connection */
		try
		{
			trkURL = newURL(bytesDown, bytesUp, bytesRemaining, trackerUrl);
			trkConnection = (HttpURLConnection) trkURL.openConnection();
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Unable to create HTTP URL Connection with tracker. ");
			return null;
		}
		
		/** Receiving tracker response */
		/*NOTE: Still needs to be modified. In progress. */
		try 
		{
			DataInputStream fromtracker = new DataInputStream(trkConnection.getInputStream());
			/*
			int size = trkConnection.getContentLength();
			trkByteResponse = new byte[size];
			fromtracker.readFully(trkByteResponse);
			*/
		} catch (IOException e) {
			System.out.println("Caught IOException: " + e.getMessage());
		}
		
		/** Decoding tracker Map response to String Array */
		 	peerList = decodeCompressedPeers(trkResponse);
		 	
		/** Extract information (interval) from tracker response  */
		//blah blah 
		
		// Dummy response 
		return trkByteResponse;
	}

	
	
	/** Method: Create and return requested URL */
	public static URL newURL(int bytesDown, int bytesUp, int bytesRemaining, URL announceURL) {
		/* Variables */
		String newUrlString = "";
		
		/** Find a random port to connect */
		listeningPort = getPortNum();
		
		/** Create requestedURL */
		newUrlString += trackerUrl 
		+ "?info_hash=" + toHexString(torrentData.info_hash.array())
		+ "&peer_id=" + toHexString((client.getPeerId()).getBytes()) 
		+ "&port=" + listeningPort 
		+ "&uploaded=" + bytesUp 
		+ "&downloaded=" + bytesDown 
		+ "&left=" + bytesRemaining;
		
		if ((client.getEvent()) != null) {
			newUrlString += "&event=" + (client.getEvent());
		}
		
		/** Return requested URL */
		try 
		{
			requestedURL = new URL(newUrlString);
			return requestedURL;
		} 
		catch (MalformedURLException e) 
		{
			System.out.println("Unable to create URL");
			return null;
		}
	}
	
	
	
	/** Method: Returns a port to connect on */
	public static int getPortNum() {
		/* Variables */
		ServerSocket serverPort;
		int listenPort;

		for (int i = 6881; i <= 6889; i++) {
			try 
			{
				serverPort = new ServerSocket(i);
				return listenPort = i;
			} 
			catch (IOException e) 
			{
				System.out.println("Unable to create Socket at port " + i);
			}
		}

		System.out.println("Unable to create Socket. Stopping Now!");
		return -1;
	}
	
	
	
	/** Method: Turn bytes to HexStrings */
	/* NOTE: NEED TO MODIFY */
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
	
	
	
	/** Method: Decode Map to String[] */
	public static String[] decodeCompressedPeers(Map map){
		ByteBuffer peers = (ByteBuffer) map.get(ByteBuffer.wrap("peers".getBytes()));
		ArrayList<String> peerURLs = new ArrayList<String>();
		try {
			while (true) {
				String ip = String.format("%d.%d.%d.%d",
						peers.get() & 0xff,
						peers.get() & 0xff,
						peers.get() & 0xff,
						peers.get() & 0xff);
				int port = peers.get() * 256 + peers.get();
				peerURLs.add(ip + ":" + port);
			}
		} catch (BufferUnderflowException e) {
			// done
		}
		return peerURLs.toArray(new String[peerURLs.size()]);
	}

	
	
	/* +++++++++++++++++++++++++++++++ GET-METHODS +++++++++++++++++++++++++++++++++++ */
	
	public String[] getPeerList(){
		return peerList;
	}
}
