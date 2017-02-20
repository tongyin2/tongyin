import synthesizer.GuitarString;

import java.util.Objects;

/**
 * Created by Tong Yin on 2/19/2017.
 */

public class GuitarHero {
    private static final String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    //private static final double CONCERT_A = 440.0;
    //private static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        //synthesizer.GuitarString stringA = new synthesizer.GuitarString(CONCERT_A);
        //synthesizer.GuitarString stringC = new synthesizer.GuitarString(CONCERT_C);
        synthesizer.GuitarString[] stringx = new synthesizer.GuitarString[keyboard.length()];


        for (int i=0; i < stringx.length; i++) {
           stringx[i] = new synthesizer.GuitarString(440.0*Math.pow(2,(i-24)/12));
           System.out.println(440.0*Math.pow(2,(i-24)/2));
        }

        while (true) {

            /* check if the user has typed a key; if so, process it */
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                /*if (key == 'a') {
                    stringA.pluck();
                } else if (key == 'c') {
                    stringC.pluck();
                }*/
                int i = keyboard.indexOf(key);
                stringx[i].pluck();
            }

        /* compute the superposition of samples */
            //double sample = stringA.sample() + stringC.sample();
            double sample = 0.0;
            for (int i = 0; i < stringx.length; i++) {
                sample = sample + stringx[i].sample();
            }

        /* play the sample on standard audio */
            StdAudio.play(sample);

        /* advance the simulation of each guitar string by one step */
            //stringA.tic();
            //stringC.tic();
            for (int i=0; i < 37; i++) {
                stringx[i].tic();
            }
        }
    }
}
