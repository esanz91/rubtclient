package rubtclient;

import java.io.*;
public class TorrentInfoHandler {

		public static TorrentInfo torrent_parser(File torrent) throws IOException, BencodingException{
			
			//sets up a datainputstream to read from torrent file into byte array
			DataInputStream torrentreader= new DataInputStream(new FileInputStream(torrent));
			long filesize=torrent.length();
			//checks to see if the file given is too big for the program
			if (filesize > Integer.MAX_VALUE || filesize < Integer.MIN_VALUE) {
				torrentreader.close();
				throw new IllegalArgumentException(filesize + " is too large for this program");
				}
			
			//reads the bencoded torrent meta info into a byte array
			byte[] torrent_bytes= new byte[(int)filesize];
			torrentreader.readFully(torrent_bytes);
			
			//creates a new TorrentInfo object and populates it
			TorrentInfo Torrent= new TorrentInfo(torrent_bytes);
			torrentreader.close();
			
			System.out.println("The torrent has been succesfully parsed");
				
			return Torrent;
		}
	
	
	
	
}
