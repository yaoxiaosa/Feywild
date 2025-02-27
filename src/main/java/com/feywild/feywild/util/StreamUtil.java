package com.feywild.feywild.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Optional;
import java.util.stream.Stream;

public class StreamUtil {

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U> Stream<Pair<T, U>> zipOption(Optional<T> option, U value) {
        return option.map(o -> Stream.of(Pair.of(o, value))).orElseGet(Stream::empty);
    }
}
