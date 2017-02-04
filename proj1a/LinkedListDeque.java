/**
 * Created by Tong Yin on 2/3/2017.
 */
public class LinkedListDeque<BB> {
    private class IntNode {
        public BB item;
        public IntNode previous;
        public IntNode next;

        public IntNode(BB i, IntNode p, IntNode n) {
            item = i;
            previous = p;
            next = n;
        }
    }

    private IntNode sentinel;
    private int size;

    public LinkedListDeque(){
        sentinel = new IntNode(null, null, null);
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    public LinkedListDeque(BB i){
        sentinel = new IntNode(null, null, null);
        sentinel.previous = sentinel;
        sentinel.next = sentinel;

        sentinel.next = new IntNode(i, sentinel, sentinel);
        sentinel.previous = sentinel.next;
        size = 1;
    }

    //adds an item to the front of the deque
    public void addFirst(BB i){
        IntNode temp = sentinel.next;
        sentinel.next = new IntNode(i, sentinel, temp);
        temp.previous = sentinel.next;
        size = size+1;
    }

    //adds an item to the back of the Deque
    public void addLast(BB i){
        IntNode temp = sentinel.previous;
        sentinel.previous = new IntNode(i,temp,sentinel);
        temp.next = sentinel.previous;
        size = size+1;
    }

    //returns true if deque is empty, false otherwise
    public boolean isEmpty(){
        if(size==0){
            return true;
        }else{
            return false;
        }
    }

    //returns the number of items in the deque
    public int size(){
        return size;
    }

    //prints the items in the deque from first to last, separated by a space
    public void printDeque(){
        IntNode temp=sentinel;
        int count = size;
        while(count>0){
            System.out.print(temp.next.item);
            System.out.print(' ');
            temp = temp.next;
            count--;
        }
    }

    //removes and returns the item at the front of the deque
    public BB removeFirst(){
        if(this.isEmpty()) {
            return null;
        }else{
            IntNode temp = sentinel.next;
            sentinel.next = temp.next;
            temp.next.previous = sentinel;
            size = size-1;
            return temp.item;
        }
    }

    //removes and returns the item at the back of the deque
    public BB removeLast(){
        if(this.isEmpty()){
            return null;
        }else{
            IntNode temp = sentinel.previous;
            temp.previous.next = sentinel;
            sentinel.previous = temp.previous;
            size = size-1;
            return temp.item;
        }
    }

    //gets the item at the given index (iterative)
    public BB get(int index){
        if(index >= this.size()){
            return null;
        }else{
            int i;
            IntNode temp=sentinel;
            for(i = 0; i <= index; i++){
                temp=temp.next;
            }
            return temp.item;
        }
    }

    //same as get, but uses recursion
    public BB getRecursive(int index){
        if(index >= this.size()){
            return null;
        }else if(index == 0){
            return sentinel.next.item;
        }else{
            BB temp = this.removeFirst();
            BB x = getRecursive(index-1);
            this.addFirst(temp);
            return x;
        }
    }

    /*public static void main(String[] args) {

        LinkedListDeque<Integer> L = new LinkedListDeque();
        L.addFirst(1);
        L.addFirst(0);
        L.addLast(2);
        L.addLast(3);
        //L.removeFirst();
        //L.removeLast();
        System.out.println(L.get(3));
        System.out.println(L.getRecursive(3));
        L.printDeque();
    }*/

}
