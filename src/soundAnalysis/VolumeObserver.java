package soundAnalysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import Util.Log;

public class VolumeObserver extends Thread{

	// リニアPCM 16bit 8000Hz x １秒
	public static int BITS = 16;
	public static int HZ = 8000;
	public static int MONO = 1;
	public static double DetectionInterval = 0.1;

	public byte[] voice;
	private TargetDataLine target;
	public AudioInputStream stream;

	public boolean stateConstructor = false;

    public double calibrationTime = 10.0;
    public double detectTime = 0.5;
    public double lostTime = 1.0;



	public VolumeObserver(){

		init();

	}

	public VolumeObserver(int _HZ, int _BITS, int _MONO, double _DetectionInterval){

		HZ = _HZ;
		BITS = _BITS;
		MONO = _MONO;
		DetectionInterval = _DetectionInterval;


		init();

	}


	public void init()
	{
	    try
	    {
	    	//
	    	voice = new byte[(int)(HZ * BITS / 8 * MONO * DetectionInterval)];

	        // オーディオフォーマットの指定
	        AudioFormat linear = new AudioFormat( HZ, BITS, MONO, true, false );

	        // ターゲットデータラインを取得
	        DataLine.Info info = new DataLine.Info( TargetDataLine.class, linear );
	        target = (TargetDataLine)AudioSystem.getLine( info );

	        // ターゲットデータラインを開く
	        target.open( linear );

	        // マイク入力開始
	        target.start();

	        // 入力ストリームを取得
	        stream = new AudioInputStream( target );

	    } catch (LineUnavailableException e)
	    {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }

	    //System.out.println("run record");
	    stateConstructor = true;
	}




	public void run(){

		new Log("run volume observer");

        int init_count = 0;
        boolean Detect = false;
        double volumeThreshold = 0;
        double volumeMaxThreshold = 0;
        boolean Listening = false;

        ArrayList<Double> volumes = new ArrayList<Double>();
        ArrayList<Double> volumes2 = new ArrayList<Double>();


        int detectNum = (int)(detectTime/DetectionInterval);
        int lostNum = (int)(lostTime/DetectionInterval);


	    while( true )
	    {
	        if( !stateConstructor ) return;
	        try
	        {
	            // ストリームから音声データを取得
	            stream.read( voice , 0, voice.length );

	        } catch (IOException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }


	        double rms = 0;

	        if(false) {

	        	// byte(8bit)配列にある16bitデータを正規化-1~+1)
	        	double values[] = new double[voice.length/2];


		        for (int i = 0; i < voice.length; i += 2) {
		        	short c;
		        	c = (short)(voice[i] & 0xff | (voice[i+1] << 8));
		        	//c = (short)(voice[i] << 8 | (voice[i+1] & 0xff));
		        	//c = (int) ByteBuffer.wrap( voice ).order( ByteOrder.LITTLE_ENDIAN ).getShort();
		        	//System.out.println(" " + c);
		        	values[i/2] = (double)c/32768.0;
		        	//System.out.println(values[i/2]);
		        	}

		        // RMC形式で平滑化
		        double value = 0;

		        for(int i=0; i<values.length;i++ ) {
		        	value +=  values[i]*values[i];
		        }

		        value = Math.sqrt(value/(double)values.length);
		        rms = 20*Math.log10(value);




	        }else {

	        	// 最大振幅をとる
		        for (int i = 0; i < voice.length; i += 2) {
		        	short c;
		        	c = (short)(voice[i] & 0xff | (voice[i+1] << 8));

		        	if(Math.abs(c)>rms)
		        		rms = Math.abs(c);
		        }
		        rms /=100; // 値を縮小

	        	volumes2.add(rms);
	        	if(volumes2.size()> detectNum + lostNum)
	        		volumes2.remove(0);

	        }


	        //System.out.println(rms);



	        // 平常値と発話値を推測しつつ、発話検知の閾値の同定

	        // 一定時間キャリブレーション
	        if(calibrationTime>(double)init_count*DetectionInterval) {

	        	if(init_count==0) new Log("Calibration Start. Wait the second: " + calibrationTime);
	        	new Log(init_count + " " + rms, false);
	        	init_count++;
	        	volumes.add(rms);

	        // 発話検出閾値と外れ値用閾値の計算
	        }else if(!Detect){

	        	volumeMaxThreshold = maxThreshold(volumes);
	        	volumeThreshold = voiceThreshold(volumes, volumeMaxThreshold);
	        	Detect = true;

	        // 値の判定
	        }else {

	        	//if(volumeThreshold<rms && rms<volumeMaxThreshold)
	        	//	new Log("detect sound: " + rms + ">" + volumeThreshold, false);
	        		//System.out.println("detect sound: " + rms + ">" + volumeThreshold);
	        	//if(volumeMaxThreshold<rms)
	        	//	new Log("detect sound: " + rms + ">" + volumeMaxThreshold + " but too large!", false);
	        		//System.out.println("detect sound: " + rms + ">" + volumeMaxThreshold + " but too large!");
	        	//else
	        		//new Log(String.valueOf(rms), false);
	        		//System.out.println(rms);


	        	if(!Listening) {

		        	Listening = true;
		        	double mean = 0;

		        	for(int i=volumes2.size()-1; i>volumes2.size()-detectNum-1; i--) {

		        		mean += volumes2.get(i);

		        		if(volumeThreshold>volumes2.get(i))
		        			Listening = false;
		        	}
		        	if(Listening)
		        		new Log("Detect Voice!!: " + String.format("%.2f", mean/(double)detectNum) + " > " + String.format("%.2f",volumeThreshold));

	        	}else {

		        	Listening = false;
		        	double mean = 0;

		        	for(int i=volumes2.size()-1; i>volumes2.size()-lostNum-1; i--) {

		        		mean += volumes2.get(i);

		        		if(volumeThreshold<volumes2.get(i))
		        			Listening = true;
		        	}
		        	if(!Listening)
		        		new Log("Lost Voice: " + String.format("%.2f", mean/(double)lostNum) );

	        	}
	        }

	    }
	}


	// データ取得
	public byte[] getVoice()
	{
	    return voice ;
	}


	public void end() {

		stateConstructor = false;

		// ターゲットデータラインを停止
        target.stop();

        // ターゲットデータラインを閉じる
        target.close();

	}


	public double maxThreshold(ArrayList<Double> array) {

		Collections.sort(array);

		//new Log("calculate maxThreshold by IQR");

		// IQRを利用して、外れ値のための上限を計算
		double IQR = array.get(array.size()/4*3) - array.get(array.size()/4);
		double maxValue = IQR*2 + array.get(array.size()/4);
		String log = "calculate maxThreshold by IQR\r\n";
		log += ("size: " + array.size() + "\r\n");
		log += ("size14: " + array.size()/4 + ": " + array.get(array.size()/4) + "\r\n");
		log += ("size34: " + array.size()/4*3 + ": " + array.get(array.size()/4*3) + "\r\n");
		log += ("min: " + array.get(0) + "\r\n");
		log += ("max: " + array.get(array.size()-1) + "\r\n");
		log += ("maxThreshold: " + String.format("%.2f",maxValue));
		new Log(log);

		return  maxValue;

	}



	public double voiceThreshold(ArrayList<Double> array, double maxValue) {

		Collections.sort(array);

		// 外れ値を除外
		for(int i=array.size()-1; i>=0; i--) {

			if(array.get(i)>maxValue) {
				new Log("remove: " + i + " : " + array.get(i), false);
				array.remove(i);
			}
		}

		// k-means で閾値を計算
		ArrayList<Double> upperCluster = new ArrayList<Double>();
        ArrayList<Double> lowerCluster = new ArrayList<Double>();

        for(int i = 0; i<array.size()/2; i++)
        	lowerCluster.add(array.get(i));

        for(int i = array.size()/2; i<array.size(); i++)
        	upperCluster.add(array.get(i));


        double threshold = 0, preThreshold = 0;
        int count = 0;
        while(true) {
        //for(int j=0; j<10; j++) {

	        double lowerMean=0, upperMean=0;

	        for(int i = 0; i<lowerCluster.size(); i++)
	        	lowerMean += lowerCluster.get(i);
	        lowerMean /= (double)lowerCluster.size();

	        for(int i = 0; i<upperCluster.size(); i++)
	        	upperMean += upperCluster.get(i);
	        upperMean /= (double)upperCluster.size();

	        threshold = (lowerCluster.get(lowerCluster.size()-1)+upperCluster.get(0))/2;
	        new Log("temp threshold: " + threshold, false);
	        if(preThreshold == threshold)
	        	break;
	        else if(count>array.size()/2)
	        	break;
	        preThreshold = threshold;
	        count++;
	        //if(threshold>1)break;


	        for(int i = 0; i<lowerCluster.size(); i++) {

	        	if( Math.abs(lowerCluster.get(i)-lowerMean) > Math.abs(lowerCluster.get(i)-upperMean)) {
	        		upperCluster.add(lowerCluster.get(i));
	        		lowerCluster.remove(i);
	        		i--;
	        	}
	        }

	        for(int i = 0; i<upperCluster.size(); i++) {

	        	if( Math.abs(upperCluster.get(i)-lowerMean) < Math.abs(upperCluster.get(i)-upperMean)) {
	        		lowerCluster.add(upperCluster.get(i));
	        		upperCluster.remove(i);
	        		i--;
	        	}
	        }

        }

        new Log("voice threshold: " + String.format("%.2f",threshold));
		return threshold;
	}

}
