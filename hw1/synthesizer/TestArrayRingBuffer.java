package synthesizer;
import org.junit.Test;
import static org.junit.Assert.*;

/** Tests the ArrayRingBuffer class.
 *  @author Josh Hug
 */

public class TestArrayRingBuffer {
    @Test
    public void someTest() {
        ArrayRingBuffer<Integer> arb = new ArrayRingBuffer<>(4);
        arb.enqueue(1); //1
        arb.enqueue(2); //1 2
        arb.enqueue(3); //1 2 3
        arb.enqueue(4); //1 2 3 4
        arb.dequeue(); //1 2 3 4
        arb.enqueue(5);//5 2 3 4
        arb.dequeue();// 5 2 3 4
        arb.enqueue(6); //5 6 3 4
        assertEquals((Integer) 3, arb.peek());

        for (int i : arb) {
            System.out.println(i);
        }

    }

    /** Calls tests for ArrayRingBuffer. */
    public static void main(String[] args) {
        jh61b.junit.textui.runClasses(TestArrayRingBuffer.class);
    }
} 
