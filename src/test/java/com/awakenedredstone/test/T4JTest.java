package com.awakenedredstone.test;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.chat.ITwitchChat;
import com.github.twitch4j.chat.TwitchChatBuilder;
import com.github.twitch4j.chat.events.roomstate.ChannelStatesEvent;
import net.fabricmc.api.ModInitializer;

import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

public class T4JTest implements ModInitializer {

    @Override
    public void onInitialize() {
        // external event manager
        EventManager eventManager = new EventManager();
        eventManager.autoDiscovery();
        eventManager.setDefaultEventHandler(SimpleEventHandler.class);

        // construct twitchClient
        TwitchClient twitchClient = TwitchClientBuilder.builder()
          .withEventManager(null)
          .withEnableHelix(true)
          .withEnableChat(true)
          .withEnablePubSub(true)
          .withEnableGraphQL(false)
          .withHelperThreadDelay(10000L)
          .build();

        TwitchClientBuilder b1 = TwitchClientBuilder.builder().withEnableChat(true);
        assertTrue(b1.getCommandPrefixes().isEmpty());

        TwitchClientBuilder b2 = b1.withCommandTrigger("!");
        assertEquals(1, b2.getCommandPrefixes().size());

        TwitchClientBuilder b3 = b2.withEnablePubSub(true);
        assertEquals(1, b3.getCommandPrefixes().size());

        assertDoesNotThrow(() -> twitchClient.getHelix().getChannelInternetCalendar("896715637"));

        ITwitchChat chat = TwitchChatBuilder.builder().build();
        CountDownLatch latch = new CountDownLatch(1);

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
