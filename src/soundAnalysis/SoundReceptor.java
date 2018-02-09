package soundAnalysis;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class SoundReceptor extends Thread{

	// リニアPCM 16bit 8000Hz x １秒
	public static int BITS = 16;
	public static int HZ = 8000;
	public static int MONO = 1;
	public static double DetectionInterval = 1;

	public byte[] voice;
	private TargetDataLine target;
	public AudioInputStream stream;

	public boolean stateConstructor = false;

	// コンストラクタ
	public SoundReceptor() {

		init();
	}


	public SoundReceptor(int _HZ, int _BITS, int _MONO, double _DetectionInterval) {

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



	public void end() {

		stateConstructor = false;

		// ターゲットデータラインを停止
        target.stop();

        // ターゲットデータラインを閉じる
        target.close();

	}



}
