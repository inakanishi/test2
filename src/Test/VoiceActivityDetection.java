package Test;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class VoiceActivityDetection{

    // 定数
    private final String    fileName    = "music/dog01.wav";        // チャートに表示する音声ファイルへのパス
    private final double    sec         = 0.15;                     // チャートに表示する期間(s)

    // 取得する音声情報用の変数
    private AudioFormat     format  = null;
    private int[] values  = null;


	public static void main(String arg[]) throws Exception{
		VoiceActivityDetection VAD = new VoiceActivityDetection();

		//VAD.init();

		Recorder Rec = new Recorder();
		Rec.start();



	}

	public VoiceActivityDetection()
	{


	}












	public void soundReceptor() throws Exception {

        // 音声ストリームを取得
        File                file    = new File( fileName );
        AudioInputStream    is      = AudioSystem.getAudioInputStream( file );

        // メタ情報の取得
        format = is.getFormat();
        System.out.println( format.toString() );


        // 1標本分の値を取得
        int     size        = format.getFrameSize();
        byte[]  data        = new byte[ size ];
        int     readedSize  = is.read(data);


	}



    /**
     * 音声ファイルを読み込み、メタ情報とサンプリング・データを取得
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    public void init() throws Exception
    {
        // 音声ストリームを取得
        File                file    = new File( fileName );
        AudioInputStream    is      = AudioSystem.getAudioInputStream( file );

        // メタ情報の取得
        format = is.getFormat();
        System.out.println( format.toString() );

        // 取得する標本数を計算
        // 1秒間で取得した標本数がサンプルレートであることから計算
        int mount   = (int) ( format.getSampleRate() * sec );

        // 音声データの取得
        values      = new int[ mount ];
        for( int i=0 ; i<mount ; i++ )
        {
            // 1標本分の値を取得
            int     size        = format.getFrameSize();
            byte[]  data        = new byte[ size ];
            int     readedSize  = is.read(data);

            // データ終了でループを抜ける
            if( readedSize == -1 ){ break; }

            // 1標本分の値を取得
            switch( format.getSampleSizeInBits() )
            {
                case 8:
                    values[i]   = (int) data[0];
                    break;
                case 16:
                    values[i]   = (int) ByteBuffer.wrap( data ).order( ByteOrder.LITTLE_ENDIAN ).getShort();
                    break;
                default:
            }
            System.out.println(values[i]);
        }

        // 音声ストリームを閉じる
        is.close();
    }





}
