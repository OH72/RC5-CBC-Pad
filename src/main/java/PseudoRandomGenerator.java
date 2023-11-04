/**
 * @author Oleh Hembarovskyi
 * @since 4/11/2023
 **/
public class PseudoRandomGenerator {
    private static final long M = 1023;
    private static final long A = 1;
    private static final long C = 0;
    private static final long X0 = 2;

    private long previousX;

    public PseudoRandomGenerator() {
        this.previousX = X0;
    }

    public long generateNext() {
        previousX = (A * previousX + C) % M;

        return previousX;
    }
}
