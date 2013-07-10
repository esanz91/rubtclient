package rubtclient;

import java.net.HttpURLConnection;
import java.io.*;
public class RUBTClient {

	/**
	 * @param args
	 * @throws BencodingException 
	 * @throws IOException 
	 */
	
	
	
	public static void main(String[] args) throws IOException, BencodingException {
		// TODO Auto-generated method stub
            
                if (args.length < 2) {
			System.out.println("USAGE: RUBTClient [torrent-file] [file-name]");
			System.exit(1);
		}
		String torrent_name;
		String save_name;
		
		torrent_name=args[0];
		File torr= new File(torrent_name);
		save_name=args[1];
		TorrentInfo torrent= TorrentInfoHandler.torrent_parser(torr);
                int port=Helper_Methods.getPort();
                byte[] peerid=Helper_Methods.
                

	}
	
}

