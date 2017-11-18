package org.catpaw.jdm.util.net;

public abstract class IDownloader implements Runnable {

	public void start(INetConnector connector) {
		connector.process();
	}
	
}
