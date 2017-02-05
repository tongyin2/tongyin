/**
 * Created by Tong Yin on 2/4/2017.
 */
public class ArrayDeque<BB> {
    private BB[] items;
    private int size;
    private int nextFirst;
    private int nextLast;

    public ArrayDeque(){
        items = (BB[])new Object[8];
        size = 0;
        nextFirst = items.length-1;
        nextLast = size;
    }

    //resize the array list
    public void resize(int s){
        BB[] temp = (BB[])new Object[s];
        int sP, dP, csize;

        if(nextFirst < items.length-1 && nextFirst > 0){
            sP = nextFirst + 1;
            csize = items.length-sP;
            dP = temp.length-csize;
            System.arraycopy(items,sP,temp,dP,csize);
            System.arraycopy(items,0,temp,0,nextLast);

            nextFirst = dP-1;
        }else if(nextFirst == 0){
            sP = nextFirst + 1;
            csize = size;
            dP = temp.length-csize;
            System.arraycopy(items,sP,temp,dP,csize);

            nextFirst = dP-1;
        }else{
            sP = 0;
            csize = size;
            dP = 0;
            System.arraycopy(items,sP,temp,dP,csize);

            nextFirst = temp.length-1;
        }
        items = temp;
    }

    //adds an item to the front of the Deque
    public void addFirst(BB i){
        if(nextFirst == nextLast){
            resize((size+1)*2);
        }

        items[nextFirst] = i;
        if(nextFirst == 0){
            nextFirst=items.length-1;
        }else{
            nextFirst--;
        }

        size++;
    }

    //Adds an item to the back of the deque
    public void addLast(BB i){
        if(nextFirst == nextLast){
            resize((size+1)*2);
        }

        items[nextLast] = i;

        if(nextLast == items.length-1){
            nextLast = 0;
        }else{
            nextLast++;
        }

        size++;
    }

    //returns true if deque is empty
    public boolean isEmpty(){
        if(size == 0){
            return true;
        }else{
            return false;
        }
    }

    //returns the number of items in the deque
    public int size(){
        return size;
    }

    //prints the items in the deque from first to last separated by a space
    public void printDeque(){
        if(isEmpty()){
            System.out.println("arraylist is empty");
        }else{
            int position = nextFirst;
            int counter = size();
            while(counter!=0){
                if(position < items.length-1){
                    position = position+1;
                }else{
                    position = 0;
                }
                System.out.print(items[position]);
                System.out.print(" ");
                counter--;
            }
        }
    }

    //removes and returns the item at the back of the deque
    public BB removeFirst(){
        if(size() == 0){
            return null;
        }else {
            BB removedItem;
            if (nextFirst == items.length - 1) {
                nextFirst = 0;
            } else {
                nextFirst++;
            }
            size--;
            removedItem = items[nextFirst];

            if (size * 4 < items.length && items.length > 16) {
                resize(size + 1);
            }

            return removedItem;
        }
    }

    //removes and returns the item at the back of the deque
    public BB removeLast(){
        if(size()==0){
            return null;
        }else {
            BB removedItem;
            if (nextLast == 0) {
                nextLast = items.length - 1;
            } else {
                nextLast--;
            }
            size--;
            removedItem = items[nextLast];

            if (size * 4 < items.length && items.length > 16) {
                resize(size + 1);
            }

            return removedItem;
        }
    }

    //gets the item at the given inedx , where 0 is the front, 1 is the next item, and so forth
    public BB get(int index){
        if(index < size){
            if(index+nextFirst+1 > items.length){
                return items[index+nextFirst+1-items.length];
            }else{
                return items[index+nextFirst+1];
            }
        }else{
            return null;
        }
    }

    public static void main(String[] args){
        //test
        ArrayDeque A = new ArrayDeque();
        int n;
        for(n = 0; n < 31; n++){
            A.addLast(n);
        }
        for(n = 0; n < 10; n++){
            A.addFirst(n);
        }
        for(n = 0; n < 31; n++){
            A.removeLast();
        }
        A.printDeque();
    }
}
