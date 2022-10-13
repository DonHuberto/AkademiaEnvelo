import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class CheckIfExist {
    @Test
    public void testSearch(){
        int[] numbers = {10, 8, 7, 6, 4, 3, 2, 1};

        Assertions.assertFalse(search(numbers, 5));
        Assertions.assertTrue(search(numbers, 1));
    }

    private boolean search(int[] numbers, int x) {
        return false;
    }
}
