package io.github.aratakileo.jime;

import com.google.common.collect.Iterables;
import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.ImeClient;
import io.github.aratakileo.suggestionsapi.injector.AsyncInjector;
import io.github.aratakileo.suggestionsapi.injector.Injector;
import io.github.aratakileo.suggestionsapi.injector.SuggestionsInjector;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import io.github.aratakileo.suggestionsapi.util.StringContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class JimeInjector implements SuggestionsInjector, AsyncInjector {
    private int startOffset = 0;
    private boolean isPhraseFound = true;

    @Override
    public @Nullable List<Suggestion> getSuggestions(@NotNull StringContainer stringContainer) {
        final var phraseMatcher = Injector.SIMPLE_WORD_PATTERN.matcher(stringContainer.getContent());
        final var specialLiteralMatcher = Pattern.compile("\\.{3}|[-,.?!<>(){}&\"'\\[\\]]$")
                .matcher(stringContainer.getContent());
        final var isSpecialLiteralFound = specialLiteralMatcher.find();

        isPhraseFound = phraseMatcher.find();

        if (isPhraseFound) {
            startOffset = phraseMatcher.start();

            return List.of(Suggestion.alwaysShown(
                    HiraganaConverter.convert(phraseMatcher.group().toLowerCase())
            ));
        }

        if (isSpecialLiteralFound) {
            startOffset = specialLiteralMatcher.start();

            return HiraganaConverter.getVariations(specialLiteralMatcher.group())
                    .stream()
                    .map(Suggestion::alwaysShown)
                    .toList();
        }

        return null;
    }

    @Override
    public @Nullable Supplier<@Nullable List<Suggestion>> getAsyncApplier(@NotNull StringContainer stringContainer) {
        if (!isPhraseFound) return null;

        return () -> {
            final var answeredHashMap = ImeClient.getRequestAnswer(stringContainer.getContent().substring(startOffset));

            if (Objects.isNull(answeredHashMap)) return List.of();

            return Iterables.getLast(answeredHashMap.values()).stream().map(Suggestion::alwaysShown).toList();
        };
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }
}
