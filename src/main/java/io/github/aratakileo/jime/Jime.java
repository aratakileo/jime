package io.github.aratakileo.jime;

import com.google.common.collect.Lists;
import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.KanjiConverter;
import io.github.aratakileo.suggestionsapi.SuggestionsAPI;
import io.github.aratakileo.suggestionsapi.suggestion.Injector;
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
        SuggestionsAPI.registerSuggestionsInjector(Injector.simple(
                Pattern.compile("[A-Za-z0-9]+"),
                (currentExpression, startOffset) -> Lists.newArrayList(
                        Suggestion.alwaysShown(
                                HiraganaConverter.convert(
                                        currentExpression.substring(startOffset).toLowerCase()
                                )
                        )
                ),
                true
        ));

        SuggestionsAPI.registerSuggestionsInjector(Injector.async(
                Pattern.compile("[A-Za-z0-9]+"),
                (currentExpression, startOffset, applier) -> () -> {
                    applier.accept(Lists.newArrayList(
                            KanjiConverter.convert(HiraganaConverter.convert(currentExpression.substring(startOffset)))
                                    .stream()
                                    .map(Suggestion::alwaysShown)
                                    .toList()
                    ));
                },
                true
        ));

        SuggestionsAPI.registerSuggestionsInjector(Injector.simple(
                Pattern.compile("\\.{3}|[-,.?!<>(){}&\"'\\[\\]]"),
                (currentExpression, startOffset) -> Cast.of(
                        HiraganaConverter.getVariations(currentExpression.substring(startOffset))
                                .stream()
                                .map(Suggestion::alwaysShown)
                                .toList()
                ),
                true
        ));
    }
}
