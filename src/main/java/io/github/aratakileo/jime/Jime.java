package io.github.aratakileo.jime;

import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.KanjiConverter;
import io.github.aratakileo.suggestionsapi.SuggestionsAPI;
import io.github.aratakileo.suggestionsapi.suggestion.AlwaysShownSuggestion;
import io.github.aratakileo.suggestionsapi.suggestion.SimpleSuggestionsInjector;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import net.fabricmc.api.ClientModInitializer;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Jime implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Jime.class);

    @Override
    public void onInitializeClient() {
        SuggestionsAPI.registerSuggestionsInjector(
                new SimpleSuggestionsInjector(Pattern.compile("[A-Za-z0-9]+")) {
                    @Override
                    public boolean isIsolated() {
                        return true;
                    }

                    @Override
                    public <T extends Suggestion> List<T> getUncheckedSuggestions(
                            @NotNull String currentExpression
                    ) {
                        final var hiraganaLiterals = HiraganaConverter.convert(
                                currentExpression.substring(getStartOffset())
                        );
                        final var output = new ArrayList<Suggestion>(
                                KanjiConverter.convert(hiraganaLiterals)
                                        .stream()
                                        .map(AlwaysShownSuggestion::new)
                                        .toList()
                        );

                        output.add(new AlwaysShownSuggestion(hiraganaLiterals));

                        return (List<T>) output;
                    }
                }
        );
    }
}
