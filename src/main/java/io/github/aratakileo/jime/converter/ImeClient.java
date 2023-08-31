package io.github.aratakileo.jime.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import io.github.aratakileo.jime.Jime;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ImeClient {
    private static final String BASE_URL = "https://www.google.com/transliterate?langpair=ja-Hira%7Cja&text=";

    public static @NotNull List<String> getKanjiVariations(@NotNull String hiragana) {
        if (hiragana.isEmpty())
            return List.of();

        BufferedReader reader = null;

        try {
            final var url = new URI(BASE_URL + URLEncoder.encode(hiragana, StandardCharsets.UTF_8)).toURL();

            reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            return JsonParser.parseReader(reader)
                    .getAsJsonArray()
                    .get(0)
                    .getAsJsonArray()
                    .get(1)
                    .getAsJsonArray()
                    .asList()
                    .stream()
                    .map(JsonElement::getAsString)
                    .toList();
        } catch (Exception e) {
            Jime.LOGGER.error("KanjiConverter error: ", e);
        } finally {
            if (reader != null) try {reader.close();} catch (IOException ignore) {}
        }

        return List.of();
    }
}
