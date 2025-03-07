package com.jsonmorberg.ururl.utils;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UrlHashGenerator {
    private static final List<Character> BASE62_CHARS = generateBase62Chars();
    private static final int SHORT_URL_LENGTH = 6;
    private static final SecureRandom RANDOM = new SecureRandom();

    private static List<Character> generateBase62Chars() {
        return IntStream.concat(
                        IntStream.rangeClosed('a', 'z'),
                        IntStream.concat(
                                IntStream.rangeClosed('A', 'Z'),
                                IntStream.rangeClosed('0', '9')
                        )
                )
                .mapToObj(c -> (char) c)
                .collect(Collectors.toList());
    }

    public String generateShortCode() {
        return RANDOM.ints(SHORT_URL_LENGTH, 0, BASE62_CHARS.size())
                .mapToObj(BASE62_CHARS::get)
                .map(String::valueOf)
                .collect(Collectors.joining());
    }
}
