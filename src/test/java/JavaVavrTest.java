import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;

import static io.vavr.API.Left;
import static io.vavr.API.Right;
import static org.junit.jupiter.api.Assertions.*;

class JavaVavrBasicTest {

    @Test
    void option_basic() {
        Option<String> value = Option.of("hej");
        Option<String> empty = Option.of(null);

        assertTrue(value.isDefined());
        assertEquals("hej", value.get());

        assertTrue(empty.isEmpty());
    }

    @Test
    void try_basic() {
        Try<Integer> success = Try.of(() -> Integer.parseInt("123"));
        Try<Integer> failure = Try.of(() -> Integer.parseInt("abc"));

        assertTrue(success.isSuccess());
        assertEquals(123, success.get());

        assertTrue(failure.isFailure());
        assertEquals(0, failure.getOrElse(0));
    }

    @Test
    void either_basic() {
        Either<String, Integer> ok = divide(10, 2);
        Either<String, Integer> fail = divide(10, 0);

        assertTrue(ok.isRight());
        assertEquals(5, ok.get());

        assertTrue(fail.isLeft());
        assertEquals("Division by zero", fail.getLeft());
    }

    @Test
    void list_basic() {
        List<Integer> numbers = List.of(1, 2, 3, 4);

        List<Integer> result = numbers
                .map(n -> n * 2)
                .filter(n -> n > 4);

        assertEquals(List.of(6, 8), result);
    }

    private Either<String, Integer> divide(int a, int b) {
        return b == 0 ? Left("Division by zero") : Right(a / b);
    }
}