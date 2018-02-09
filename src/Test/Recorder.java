package Test;

import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class Recorder extends Thread
{

	private static final int BITS = 16;
	private static final int HZ = 8000;
	private static final int MONO = 1;

	// リニアPCM 16bit 8000Hz x １秒
	private byte[] voice = new byte[HZ * BITS / 8 * MONO /10];
	private TargetDataLine target;
	private AudioInputStream stream;
	private AudioFormat     format  = null;

	public boolean g_bRecorder = false;

	// コンストラクタ
	Recorder()
	{
	    try
	    {
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

	    format = stream.getFormat();


	    g_bRecorder = true;
	}

	// スレッド実行
	public void run()
	{
		System.out.println("run record");

	    while( true )
	    {
	        if( !g_bRecorder ) return;
	        try
	        {
	            // ストリームから音声データを取得
	            stream.read( voice , 0, voice.length );

	        } catch (IOException e)
	        {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }


	        double values[] = new double[voice.length/2];

	        for (int i = 0; i < voice.length; i += 2) {
	        	short c;
	        	c = (short)(voice[i] & 0xff | (voice[i+1] << 8));
	        	//System.out.println("" + c);
	        	values[i/2] = (double)c/32768.0;
	        	//System.out.println(values[i/2]);
	        	}



	        //int values[] = new int[voice.length];
	        double value = 0;

	        for(int i=0; i<values.length;i++ ) {

	        //	double x = (double)voice[i]/(double)128;

	        	value +=  values[i]*values[i];

	        }

	        value = Math.sqrt(value/(double)voice.length);
	        //double rms = 20*Math.log10(value);

	        System.out.println(value);




            // 1標本分の値を取得
            //switch( format.getSampleSizeInBits() )
           // {
           //     case 8:
            //        value   = (int) voice[0];
            //        break;
            //    case 16:
            //        value   = (int) ByteBuffer.wrap( voice ).order( ByteOrder.LITTLE_ENDIAN ).getShort();
            //        break;
             //   default:
            //}
            //System.out.println(value);



	        // 一応、ウエイト
	        //try{
	        //    Thread.sleep( 100 );
	        //} catch (InterruptedException e) {
	        //    // TODO Auto-generated catch block
	        //    e.printStackTrace();
	        //}
	    }
	}

	// データ取得
	public byte[] getVoice()
	{
	    return voice ;
	}

	// 終了
	public void end()
	{
	    g_bRecorder = false;

	    // ターゲットデータラインを停止
	    target.stop();

	    // ターゲットデータラインを閉じる
	    target.close();
	}
}