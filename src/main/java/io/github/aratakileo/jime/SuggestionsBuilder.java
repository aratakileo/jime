package io.github.aratakileo.jime;

import com.mojang.brigadier.context.StringRange;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class SuggestionsBuilder {
    private final String input;
    private final int start;
    private final List<Suggestion> result = new ArrayList<>();

    public SuggestionsBuilder(final String input, final int start) {
        this.input = input;
        this.start = start;
    }

    public Suggestions build() {
        return Suggestions.create(input, result);
    }

    public CompletableFuture<Suggestions> buildFuture() {
        return CompletableFuture.completedFuture(build());
    }

    public SuggestionsBuilder forcedSuggest(final String text) {
        result.add(new Suggestion(StringRange.between(start, input.length()), text));
        return this;
    }
}
