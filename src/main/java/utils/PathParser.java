package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * La classe rappresenta uno strumento utile per effettuare il parsing
 * degli URL e delle query string.
 */
public class PathParser {
    ArrayList<String> pathTokens;
    HashMap<String, String> queryString;

    public PathParser(String path, Map<String, String[]> queryString) {
        this.pathTokens = new ArrayList<>();
        this.queryString = new HashMap<>();
        if (path.charAt(path.length() - 1 ) != '/') {
            path += '/';
        }
        Pattern namesFinders = Pattern.compile("(?<=\\/)(.*?)(?=\\/)");
        Matcher matcher = namesFinders.matcher(path);
        while(matcher.find()) {
            this.pathTokens.add(matcher.group());
        }
        queryString.forEach((key, value) -> {
            this.queryString.put(key, value[0]);
        });
    }

    /**
     * Restituisce tutti i livello dell'URL. IL primo da sinistra
     * è identificato dall'indice 0, mentre l'ultimo a destra avrà
     * indice pathTokens.size() - 1. Il primo livello è il primo
     * componente dopo l'endpoint.
     * 
     * @return livelli dell'URL
     */
    public ArrayList<String> getPathTokens() {
        return pathTokens;
    }

    /**
     * Restituisce una hashMap contenente tutti i parametri della
     * query string. I parametri sono accessibili come coppie
     * chiave-valore.
     * 
     * @return parametri della query string
     */
    public HashMap<String, String> getQueryString() {
        return queryString;
    }
}