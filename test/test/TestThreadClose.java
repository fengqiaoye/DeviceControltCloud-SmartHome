package test;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;

public class TestThreadClose {

	public static void main(String[] args) throws Exception {
		final ServerSocket serverSocket = new ServerSocket(9000);
		try {
			while (true) {
				final Socket clientSocket = serverSocket.accept();
				System.err.println("Accepted connection from " + clientSocket);
				new Thread(new Runnable() {

					ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();

					@Override
					public void run() {
						Thread readThread = new Thread(new ReadThread(clientSocket, queue));
						readThread.start();
						Thread writeThread = new Thread(new WriteThread(clientSocket, queue));
						writeThread.start();
						while(true){
							if(!readThread.isAlive() && !writeThread.isAlive()){
								System.err.println("Client read/write thread stoped ...");
								break;
							}
						}
					}

				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			serverSocket.close();
		}
	}

}

class ReadThread implements Runnable {

	private Socket clientSocket;

	private ConcurrentLinkedQueue<String> queue;

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public ConcurrentLinkedQueue<String> getQueue() {
		return queue;
	}

	public void setQueue(ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
	}

	public ReadThread(Socket clientSocket, ConcurrentLinkedQueue<String> queue) {
		this.clientSocket = clientSocket;
		this.queue = queue;
	}

	@Override
	public void run() {
		System.err.println("Read thread started ...");
		while (null != clientSocket && !clientSocket.isClosed()) {
			BufferedReader bufferedReader;
			try {
				bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				String line = bufferedReader.readLine();
				if ("quit".equals(line)) {
					clientSocket.close();
				} else {
					queue.offer(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.err.println(new Date()+"Read thread stoped ...");
	}

}

class WriteThread implements Runnable {

	private Socket clientSocket;

	private ConcurrentLinkedQueue<String> queue;

	public Socket getClientSocket() {
		return clientSocket;
	}

	public void setClientSocket(Socket clientSocket) {
		this.clientSocket = clientSocket;
	}

	public ConcurrentLinkedQueue<String> getQueue() {
		return queue;
	}

	public void setQueue(ConcurrentLinkedQueue<String> queue) {
		this.queue = queue;
	}

	public WriteThread(Socket clientSocket, ConcurrentLinkedQueue<String> queue) {
		this.clientSocket = clientSocket;
		this.queue = queue;
	}

	@Override
	public void run() {
		System.err.println("Write thread started ...");
		while (null != clientSocket && !clientSocket.isClosed()) {
			if (!queue.isEmpty()) {
				String line = queue.poll();
				if (null != line && !line.isEmpty()) {
					try {
						PrintWriter printWriter = new PrintWriter(clientSocket.getOutputStream(), true);
						printWriter.write(line);
						printWriter.write("\r\n");
						printWriter.flush();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		System.err.println(new Date()+"Write thread stoped ...");
	}

}