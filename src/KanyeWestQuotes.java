import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
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

    public static void main(String[] args) {
        try {
            System.out.println(
                    stripQuoteFromJSON(
                            sendGet()
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
}
