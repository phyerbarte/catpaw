package org.catpaw.jdm.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HttpsURLConnection;

public class HttpDownloader extends Thread {

	private int THREADS_NUM = 1;
	private int BUFFER_SIZE = 1024;
	private String urlStr;

	private long startPos;
	private long endPos;

	public static void main(String[] args) {
		System.out.println(new File("/").getAbsolutePath());
		new HttpDownloader().downloadFile(
				"https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/cache/static/protocol/https/sug/js/bdsug_async_5aba0a9.js");
	}

	public void init(String urlStr, long startPos, long endPos) throws Exception {
		this.urlStr = urlStr;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public void run() {

		try {
			URL url = new URL(urlStr);
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
			conn.setAllowUserInteraction(true);
//			conn.setRequestProperty("Range", "bytes=" + startPos + "-" + endPos);
//			conn.setRequestProperty("Accept-Encoding", "gzip");

//			BufferedReader bis = new BufferedReader(new InputStreamReader(new GZIPInputStream(conn.getInputStream())));
			BufferedReader bis = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			RandomAccessFile fos = new RandomAccessFile(new File("/temp/test"), "rw");

			String inputLine;
			String html = "";
			while ((inputLine = bis.readLine()) != null) {
				html += inputLine + "\n";
			}
			
			System.out.println(html);

//			fos.seek(startPos);

			// long curPos = startPos;
			// byte[] buf = new byte[1024];
			// while (curPos < endPos) {
			// int len = bis.read(buf, 0, BUFFER_SIZE);
			// if (len == -1) {
			// break;
			// }
			// fos.write(buf, 0, len);
			// curPos = curPos + len;
			//
			// long readByte = 0;
			//
			// if (curPos > endPos) {
			// readByte += len - (curPos - endPos) + 1;
			// } else {
			// readByte += len;
			// }
			//
			// System.out.println(new String(buf, 0, len));
			// }
			bis.close();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void downloadFile(String urlStr) {

		try {

			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("Accept-Encoding", "gzip");
			long fileLength = conn.getContentLengthLong();

			System.out.println(fileLength);

			long totalSize = fileLength / THREADS_NUM;

			for (int i = 0; i < THREADS_NUM; i++) {
				long startPos = totalSize * i + 1;
				long endPos = totalSize * (i + 1);

				HttpDownloader dl = new HttpDownloader();
				dl.init(urlStr, startPos, endPos);
				dl.start();
			}

		} catch (Exception ioe) {

			System.out.println("Oops- an IOException happened.");
			ioe.printStackTrace();
			System.exit(1);

		} finally {

		}
	}

}
