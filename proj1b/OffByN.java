/**
 * Created by Tong Yin on 2/9/2017.
 */
public class OffByN implements CharacterComparator{

    int n;

    public OffByN(int N) {
        n = N;
    }

    @Override
    public boolean equalChars(char x, char y) {
        if(Math.abs(x-y) == n) {
            return true;
        }else {
            return false;
        }
    }
}
