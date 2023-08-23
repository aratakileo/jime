package io.github.aratakileo.jime.mixin;

import com.google.common.base.Strings;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestions;
import io.github.aratakileo.jime.converter.HiraganaConverter;
import io.github.aratakileo.jime.SuggestionsBuilder;
import io.github.aratakileo.jime.converter.KanjiConverter;
import net.minecraft.client.gui.components.CommandSuggestions;
import net.minecraft.client.gui.components.EditBox;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

@Mixin(CommandSuggestions.class)
public class CommandSuggestionsMixin {
    @Unique
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");

    @Shadow
    @Final
    EditBox input;

    @Shadow @Nullable
    private CompletableFuture<Suggestions> pendingSuggestions;

    @Shadow @Final
    private boolean commandsOnly;

    @Inject(method = "updateCommandInfo", at = @At("TAIL"), cancellable = true)
    private void updateCommandInfo(CallbackInfo ci){
        final var contentText = input.getValue();
        final var stringReader = new StringReader(contentText);
        final var hasSlash = stringReader.canRead() && stringReader.peek() == '/';
        final var cursorPosition = input.getCursorPosition();

        if (hasSlash)
            stringReader.skip();

        if (commandsOnly || hasSlash) return;

        final var textUptoCursor = contentText.substring(0, cursorPosition);
        final var patternBounds = getLastMatchedBounds(HiraganaConverter.ROMAJI_LITERALS_PATTERN);
        final var whitespaceEnd = getLastMatchedBounds(WHITESPACE_PATTERN).getB();

        if (patternBounds.getA() == -1 || patternBounds.getB() < whitespaceEnd) return;

        final var romajiLiterals = contentText.substring(patternBounds.getA(), cursorPosition);
        final var hiraganaLiterals = HiraganaConverter.convert(romajiLiterals);
        final var suggestionsBuilder = new SuggestionsBuilder(textUptoCursor, patternBounds.getA());
        final var suggestions = new ArrayList<>(KanjiConverter.convert(hiraganaLiterals));

        suggestions.add(hiraganaLiterals);

        for (final var suggestion: suggestions) suggestionsBuilder.forcedSuggest(suggestion);

        pendingSuggestions = suggestionsBuilder.buildFuture();

        pendingSuggestions.thenRun(() -> {
            if (!pendingSuggestions.isDone()) return;
            ((CommandSuggestions)(Object)this).showSuggestions(false);
        });

        ci.cancel();
    }

    @Unique
    private Pair<Integer, Integer> getLastMatchedBounds(Pattern pattern){
        if (Strings.isNullOrEmpty(input.getValue())) {
            return new Pair<>(-1, -1);
        }

        final var matcher = pattern.matcher(input.getValue());

        var start = -1;
        var end = -1;

        while (matcher.find()) {
            start = matcher.start();
            end = matcher.end();
        }

        return new Pair<>(start, end);
    }
}
