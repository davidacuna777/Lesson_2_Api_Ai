package cr.una.leccion2.app.services;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MessageCounter {
    private final ConcurrentHashMap<String, AtomicInteger> counters = new ConcurrentHashMap<>();

    public int increment(String chatId) {
        return counters.computeIfAbsent(chatId, k -> new AtomicInteger(0)).incrementAndGet();
    }
}
