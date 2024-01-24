package io.github.aratakileo.jime;

import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.suggestionsapi.SuggestionsAPI;
import io.github.aratakileo.suggestionsapi.injector.Injector;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import io.github.aratakileo.suggestionsapi.util.Cast;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Pattern;

public class Jime implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Jime.class);

    @Override
    public void onInitializeClient() {
        SuggestionsAPI.registerInjector(new JimeInjector());

        SuggestionsAPI.registerInjector(Injector.simple(
                Pattern.compile("\\.{3}|[-,.?!<>(){}&\"'\\[\\]]"),
                (stringContainer, startOffset) -> Cast.of(
                        HiraganaConverter.getVariations(stringContainer.getContent().substring(startOffset))
                                .stream()
                                .map(Suggestion::alwaysShown)
                                .toList()
                )
        ));
    }
}
