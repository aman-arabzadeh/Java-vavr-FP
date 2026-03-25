import io.vavr.Lazy;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import io.vavr.control.Try;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.vavr.API.*;
import static io.vavr.Patterns.$None;
import static io.vavr.Patterns.$Some;
import static io.vavr.Predicates.isIn;
import static org.junit.jupiter.api.Assertions.*;

//@ExtendWith(MockitoExtension.class)

public class JavaVavrTest {
 private static  final Logger LOGGER = LoggerFactory.getLogger(JavaVavrTest.class);
    @Test
    void listOptionFlatmap() {
        List<Option<Integer>> raw = List(Option(10), Option.none(), Option(20));
        List<Integer> result = raw.flatMap(o -> o);
        // result.forEach(s -> LOGGER.error(s.toString()));
        assertEquals(List(10, 20), result);
    }

    @Test
    void listTransformations() {
        List<Integer> result = List(1, 2, 3, 4)
                .map(i -> i * 2)
                .filter(i -> i > 4);

        assertEquals(List(6, 8), result);
    }

    @Test
    void tupleUsage() {
        Tuple2<String, Integer> person = Tuple("Alice", 30);
        Tuple2<String, Integer> updated = person.map2(age -> age + 5);

        assertEquals("Alice", updated._1);
        assertEquals(35, updated._2);
    }

    @Test
    void optionNullSafety() {
        String input = null;

        Option<String> result = Option.of(input)
                .map(String::toUpperCase);

        assertTrue(result.isEmpty());
    }

    @Test
    void optionChaining() {
        Option<Integer> result = Option("42 ")
                .map(String::trim)
                .map(Integer::parseInt)
                .filter(i -> i > 0);

        assertEquals(42, result.get());
    }

    @Test
    void trySuccessFailure() {
        Try<Integer> success = Try.of(() -> Integer.parseInt("10"));
        Try<Integer> failure = Try.of(() -> Integer.parseInt("x"));

        assertTrue(success.isSuccess());
        assertTrue(failure.isFailure());

        assertEquals(10, success.get());
        assertEquals(0, failure.getOrElse(0));
    }

    @Test
    void tryChaining() {
        Try<Integer> result = Try.of(() -> 10)
                .map(i -> i * 2)
                .filter(i -> i > 10);

        assertEquals(20, result.get());
    }

    @Test
    void eitherBusinessLogic() {
        Either<String, Integer> ok = divide(10, 2);
        Either<String, Integer> fail = divide(10, 0);

        assertTrue(ok.isRight());
        assertTrue(fail.isLeft());

        assertEquals(5, ok.get());
        assertEquals("Division by zero", fail.getLeft());
    }

    private Either<String, Integer> divide(int a, int b) {
        return b == 0 ? Left("Division by zero") : Right(a / b);
    }

    @Test
    void patternMatchingOption() {
        Option<Integer> input = Option(42);

        String result = Match(input).of(
                Case($Some($(42)), "answer"),
                Case($Some($()), "other"),
                Case($None(), "empty")
        );

        assertEquals("answer", result);
    }


    @ParameterizedTest
    @CsvSource({
            "-h, Help is coming....",
            "--help, Help is coming....",
            "-v,  java 25.0.2 2026-01-20 LTS"
    })
    void patternMatchingWithTest(String arg, String expectedStart) {
        String result = Match(arg).of(
                Case($(isIn("-h", "--help")), o -> displayHelp()),
                Case($(isIn("-v", "--version")), o -> displayVersion()),
                Case($(), o -> { throw  new IllegalArgumentException(arg);
                })

        );

       assertEquals(expectedStart, result);
    }

    private String displayVersion() {
        return "java 25.0.2 2026-01-20 LTS";
    }

    private String displayHelp() {
        return "Help is coming....";
    }

    @Test
    void lazyMemoizationTest() {
        Lazy<Double> lazy = Lazy.of(Math::random);

        assertFalse(lazy.isEvaluated());

        Double v1 = lazy.get();
        Double v2 = lazy.get();

        assertTrue(lazy.isEvaluated());
        assertEquals(v1, v2);
    }

    @Test
    void streamLazySequence() {
        List<Integer> result = Stream.from(1)
                .take(5)
                .toList();

        assertEquals(List(1, 2, 3, 4, 5), result);
    }

    @Test
    void sideEffectsPeek() {
        List<String> result = List("a", "b")
               // .peek(s -> System.out.println("Processing " + s))
                .map(String::toUpperCase);

        assertEquals(List("A", "B"), result);
    }

    @Test
    void optionToTryConversion() {
        Option<String> maybe = Option("100");

        Try<Integer> result = maybe
                .toTry(() -> new RuntimeException("Missing"))
                .map(Integer::parseInt);

        assertEquals(100, result.get());
    }

    @Test
    void pipelineTest() {
        String input = " 50 ";


        Try<Integer> result = Try.of(() -> input)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .filter(i -> i > 0);

        assertTrue(result.isSuccess());
        assertEquals(50, result.get());
    }

    @Test
    void combinedOptionTryEither() {
        Either<String, Integer> result = Option("25")
                .toEither("No input")
                .map(String::trim)
                .flatMap(s -> Try.of(() -> Integer.parseInt(s))
                        .toEither("Invalid number"))
                .filterOrElse(i -> i > 10, i -> "Too small");

        assertTrue(result.isRight());
        assertEquals(25, result.get());
    }
}