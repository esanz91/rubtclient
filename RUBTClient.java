package RUBTClient;

import java.net.HttpURLConnection;
import java.io.*;
import java.util.Map;

/**
 * RUBTClient Class
 * Main Functions:
 * 1) Capture torrent FILE and destination FILE
 * 2) Create Client
 * 3) Initiate TrackerGtr
 * 4)
 */

public class RUBTClient {

	/* Public variables */
	
	/** Torrent File Information */
	public static String torrentName;
	public static File torrentFile;
	public static TorrentInfo torrent;
	
	/** Destination File Information */
	public static String destinationName;
	public static File destinationFile;
	
	/** File Information */
	public int bytesDownloaded;
	public int bytesUploaded;
	public int bytesRemaining;
	public static String event;
	
	/** Client Information */
	public static String peerID;

	
	
	
	
	/** Client Constructor */
	RUBTClient(String firstArg, String secondArg){
		try{
			/** Extract information as String */
			torrentName = firstArg;
			destinationName = firstArg;

			/** Capture torrent File */
			torrentFile= new File(torrentName);
			
			/** Parse torrent and return TorrentInfo */
			torrent = torrent_parser(torrentFile);	

			/** Initialize upload/download/remaining Bytes */
			bytesDownloaded = 0;
			bytesUploaded = 0;
			bytesRemaining = torrent.file_length;
			event = null;

			/** Create peerID */
			peerID = GeneratingFunctions.generatePeerId();
			
			/** Initialize BitSet */
			/** 
			 * 
			 */
		}
		catch (NullPointerException e){
			System.err.println("ERROR: Torrent File does not exist.");
			System.exit(1);
		}
		catch (Exception e){
			System.err.println("ERROR: Unable to initialize Client.");
			System.exit(1);
		}
	}
	
	
	
	
	
	/** MAIN */
	public static void main(String[] args) throws IOException, BencodingException {
		
		/* Global Variables */
		RUBTClient client;
		TrackerGetr Tracker;
		
		/** Check if valid number of arguments in the command line */ 
		if (!validateNumArgs(args)) {
			System.out.println("USAGE: RUBTClient [torrent-file-to-read] [file-name-to-save]");
			System.exit(1);
		}
		
		/** Initialize Client */
		client = new RUBTClient(args[0], args[1]);
		
		/** Initialize Tracker */
		Tracker = new TrackerGetr(client, torrent);
		
		/** connects to tracker and gets response and then decodes that
		 * response into an array of peers of the form <IP address>:<port>
		 */         
		byte[] tracker_response=Tracker.connect(torrent);
		Map compressedpeers=(Map)Bencoder2.decode(tracker_response);
		String[] peerslist= GeneratingFunctions.decodeCompressedPeers(compressedpeers);
	}
	
	/** Checks number of arguments in command line */
	public static boolean validateNumArgs(String[] args){
		if ((args.length != 2)) {
			System.err.println("ERROR: Invalid number of arguments");
			return false;
		}
		else
			return true;
	}
	
	/** Parses Torrent File */
	public static TorrentInfo torrent_parser(File torrent) {

		/* Variables */
		DataInputStream torrentReader;
		long torrentSize;
		byte[] torrentByteArray;
		TorrentInfo torrentData;

		try
		{
			/** Reads from torrent File into byte array */
			torrentReader= new DataInputStream(new FileInputStream(torrent));
			torrentSize=torrent.length();

			/** Checks size of the torrent File */
			if ((torrentSize > Integer.MAX_VALUE) || (torrentSize < Integer.MIN_VALUE)) {
				torrentReader.close();
				throw new IllegalArgumentException(torrentSize + " Torrent File is too large to process. ");
			}

			/** Reads bencoded torrent meta info into a byte array */
			torrentByteArray = new byte[(int)torrentSize];
			torrentReader.readFully(torrentByteArray);

			/** Creates and populates new TorrentInfo object  */
			torrentData= new TorrentInfo(torrentByteArray);
			torrentReader.close();

			return torrentData;
		}
		catch(FileNotFoundException e)
		{
			System.err.println("ERROR: Torrent File not found. ");
			return null;
		}
		catch(IOException e)
		{
			System.err.println("ERROR: Could not process torrent File. ");
			return null;
		}
		catch(BencodingException e)
		{
			System.err.println("ERROR: Unable to Bencode. ");
			return null;
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Unable to parse torrent File. ");
			return null;
		}
	}
	
	/* +++++++++++++++++++++++++++++++ GET METHODS +++++++++++++++++++++++++++++++++++ */
	public int getNumPieces(){
		return torrent.piece_hashes.length;
	}
	
	public String getPeerId(){
		return peerID;
	}
		
}
