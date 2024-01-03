package io.github.aratakileo.jime;

import com.google.common.collect.Iterables;
import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.ImeClient;
import io.github.aratakileo.suggestionsapi.injector.AsyncInjector;
import io.github.aratakileo.suggestionsapi.injector.Injector;
import io.github.aratakileo.suggestionsapi.injector.SuggestionsInjector;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class JimeInjector implements SuggestionsInjector, AsyncInjector {
    private int startOffset = 0;
    private boolean shouldShowSuggestions = true;

    @Override
    public @Nullable List<Suggestion> getSuggestions(@NotNull String currentExpression) {
        final var matcher = Injector.SIMPLE_WORD_PATTERN.matcher(currentExpression);

        shouldShowSuggestions = false;

        if (!matcher.find()) return null;

        shouldShowSuggestions = true;
        startOffset = matcher.start();

        return List.of(Suggestion.alwaysShown(
                HiraganaConverter.convert(currentExpression.substring(startOffset).toLowerCase())
        ));
    }

    @Override
    public @Nullable Supplier<@Nullable List<Suggestion>> getAsyncApplier(@NotNull String currentExpression) {
        if (!shouldShowSuggestions) return null;

        return () -> {
            final var answeredHashMap = ImeClient.getRequestAnswer(currentExpression.substring(startOffset));

            if (Objects.isNull(answeredHashMap)) return List.of();

            return Iterables.getLast(answeredHashMap.values()).stream().map(Suggestion::alwaysShown).toList();
        };
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }
}
