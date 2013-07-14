package RUBTClient;

import java.io.*;
import java.util.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.net.*;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;


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

	/** Client Information: 
	 * destinationFile,
	 * bytesDownloaded, 
	 * bytesUploaded, 
	 * bytesRemaining, 
	 * event; */
	private static RUBTClient client;

	/** Tracker Information */
	private static URL trackerUrl;
	private static String trackerIP;
	private static int trackerPort;
	private static int trackerInterval;

	/** Connection Information */
	private static URL requestedURL;
	private static ArrayList<Peer> peerList ; 
	static int listeningPort = -1;

	/** keyINTERVAL */
	public static final ByteBuffer keyINTERVAL = 
		ByteBuffer.wrap(new byte[] { 'i', 'n', 't', 'e', 'r', 'v', 'a', 'l' });

	/** keyPEERS */
	public static final ByteBuffer keyPEERS = 
		ByteBuffer.wrap(new byte[] { 'p', 'e', 'e', 'r', 's' });

	/** keyPEER_IP */
	public static final ByteBuffer keyPEER_IP = 
		ByteBuffer.wrap(new byte[] { 'i', 'p' });

	/** keyPEER_ID */
	public static final ByteBuffer keyPEER_ID = 
		ByteBuffer.wrap(new byte[] { 'p', 'e', 'e', 'r', ' ', 'i', 'd' });

	/** keyPEER_PORT */
	public static final ByteBuffer keyPEER_PORT = 
		ByteBuffer.wrap(new byte[] { 'p','o', 'r', 't' });

	/* ================================================================================ */
	/* 								Tracker Constructor									*/  
	/* ================================================================================ */
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



	/* ================================================================================ */
	/* 									METHODS  										*/  
	/* ================================================================================ */

	public void connect(int bytesDown, int bytesUp, int bytesRemaining, String event) throws IOException {

		/** Variables */
		Socket trkSocket = null;
		URL trkURL = null;
		HttpURLConnection trkConnection = null;

		DataInputStream trackerData;
		int size;

		byte[] trkDataByteArray = null;
		Map<ByteBuffer, Object> trkMapResponse = null;

		/** Verify Tracker was initialized */
		if (trackerUrl == null)
		{
			System.err.println("Tracker was not created properly. ");
		}

		/** Open socket in order to communicate with tracker */
		try
		{
			trkSocket = new Socket(trackerIP, trackerPort);
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Unable to create socket at " + trackerIP + ":" + trackerPort);
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
		}

		/** Receiving tracker response */
		try 
		{
			trackerData = new DataInputStream(trkConnection.getInputStream());
			size = trkConnection.getContentLength();
			trkDataByteArray = new byte[size];
			trackerData.readFully(trkDataByteArray);
			trackerData.close();

		} catch (IOException e) {
			System.err.println("Caught IOException: " + e.getMessage());
		}

		/** Decoding tracker byte Array response to Map  */
		try
		{
			trkMapResponse = (Map<ByteBuffer, Object>)Bencoder2.decode(trkDataByteArray); 
		}
		catch(BencodingException e)
		{
			System.err.println("Unable to decode tracker response. ");
		}

		/** Extract and set Info from tracker response */ 
		setPeerList(trkMapResponse);

	}


	/** Method: set peer list */
	public static void setPeerList(Map<ByteBuffer, Object> trackerResponse){

		/* Variables */
		String[] decodedTrkResponse;
		ArrayList<Object> pArray;
		ArrayList<Peer> pList;

		/** Extract interval */
		trackerInterval = (Integer)trackerResponse.get(keyINTERVAL);
		System.out.println("trackerInterval: " + trackerInterval);

		/** Decode tracker Map response to String[] */
		decodedTrkResponse = decodeCompressedPeers(trackerResponse);

		/** Extract peer */ 
		pArray = (ArrayList<Object>) trackerResponse.get(keyPEERS);

		try
		{
			for (int p = 0; p < pArray.size(); p++){
				Object peer = pArray.get(p);
				String ipNum = "";
				String peerIdNum = "";
				int peerPortNum = 0;

				Map<ByteBuffer, Object> pMap = (Map<ByteBuffer, Object>) peer;
				peerIdNum = bufferToString((ByteBuffer) pMap.get(keyPEER_ID));
				ipNum = bufferToString((ByteBuffer) pMap.get(keyPEER_IP));
				peerPortNum = (Integer) pMap.get(keyPEER_PORT);

				/* ================ */
				/* Print Statements */
				/* ================ */	
				System.out.println(keyPEER_IP + " " + keyPEER_ID + " " + keyPEER_PORT);

				/*
				Peer newPeer = new Peer(peerIdNum, ipNum, peerPortNum);

				if(newPeer.isValid()){
					peerList.add(newPeer);
					}
				 */

			}
		}
		catch(Exception e)
		{
			System.err.println("Error: Could not load peers into PeerList. ");
		}

		//peerList = pList;
	

		/* ================ */
		/* Print Statements */
		/* ================ */	
		/*
		System.out.println("Extracting PeerList from String Array");
		for(int i = 0; i < decodedTrkResponse.length; i++){
			System.out.println(decodedTrkResponse[i]);
		}
		*/
	}

	/* copy past copy paste */
	public static String bufferToString(ByteBuffer buffer) {
		byte[] bufferBytes = new byte[buffer.capacity()];
		buffer.get(bufferBytes, 0, bufferBytes.length);
		String value = new String(bufferBytes);
		return value;
	}

	/** Method: Create and return requested URL */
	public static URL newURL(int bytesDown, int bytesUp, int bytesRemaining, URL announceURL) {
		/* Variables */
		String newUrlString = "";

		/** Find a random port to connect */
		listeningPort = setPortNum();

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
	public static String[] decodeCompressedPeers(Map<ByteBuffer, Object> map){
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

	/* ================================================================================ */
	/* 									SET-METHODS  									*/  
	/* ================================================================================ */

	/** Method: Returns a port to connect on */
	public static int setPortNum() {
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

	/* ================================================================================ */
	/* 									GET-METHODS  									*/  
	/* ================================================================================ */

	public ArrayList<Peer> getPeerList(){
		return peerList;
	}

	public URL getTrackerUrl(){
		return trackerUrl;
	}

	public String getTrackerIP(){
		return trackerIP;
	}

	public int getTrackerPort(){
		return trackerPort;
	}

	public int getTrackerInterval(){
		return trackerInterval;
	}

}
