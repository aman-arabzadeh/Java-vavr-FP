package org.example;


import io.vavr.collection.List;
import io.vavr.control.Try;


public class Main {
    public static void main(String[] args) {


        Try<Long> result = Try.of( () -> Long.parseLong("12347"));
        List<String> list = List.of("2", "3", "5", "7", "11", "13", "17", "19", "23");
        list.map(i -> Try.of(() -> Integer.parseInt(i))
                        .map(b -> "Success: " + b)
                .recover(NumberFormatException.class,e -> "Error: " + e.getMessage())
                .get())
                .take(5)
                .forEach(System.out::println);


        System.out.println("-".repeat(20));

        var res = result
                .map(i -> "Success: " + i)
                .recover(NumberFormatException.class,e ->  "Error: " + e.getMessage())
                .get();


        System.out.println(res);
    }
}