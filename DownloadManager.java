package RUBTClient;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Vector;



/** 
 * STEPS: 
 * 1) Perform handshake 
 * 2) Send interested message 
 * 3) Wait for unchoke 
 * 4) Unchoke, start sequentially requesting blocks of data
 * 5) When a piece is complete, verify it sends a "have message" and writes to "destination" File
 * 
 */


public class DownloadManager extends Thread{

	private TrackerGetr tracker;
	private TorrentInfo torrent;
	private ArrayList<Peer> peerList;
	private RUBTClient client;
	private boolean stillRunning;
	
	public DownloadManager(RUBTClient r,TrackerGetr t)
	{
		client = r;
		torrent = r.getTorrentInfo();
		tracker = t;
		peerList = t.getPeerList();
		stillRunning = true;
	}

	/** METHOD: Running the program */
	public void run(){
		/* Extracts peer needed */
		Peer peer = peerList.get(0);
		
		/* Set up connection with Peer */
		peer.setPeerConnection();
		
		/* Establish handshake */
		peer.sendHandshake(client.getPeerId(), torrent.info_hash);
		
		/* Receive and verify handshake */
		if(!peer.verifyHandshake(torrent.info_hash)){
			System.err.println("ERROR: Unable to verify handshake. ");
		}
		else{
			int len = peer.getPeerResponseInt();
		}
	}

	
	/* +++++++++++++++++++++++++++++++ GET METHODS +++++++++++++++++++++++++++++++++++ */	
	
	ArrayList<Peer> getPeerList()
	{
		return peerList;
	}
	
}
