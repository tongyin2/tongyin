/**
 * Created by Tong Yin on 2/9/2017.
 */
public class Palindrome {
    public static Deque<Character> wordToDeque(String word) {
        ArrayDequeSolution<Character> L = new ArrayDequeSolution<>();
        for(int i = 0; i < word.length(); i++) {
            char a_char = word.charAt(i);
            L.addLast(a_char);
        }
        return L;
    }

    public static boolean isPalindrome(String word) {
        if(word.length() <= 1) {
            return true;
        }else{
            String first = Character.toString(word.charAt(0));
            String last = Character.toString(word.charAt(word.length()-1));
            if(first.equals(last)){
                String w = word.substring(1,word.length()-1);
                return isPalindrome(w);
            }else {
                return false;
            }
        }
    }

    public static boolean isPalindrome(String word, CharacterComparator cc){
        if(word.length() <= 1) {
            return true;
        }else {
            if(cc.equalChars(word.charAt(0),word.charAt(word.length()-1))) {
                return isPalindrome(word.substring(1,word.length()-1),cc);
            }else {
                return false;
            }
        }
    }

    public static void main(String[] args){
        String word = "abzed";
        OffByN c = new OffByN(3);
        if(isPalindrome(word,c)){
            System.out.println("it is off by "+c.n+" palindrome");
        }else {
            System.out.println("it's NOT off by "+c.n+" palindrome");
        }

    }
}
