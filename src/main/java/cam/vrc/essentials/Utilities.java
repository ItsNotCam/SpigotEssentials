package cam.vrc.essentials;

public class Utilities {
    public static String toTitle(String s) {
        String[] words = s.toLowerCase().split("_");
        for(int i = 0; i < words.length; i++) {
            String word = words[i];
            words[i] = ((char) (word.charAt(0)-32)) + word.substring(1);
        }

        return String.join(" ", words);
    }

    public static boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A'
                || c == 'E' || c == 'I' || c == 'O' || c == 'U';
    }
}
