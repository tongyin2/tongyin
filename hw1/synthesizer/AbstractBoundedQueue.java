package synthesizer;

/**
 * Created by Tong Yin on 2/18/2017.
 */
abstract public class AbstractBoundedQueue<T> implements BoundedQueue<T>{
    protected int fillCount;
    protected int capacity;

    public int capacity(){
        return capacity;
    }

    public int fillCount(){
        return fillCount;
    }
}
