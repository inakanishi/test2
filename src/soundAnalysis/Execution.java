package soundAnalysis;


public class Execution {

	private static final int BITS = 16;
	private static final int HZ = 8000;
	private static final int MONO = 1;
	private static final double DetectionInterval = 0.1;


	public static void main(String arg[]){

		System.out.println("start sound manger");

		VolumeObserver VO = new VolumeObserver(HZ, BITS, MONO, DetectionInterval);

		VO.start();

		Player P = new Player(HZ, BITS, MONO, DetectionInterval);

		//P.start();

		while( true )
        {

                P.setVoice( VO.getVoice() );

        }

	}
}
