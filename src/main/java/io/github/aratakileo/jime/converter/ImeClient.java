package io.github.aratakileo.jime.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.aratakileo.jime.Jime;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class ImeClient {
    private static final String BASE_URL = "https://www.google.com/transliterate?langpair=ja-Hira%7Cja&text=";

    public static @Nullable LinkedHashMap<@NotNull String, @NotNull List<@NotNull String>> getRequestAnswer(
            @NotNull String romajiText
    ) {
        final var hiraganaText = HiraganaConverter.convert(romajiText.toLowerCase());

        if (hiraganaText.isEmpty())
            return null;

        var reader = (BufferedReader) null;

        try {
            final var url = new URI(BASE_URL + URLEncoder.encode(hiraganaText, StandardCharsets.UTF_8)).toURL();

            reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            final var finalReader = reader;

            return new LinkedHashMap<>() {{
                JsonParser.parseReader(finalReader)
                        .getAsJsonArray()
                        .asList()
                        .stream()
                        .map(JsonElement::getAsJsonArray)
                        .forEach(untransformedEntry -> put(
                                untransformedEntry.get(0).getAsString(),
                                untransformedEntry.get(1)
                                        .getAsJsonArray()
                                        .asList()
                                        .stream()
                                        .map(JsonElement::getAsString)
                                        .toList()
                        ));
            }};
        } catch (Exception e) {
            Jime.LOGGER.error("ImeClient request error: ", e);
        } finally {
            if (Objects.nonNull(reader)) try {reader.close();} catch (IOException ignore) {}
        }

        return null;
    }
}
