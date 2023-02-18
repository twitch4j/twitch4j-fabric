package com.awakenedredstone.test;

import com.github.twitch4j.chat.ITwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.chat.events.roomstate.ChannelStatesEvent;
import net.fabricmc.api.ModInitializer;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class T4JTest implements ModInitializer {

    @Override
    public void onInitialize() {
        ITwitchChat chat = TwitchChatBuilder.builder().build();
        CountDownLatch latch = new CountDownLatch(0);

        chat.getEventManager().onEvent(ChannelStatesEvent.class, e -> latch.countDown());
        chat.joinChannel("twitch");

        try {
            boolean success = latch.await(30, SECONDS);
            assertTrue(success);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
