package RUBTClient;

import java.io.*;
import java.util.Random;
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

	/** Client Information */
	public static String peerID;

	/** Peer Information */
	public static String[] peer;
	
	/** Download Information */
	public int bytesDownloaded;
	public int bytesUploaded;
	public int bytesRemaining;
	public static String event;

	public static final int blockLength = 16384;
	public static int numPieces = 0;
	public static int numBlocks = 0;
	public static int numBlkPieceRatio = 0;
	public static int numBlkLastPiece = 0;
	
	


	/* ================================================================================ */
	/* 								RUBTClient Constructor								*/  
	/* ================================================================================ */
	
	RUBTClient(String firstArg, String secondArg){
		try{
			/** Extract information as String */
			torrentName = firstArg;
			destinationName = secondArg;

			/** Capture torrent File */
			torrentFile= new File(torrentName);

			/** Parse torrent and return TorrentInfo */
			torrent = torrent_parser(torrentFile);	

			/** Initialize upload/download/remaining Bytes */
			bytesDownloaded = 0;
			bytesUploaded = 0;
			bytesRemaining = torrent.file_length % blockLength;
			event = null;

			/** Set number of pieces */
			if (bytesRemaining == 0){
				numPieces = torrent.file_length / torrent.piece_length;
			}
			else{
				numPieces = torrent.file_length / torrent.piece_length + 1;
			}
			
			/** Set number of blocks */
			numBlocks = (int)Math.ceil(torrent.file_length / blockLength);
			
			/** Set blocks per piece */
			numBlkPieceRatio = torrent.piece_length / blockLength;
			
			/** Create peerID */
			peerID = setPeerId();

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


	/* ================================================================================ */
	/* 									MAIN	  										*/  
	/* ================================================================================ */
	
	public static void main(String[] args) throws IOException, BencodingException {

		/* Global Variables */
		RUBTClient client;
		TrackerGetr tracker;
		String localPeer ="";

		/** Check if valid number of arguments in the command line */ 
		if (!validateNumArgs(args)) {
			System.out.println("USAGE: RUBTClient [torrent-file-to-read] [file-name-to-save]");
			System.exit(1);
		}

		/** Initialize Client */
		client = new RUBTClient(args[0], args[1]);

		/** Initialize Tracker */
		tracker = new TrackerGetr(client, torrent);

		/** Connects to tracker */         
		tracker.connect(0,0,client.bytesRemaining, "started");

		/** Checks if connection was successful */
		/*
		if(trackerResponse == null){
			System.err.println("ERROR: Did not recieve tracker response. ");
		}
		*/
		
		/** Update peerList ??? */

		
		
		/** Get peerList */
		peer = tracker.getPeerList();
		
		/** Extract specific peer with ID: 128.6.171.3 */
		// for(i = 0 ...
		// if(peer[i] has same ID as 128.6.171.3 then,  
		// localPeer = peer[i]
		
		/** Start ProgramManager ("downloader" application thread) */
		//blah blah
		
	}
	
	

	/* ================================================================================ */
	/* 									METHODS  										*/  
	/* ================================================================================ */

	/** METHOD: Checks number of arguments in command line */
	public static boolean validateNumArgs(String[] args){
		if ((args.length != 2)) {
			System.err.println("ERROR: Invalid number of arguments");
			return false;
		}
		else
			return true;
	}


	/** METHOD: Parses Torrent File */
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

	/* ================================================================================ */
	/* 									SET-METHODS  									*/  
	/* ================================================================================ */

	/** METHOD: Set random peerID */
	public static String setPeerId(){

		/** Variables */
		String randomPeerID = "";
		Random randomSequence; 

		/** Bank of appropriate peer ID characters */
		char[] appropriateChars = 
		{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
				'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z',
				'0','1','2','3','4','5','6','7','8','9'};

		/** Initiate random sequence */
		randomSequence = new Random();

		/** First letter cannot be R */
		randomPeerID += appropriateChars[randomSequence.nextInt(61)];
		while (randomPeerID.equalsIgnoreCase("r")){
			randomPeerID = "";
			randomPeerID += appropriateChars[randomSequence.nextInt(61)];
		}

		/** Complete random sequence */
		for(int i = 1; i < 20; i++)
		{
			randomPeerID += appropriateChars[randomSequence.nextInt(61)];
		}

		return randomPeerID;
	}

	
	/* ================================================================================ */
	/* 									GET-METHODS  									*/  
	/* ================================================================================ */
	
	/** Retrieves TorrentInfo */
	public TorrentInfo getTorrentInfo(){
		return torrent;
	}

	/** Retrieves bytes downloaded */
	public int getBytesDownloaded(){
		return bytesDownloaded;
	}

	/** Retrieves bytes uploaded */
	public int getBytesUploaded(){
		return bytesUploaded;
	}

	/** Retrieves bytes left */
	public int getBytesRemaining(){
		return bytesRemaining;
	}

	/** Retrieves event */
	public String getEvent(){
		return event;
	}

	/** Retrieves number of pieces */
	public int getNumPieces(){
		return numPieces;
	}

	/** Retrieves peer ID */
	public String getPeerId(){
		return peerID;
	}

}
