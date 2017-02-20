// TODO: Make sure to make this class a part of the synthesizer package
package synthesizer;
import java.util.ArrayList;
import java.util.Iterator;

//TODO: Make sure to make this class and all of its methods public
//TODO: Make sure to make this class extend AbstractBoundedQueue<t>
public class ArrayRingBuffer<T> extends AbstractBoundedQueue<T> {
    /* Index for the next dequeue or peek. */
    private int first;            // index for the next dequeue or peek
    /* Index for the next enqueue. */
    private int last;
    /* Array for storing the buffer data. */
    private T[] rb;

    /**
     * Create a new ArrayRingBuffer with the given capacity.
     */
    public ArrayRingBuffer(int capacity) {
        // TODO: Create new array with capacity elements.
        //       first, last, and fillCount should all be set to 0.
        first = 0;
        last = 0;
        fillCount = 0;
        //       this.capacity should be set appropriately. Note that the local variable
        this.capacity = capacity;
        //       here shadows the field we inherit from AbstractBoundedQueue, so
        //       you'll need to use this.capacity to set the capacity.
        rb = (T[]) new Object[capacity];
    }

    /**
     * Adds x to the end of the ring buffer. If there is no room, then
     * throw new RuntimeException("Ring buffer overflow"). Exceptions
     * covered Monday.
     */
    public void enqueue(T x) {
        // TODO: Enqueue the item. Don't forget to increase fillCount and update last.
        if (fillCount() == capacity()) {
            throw new RuntimeException("Ring Buffer Overflow");
        }else {
            if (last >= capacity()) {
                last = 0;
            }
            rb[last] = x;
            fillCount = fillCount + 1;
            last = last + 1;
        }

    }

    /**
     * Dequeue oldest item in the ring buffer. If the buffer is empty, then
     * throw new RuntimeException("Ring buffer underflow"). Exceptions
     * covered Monday.
     */
    public T dequeue() {
        // TODO: Dequeue the first item. Don't forget to decrease fillCount and update
        if (fillCount() == 0) {
            throw new RuntimeException("Ring Buffer Underflow");
        }else {

            T a = rb[first];
            if (first + 1 >= capacity()) {
                first = 0;
            } else {
                first = first + 1;
            }
            fillCount = fillCount - 1;
            return a;
        }

    }

    /**
     * Return oldest item, but don't remove it.
     */
    public T peek() {
        // TODO: Return the first item. None of your instance variables should change.


            return rb[first];

    }

    // TODO: When you get to part 5, implement the needed code to support iteration.
    public Iterator<T> iterator() {
        return new ARBiter();
    }

    private class ARBiter implements Iterator<T>{
        private int position;
        private int counter;

        public ARBiter() {
            position = first;
            counter = 0;
        }
        public T next() {
            T returnItem = rb[position];
            if (position + 1 >= capacity()) {
                position = 0;
            }else {
                position = position + 1;
            }
            counter = counter + 1;
            return returnItem;
        }

        public boolean hasNext() {
            return counter < fillCount();
        }
    }

}
