package soundAnalysis;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Player extends Thread
{
    private static final int BITS = 16;
    private static final int HZ = 8000;
    private static final int MONO = 1;

    // リニアPCM 16bit 8000Hz x １秒
    private byte[] voice;
    private SourceDataLine source;

	public boolean stateConstructor = false;

    // コンストラクタ
	public Player() {

		init(HZ, BITS, MONO, 1);
	}


	public Player(int _HZ, int _BITS, int _MONO, double _DetectionInterval) {

		init(_HZ, _BITS, _MONO, _DetectionInterval);
	}



    public void init(int _HZ, int _BITS, int _MONO, double _DetectionInterval) {
        try
        {

        	voice = new byte[(int)(_HZ * _BITS / 8 * _MONO * _DetectionInterval)];

            // オーディオフォーマットの指定
            AudioFormat linear = new AudioFormat( _HZ, _BITS, _MONO, true, false );

            // ソースデータラインを取得
            DataLine.Info info = new DataLine.Info( SourceDataLine.class, linear );
            source = (SourceDataLine)AudioSystem.getLine( info );

            // ソースデータラインを開く
            source.open( linear );

            // スピーカー出力開始
            source.start();

        } catch (LineUnavailableException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        stateConstructor = true;

    }

    // スレッド実行
    public void run()
    {
        while( true )
        {
            if( !stateConstructor ) return;

            // スピーカーに音声データを出力
            source.write( voice, 0, voice.length );

        }
    }

    // データ設定
    public void setVoice( byte[] b )
    {
        voice = b;
    }

    // 終了
    public void end()
    {
    	stateConstructor = false;

        // ソースデータラインを停止
        source.stop();

        // ソースデータラインを閉じる
        source.close();
    }
}