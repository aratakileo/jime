package io.github.aratakileo.jime;

import io.github.aratakileo.suggestionsapi.SuggestionsAPI;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jime implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Jime.class);

    @Override
    public void onInitializeClient() {
        SuggestionsAPI.registerInjector(new JimeInjector());
    }
}
