package cr.una.leccion2.app.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

@Service
public class MessageCounter {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public int increment(String chatId) {
        if (chatId == null || chatId.isBlank()) {
            return 0;
        }
        return counters.computeIfAbsent(chatId, key -> new AtomicInteger(0)).incrementAndGet();
    }
}
