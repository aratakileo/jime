package io.github.aratakileo.japaneseime;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JapaneseIME implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(JapaneseIME.class);

    @Override
    public void onInitializeClient() {
        LOGGER.info("Before: watashiha");
        LOGGER.info("After: " + RomajiToHiraganaConverter.convert("watashiha"));
    }
}
