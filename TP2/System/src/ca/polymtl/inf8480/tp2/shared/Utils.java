package ca.polymtl.inf8480.tp2.shared;

import java.io.Serializable;

/**
 * Methodes utilitaires pour effectuer les utils du TP2.
 * <p>
 * L'implementation des utils est volontairement non-optimale.
 *
 * @author Simon Delisle et Francois Doray
 */
public class Utils implements Serializable {

    public static int pell(int x) {
        if (x == 0)
            return 0;
        if (x == 1)
            return 1;
        return 2 * pell(x - 1) + pell(x - 2);
    }

    public static int prime(int x) {
        int highestPrime = 0;

        for (int i = 1; i <= x; ++i) {
            if (isPrime(i) && x % i == 0 && i > highestPrime)
                highestPrime = i;
        }

        return highestPrime;
    }

    private static boolean isPrime(int x) {
        if (x <= 1)
            return false;

        for (int i = 2; i < x; ++i) {
            if (x % i == 0)
                return false;
        }

        return true;
    }
}
