package xyz.maksimenko.smssearch;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by smaksimenko on 16.02.2016.
 */
public class Search {
    private String searchString;
    private String searchStringTranslited;
    private Set<String> keywords = new HashSet<String>();
    private Set<String> keywordsTranslit = new HashSet<String>();
    private static final Pattern specificPattern = Pattern.compile("\".+?\"");

    private static final Map<Character,Character> translit = new HashMap<Character, Character>();
    private static final Map<Character,Character> translitReversed = new HashMap<Character, Character>();

    static {
        translit.put('а', 'a'); translitReversed.put('a', 'а');
        translit.put('б', 'b'); translitReversed.put('b', 'б');
        translit.put('в', 'v'); translitReversed.put('v', 'в');
        translit.put('г', 'g'); translitReversed.put('g', 'г');
        translit.put('д', 'd'); translitReversed.put('d', 'д');
        translit.put('е', 'e'); translitReversed.put('e', 'е');
        translit.put('ё', 'e');
        translit.put('ж', 'z');
        translit.put('з', 'z'); translitReversed.put('z', 'з');
        translit.put('и', 'i'); translitReversed.put('i', 'и');
        translit.put('й', 'j'); translitReversed.put('j', 'й');
        translit.put('к', 'k'); translitReversed.put('k', 'к');
        translit.put('л', 'l'); translitReversed.put('l', 'л');
        translit.put('м', 'm'); translitReversed.put('m', 'м');
        translit.put('н', 'n'); translitReversed.put('n', 'н');
        translit.put('о', 'o'); translitReversed.put('o', 'о');
        translit.put('п', 'p'); translitReversed.put('p', 'п');
        translit.put('р', 'r'); translitReversed.put('r', 'р');
        translit.put('с', 's'); translitReversed.put('s', 'с');
        translit.put('т', 't'); translitReversed.put('t', 'т');
        translit.put('у', 'u'); translitReversed.put('u', 'у');
        translit.put('ф', 'f'); translitReversed.put('f', 'ф');
        translit.put('х', 'h'); translitReversed.put('h', 'х');
        translit.put('ч', 'c');
        translit.put('ш', 's'); translitReversed.put('s', 'ц');
        translit.put('щ', 's');
        translit.put('ц', 'c'); translitReversed.put('c', 'ц');
        translit.put('ъ', '\'');
        translit.put('ь', '\''); translitReversed.put('\'', 'ь');
        translit.put('э', 'e');
        translit.put('ю', 'u'); translitReversed.put('u', 'ю');
        translit.put('я', 'a');
    }

    public Search(String searchString){
        this.searchString = searchString;
        this.searchStringTranslited = translitString(searchString);
        createKeywordsSet();
    }

    private String translitString(String inputString) {
        byte numoffails;
        String result = "";
        for(short i = 0; i < inputString.length(); i++){
            Character symb = translit.get(inputString.charAt(i));
            numoffails = 0;
            if(symb == null) {
                numoffails++;
                symb = translitReversed.get(inputString.charAt((i)));
                if (symb == null) {
                    numoffails++;
                } else {
                    numoffails = 0;
                    result += symb;
                }
            } else {
                result+= symb;
            }
            if(numoffails > 2) {
                return "";
            }
        }
        return result;
    }

    /**
     * fills keywords set, considering qoutes
     */
    private void createKeywordsSet(){
        Matcher m = specificPattern.matcher(searchString);
        while(m.find()) {
            String foundItem = m.group();
            keywords.add(foundItem.substring(1, foundItem.length() - 2));
            searchString.replace(foundItem, "");
        }
        keywords.addAll(Arrays.asList(searchString.split(" ")));

        m = specificPattern.matcher(searchStringTranslited);
        while(m.find()) {
            String foundItem = m.group();
            System.out.println(foundItem.substring(1, foundItem.length() - 1));
            searchStringTranslited.replace(foundItem, "");
        }
        keywordsTranslit.addAll(Arrays.asList(searchStringTranslited.split(" ")));
    }

    public boolean match(String text){
        Iterator<String> it = keywords.iterator();
        while(it.hasNext()){
            String toLower = text.toLowerCase(new Locale("ru", "RU"));
            String keyword = it.next().toLowerCase(new Locale("ru", "RU"));
            if(!toLower.contains(keyword)) {
                return matchTranslited(text);
            }
        }
        return true;
    }

    public String getSearchString() {
        return searchString;
    }

    private boolean matchTranslited(String text){

        Iterator<String> it = keywordsTranslit.iterator();
        while(it.hasNext()){
            String toLower = text.toLowerCase(new Locale("ru", "RU"));
            String keyword = it.next().toLowerCase(new Locale("ru", "RU"));
            if(!toLower.contains(keyword)) {
                return false;
            }
        }
        return true;
    }
}
