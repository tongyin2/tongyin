import static org.junit.Assert.*;
import org.junit.Test;
/**
 * Created by Tong Yin on 2/8/2017.
 */
public class TestArrayDequeLauncher {
    @Test
    public void PhaseOnePartOne(){
        StudentArrayDeque<Integer> student = new StudentArrayDeque<>();
        ArrayDequeSolution<Integer> solution = new ArrayDequeSolution<>();

        for(int i=0; i < 5; i++){
            Integer number = StdRandom.uniform(1,100);
            student.addLast(number);
            solution.addLast(number);
            assertEquals("addLast("+number+")",solution.get(i),student.get(i));
        }

        for(int i=0; i < 5; i++){
            Integer number = StdRandom.uniform(1,100);
            student.addFirst(number);
            solution.addFirst(number);
            assertEquals("addFirst("+number+")",solution.get(0),student.get(0));
        }

        for(int i=0; i < 5; i++){
            assertEquals("removeFirst(), operation "+i,solution.removeFirst(),student.removeFirst());
        }

        for(int i=0; i < 4; i++){
            assertEquals("removeLast(), operation "+i,solution.removeLast(),student.removeLast());
        }
    }

}
