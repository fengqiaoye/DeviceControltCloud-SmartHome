package cooxm.devicecontrol.synchronize;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import cooxm.devicecontrol.encyco.TuringCatAesCrypto;

/** 
 * @author Chen Guanghua E-mail: richard@cooxm.com
 * @version Created：Jul 23, 2015 5:59:36 PM 
 */

public class HttpDownLoad {
	URL url;
	HttpURLConnection httpURLConnection;
	InputStream inputStream;
	RandomAccessFile outputStream;
	
	public String download (String... params){
		try {
			url = new URL(params[0]);
			httpURLConnection = (HttpURLConnection) url.openConnection();
			// 设置维持长连接
			httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
			// 设置连接服务器超时时间
			httpURLConnection.setConnectTimeout(600 * 1000);
			// 设置从服务器读取数据超时时间
			httpURLConnection.setReadTimeout(600 * 1000);
			// httpURLConnection.setAllowUserInteraction(true);


			File file = new File("d:/test/");
			if (!file.exists()) {
				file.mkdirs();
				// Runtime.getRuntime().exec("attrib +H "+ControlConsts.path);//隐藏文件夹
			}
			File outfile=new  File("d:/test/test.txt");
			if (outfile.exists()) {
				outfile.delete();
			}
			outfile.createNewFile();



			// 设置当前线程下载的起点，终点
			int length = httpURLConnection.getContentLength();
			int startPosition = 0;
			inputStream = httpURLConnection.getInputStream();
			// 使用java中的RandomAccessFile 对文件进行随机读写操作
			outputStream = new RandomAccessFile(outfile, "rw");
			outputStream.seek(startPosition);

			byte[] buf = new byte[1024 * 10];
			int read = 0;
			int curSize = startPosition;
			while (true) {
				read = inputStream.read(buf);
				if (read == -1) {
					break;
				}
				outputStream.write(buf, 0, read);
				curSize = curSize + read;
				// 当调用这个方法的时候会自动去调用onProgressUpdate方法，传递下载进度

				if (curSize == length) {
					break;
				}
				Thread.sleep(10);
			}
			inputStream.close();
			outputStream.close();
			httpURLConnection.disconnect();


		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return "";
	}
	
	public static void main(String[] args) {
		HttpDownLoad hd=new HttpDownLoad();
		Date start=new Date();
		//hd.download("http://120.24.81.23/keyfiles3/AC/codes/84.txt");
		//hd.download("http://172.16.45.99/gtzn/download.php");
//		String tes=AESUtil2.encodeAES("keyfiles3/AC/codes/84.txt","token_key");
//		String url="http://120.24.81.23/file/download.php?info="+tes;
		TuringCatAesCrypto crypto = new TuringCatAesCrypto();
		crypto.setToken("token_key");
		String dec="TjF3SW90NzcwUGZNRTQ2a2p0Qzl3WVJNa05OZmlCQ292K2hDb2VYaVFyRmJuZW04bzZ5V05DZlhoenc9";
		String de=crypto.decrypt(dec);
		System.out.println(de);
		String url="http://120.24.81.23/file/download.php?info="+dec;
		url="http://120.24.81.23:8080/HelloWorldServlet/hello?info=ejBLT2o1WjJveXJXbDAzNVdLSWk0amVtRnV1aEttYURvWWVjTm5nRForcEdWbmNWUHJXMHhOYW51SUE9";
		System.out.println(url);
		hd.download(url);
		
		System.out.println("finished in "+(new Date().getTime()-start.getTime())/1000 +" secods");
	}
	

}
