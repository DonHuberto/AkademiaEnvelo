import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

/**
 * Napisz program, który wykorzysta API Kanye Rest https://kanye.rest/ by każdorazowo zaproponować nową perełkę mądrości
 * od Kanye Westa. Program powinien być obsługiwany z poziomu konsoli i obsługiwać komendę "next" by wywołać następny
 * cytat. Program nie potrzebuje oprawy graficznej. Zwróć uwagę na poprawną architekturę aplikacji oraz na czystość kodu.
 * Dla chętnych, za dodatkowe punkty: dodaj zapisywanie cytatów w pamięci, by upewnić się, że każdy kolejny cytat jest nowy.
 */

public class KanyeWestQuotes {
    private final static String API_URL = "https://api.kanye.rest/";
    private final static String RAW_QUOTES_URL = "https://raw.githubusercontent.com/ajzbc/kanye.rest/master/quotes.json";
    private static int MAX_ITERATIONS;
    private final static Set<String> quotes = new HashSet<>();

    public KanyeWestQuotes(){
        MAX_ITERATIONS = quotesSize();
    }


    public static void main(String[] args) {
        new KanyeWestQuotes().play();
    }

    /**
     * Start your path to enlightenment and open your mind to become wiser with every quote you get.
     */
    public void play(){
        play(true);
    }

    /**
     * Start your path to enlightenment and open your mind to become wiser with every quote you get.
     * @param debugMode developer mode, if enabled it turns off the need of use console commands
     */
    private void play(boolean debugMode) {
        final String YES = "next";
        final String NO = "hell no";

        System.out.println("Objawiona krynica mądrości powiedziała:");

        try {
            System.out.println(getQuote());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int count=1;
        while (true) {
            if(debugMode)
                System.out.print("Quotes = " + ++count + " || ");
            else
                System.out.println("\nCzy jesteś gotów usłyszeć kolejne słowa wieszcza Ye? [" + YES + "/" + NO + "]");

            Scanner scanner = new Scanner(System.in);
            String userResponse;

            // to tylko na potrzeby sprawdzenia poprawności połączenia
            if(debugMode)
                userResponse = YES;
            else
                userResponse = scanner.nextLine();

            if (userResponse.equalsIgnoreCase(YES)) {
                try {
                    System.out.println(getQuote());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                continue;
            }

            if (userResponse.equalsIgnoreCase(NO)) {
                System.out.println("Wróć gdy będziesz gotów...");
                break;
            } else
                System.out.println("Nie rozpoznano polecenia. Wpisz komendę: " + YES + " lub " + NO);
        }
    }

    /**
     * Returns quote as String.
     * @param maxIterations maximum number of tries to send GET request and received distinct quote in response
     */
    private String getQuote(int maxIterations) throws IOException {
        String quote;

        for (int i = 0; i < maxIterations; i++) {
            quote = stripQuoteFromJSON(
                    sendGet()
            );
            if (distinctQuotes(quote))
                return quote;
        }

        throw new WyschlaKrynicaMadrosciException();
    }

    public String getQuote() throws IOException {
        return getQuote(MAX_ITERATIONS);
    }

    /**
     * Send GET to https://api.kanye.rest/
     * If response code was OK (200) method returns response.
     */
    private String sendGet() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()
                    )
            );

            StringBuilder response = new StringBuilder();
            String inputLine;

            while ((inputLine = input.readLine()) != null) {
                response.append(inputLine);
            }
            input.close();

            return response.toString();
        } else {
            throw new IOException("GET request failed. Response code = " + responseCode);
        }
    }

    /**
     * Quotes are given in JSON format. This method strips quote value out of JSON String and returns it as String.
     * @param jsonString JSON String given as response to GET request to https://api.kanye.rest/
     */
    private String stripQuoteFromJSON(String jsonString) {
        if (jsonString == null)
            return "";

        String quote = jsonString
                .replaceAll(Pattern.compile(".*[{]").pattern(), "")
                .replaceAll(Pattern.compile("[}].*").pattern(), "");

        if (quote.isEmpty())
            return "";

        Map<String, String> jsonMap = Stream.of(quote)
                .map(str -> str.split(":"))
                .collect(toMap(str -> str[0], str -> str[1]));

        if (jsonMap.isEmpty())
            return "";

        return jsonMap.values().stream().findFirst().get();
    }

    /**
     * Checks if quote was already used.
     * If no: quote is added to set and returns true.
     * If yes: returns false.
     */
    private boolean distinctQuotes(String quote) {
        if (!quotes.contains(quote)) {
            quotes.add(quote);
            return true;
        }
        return false;
    }

    /**
     *  All quotes are taken from: https://github.com/ajzbc/kanye.rest/blob/master/quotes.json
     *  Returns number of quotes
     */
    private int quotesSize() {
        try {
            URL url = new URL(RAW_QUOTES_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader input = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()
                        )
                );
                int count = 0;
                String line;
                while ((line = input.readLine()) != null) {
                    if (line.contains("\""))
                        count++;
                }
                input.close();

                return count;

            } else {
                System.out.println("GET request failed. Response code = " + responseCode);
                return -1;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}