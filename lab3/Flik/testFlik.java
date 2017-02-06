import static org.junit.Assert.*;
import  org.junit.Test;
/**
 * Created by Tong Yin on 2/6/2017.
 */
public class testFlik {
    @Test
    public void testSameNumber() {
        boolean act = Flik.isSameNumber(128,128);
        boolean exp = true;
        assertEquals(exp,act);
    }
}