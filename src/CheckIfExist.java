import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Mając tablicę int[] numbers, wypełnioną liczbami całkowitymi i posortowaną malejąco ( numbers[i] > numbers[i+1] ),
 * sprawdź czy występuje w niej liczba int x. Metoda powinna zwracać wartość TRUE, jeśli dana liczba występuje oraz
 * FALSE w przeciwnym wypadku. W rozwiązaniu zależy nam na jak najmniejszej złożoności obliczeniowej (priorytet) oraz
 * pamięciowej.
 * Podaj szacowaną złożoność obliczeniową oraz pamięciową.
 */

public class CheckIfExist {
    @Test
    public void testSearch(){
        int[] numbers = {10, 8, 7, 6, 4, 3, 2, 1};

        Assertions.assertFalse(search(numbers, 5));
        Assertions.assertTrue(search(numbers, 1));
    }

    private boolean search(int[] numbers, int x) {
        // check argument validation
        if (!numbers.getClass().getName().equals(int[].class.getName()))
            throw new IllegalArgumentException();

        // check if array is not empty
        if(numbers.length < 1)
            return false;

        // bisekcja
        int left = 0;
        int right = numbers.length - 1;
        int middle = right / 2;

        do {
            if (numbers[middle] == x)
                return true;

            if (numbers[middle] > x) {
                left = middle + 1;
            } else {
                right = middle - 1;
            }
            middle = (left + right) / 2;
        } while (left <= right);

        return false;
    }
}
