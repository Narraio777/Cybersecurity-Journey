package com.hypixelclient.util;

import java.util.Random;

/**
 * Human-like timing and variation helpers.
 * All modules that interact with the server should use these so their
 * patterns can't be distinguished from a real player.
 */
public final class Humanizer {
    private static final Random RNG = new Random();

    private Humanizer() {}

    /**
     * Gaussian delay centred between minMs and maxMs.
     * Values are clumped around the middle (like real human reaction times)
     * rather than spread uniformly, which is trivially detectable.
     */
    public static long gaussianDelay(long minMs, long maxMs) {
        double mean  = (minMs + maxMs) / 2.0;
        double sigma = (maxMs - minMs) / 5.0; // ~95% of values fall inside the range
        double val   = mean + RNG.nextGaussian() * sigma;
        return (long) Math.max(minMs, Math.min(maxMs, val));
    }

    /**
     * Returns a value jittered by ±spreadFraction of the base (Gaussian).
     * E.g. jitter(4.0f, 0.15) → roughly 3.4–4.6 most of the time.
     */
    public static float jitter(float base, double spreadFraction) {
        return base + (float)(RNG.nextGaussian() * base * spreadFraction);
    }

    /**
     * True with the given probability (0.0–1.0).
     * Use to randomly skip an action so it doesn't happen every single tick.
     */
    public static boolean chance(double probability) {
        return RNG.nextDouble() < probability;
    }

    /**
     * Random int in [min, max] inclusive, with slight gaussian bias toward the centre.
     */
    public static int gaussianInt(int min, int max) {
        double mean  = (min + max) / 2.0;
        double sigma = (max - min) / 5.0;
        double val   = mean + RNG.nextGaussian() * sigma;
        return (int) Math.round(Math.max(min, Math.min(max, val)));
    }
}
