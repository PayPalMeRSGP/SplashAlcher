import java.util.Random;

public class ConstantsAndStatics {

    public static final double RS_GAME_TICK_MS = 603;
    public static final double BETWEEN_ALCH_STDDEV_MS = 20;
    public static final double BETWEEN_ALCH_MEAN_MS = 215;


    public static long randomNormalDist(double mean, double stddev){
        long debug = (long) ((new Random().nextGaussian() * stddev + mean));
        return Math.abs(debug);
    }
}
