package io.github.aratakileo.jime;

import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.KanjiConverter;
import io.github.aratakileo.suggestionsapi.SuggestionsAPI;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import io.github.aratakileo.suggestionsapi.suggestion.SuggestionsInjector;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Jime implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Jime.class);

    @Override
    public void onInitializeClient() {
        SuggestionsAPI.registerSuggestionsInjector(SuggestionsInjector.simple(
                Pattern.compile("[A-Za-z0-9]+"),
                (currentExpression, offsettedExpression) -> {
                    final var hiraganaLiterals = HiraganaConverter.convert(offsettedExpression);
                    final var output = new ArrayList<Suggestion>(
                            KanjiConverter.convert(hiraganaLiterals)
                                    .stream()
                                    .map(Suggestion::alwaysShown)
                                    .toList()
                    );

                    output.add(Suggestion.alwaysShown(hiraganaLiterals));

                    return output;
                },
                true
        ));

        SuggestionsAPI.registerSuggestionsInjector(SuggestionsInjector.simple(
                Pattern.compile("\\.{3}|[-,.?!<>(){}&\"'\\[\\]]"),
                (currentExpression, offsettedExpression) -> HiraganaConverter.getVariations(offsettedExpression)
                        .stream()
                        .map(Suggestion::alwaysShown)
                        .toList(),
                true
        ));
    }
}
