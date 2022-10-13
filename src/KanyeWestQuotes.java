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
    private final static int MAX_ITERATIONS = 100;
    private final static Set<String> quotes = new HashSet<>();

    public static void main(String[] args) {
        play();
    }

    private static void play() {
        final String YES = "next";
        final String NO = "hell no";

        System.out.println("Objawiona krynica mądrości powiedziała:");

        try {
            System.out.println(getQuote());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            System.out.println("\nCzy jesteś gotów usłyszeć kolejne słowa wieszcza Ye? [" + YES + "/" + NO + "]");
            Scanner scanner = new Scanner(System.in);
            String userResponse = scanner.nextLine();

            // tylko dla odważnych
            // String userResponse = YES;

            if (userResponse.equalsIgnoreCase(YES)){
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
            }
            else
                System.out.println("Nie rozpoznano polecenia. Wpisz komendę: " + YES + " lub " + NO);
        }
    }

    private static String getQuote(int maxIterations) throws IOException {
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

    public static String getQuote() throws IOException {
        return getQuote(MAX_ITERATIONS);
    }

    private static String sendGet() throws IOException {
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
            System.out.println("GET request failed. Response code = " + responseCode);
            return "";
        }
    }

    private static String stripQuoteFromJSON(String jsonString) {
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

    private static boolean distinctQuotes(String quote) {
        if (!quotes.contains(quote)) {
            quotes.add(quote);
            return true;
        }
        return false;
    }
}
