package io.github.aratakileo.jime;

import com.google.common.collect.Lists;
import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.converter.ImeClient;
import io.github.aratakileo.suggestionsapi.injector.AsyncInjector;
import io.github.aratakileo.suggestionsapi.injector.Injector;
import io.github.aratakileo.suggestionsapi.injector.SuggestionsInjector;
import io.github.aratakileo.suggestionsapi.suggestion.Suggestion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class JimeInjector implements SuggestionsInjector, AsyncInjector {
    private final static Pattern PATTERN = Pattern.compile("[A-Za-z0-9]+");

    private int startOffset = 0;
    private boolean shouldShowSuggestions = true;

    @Override
    public @Nullable Supplier<@Nullable List<Suggestion>> getAsyncApplier(@NotNull String currentExpression) {
        if (!shouldShowSuggestions) return null;

        return () -> Lists.newArrayList(
                ImeClient.getKanjiVariations(HiraganaConverter.convert(currentExpression.substring(startOffset)))
                        .stream()
                        .map(Suggestion::alwaysShown)
                        .toList()
        );
    }

    @Override
    public @Nullable List<Suggestion> getSuggestions(@NotNull String currentExpression) {
        int lastMatchedStart = Injector.getLastMatchedStart(PATTERN, currentExpression);

        shouldShowSuggestions = false;

        if (lastMatchedStart == -1) return null;

        shouldShowSuggestions = true;

        startOffset = lastMatchedStart;

        return Lists.newArrayList(
                Suggestion.alwaysShown(
                        HiraganaConverter.convert(currentExpression.substring(lastMatchedStart).toLowerCase())
                )
        );
    }

    @Override
    public int getStartOffset() {
        return startOffset;
    }

    @Override
    public boolean isIsolated() {
        return true;
    }
}
