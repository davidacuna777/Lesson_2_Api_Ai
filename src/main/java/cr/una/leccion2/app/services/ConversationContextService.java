package cr.una.leccion2.app.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class ConversationContextService {

    private static final int MAX_HISTORY = 30;
    private static final Set<String> STOP_WORDS = Set.of(
            "a", "al", "de", "del", "el", "ella", "ellos", "en", "es", "la", "las",
            "los", "lo", "nos", "para", "por", "que", "se", "un", "una", "y"
    );

    private final Map<String, Deque<String>> history = new ConcurrentHashMap<>();

    public void appendMessage(String chatId, String message) {
        if (chatId == null || chatId.isBlank() || message == null || message.isBlank()) {
            return;
        }
        Deque<String> deque = history.computeIfAbsent(chatId, key -> new ArrayDeque<>());
        synchronized (deque) {
            if (deque.size() >= MAX_HISTORY) {
                while (deque.size() >= MAX_HISTORY) {
                    deque.removeFirst();
                }
            }
            deque.addLast(message.trim());
        }
    }

    public List<String> lastMessages(String chatId, int limit) {
        if (chatId == null || chatId.isBlank() || limit <= 0) {
            return List.of();
        }
        Deque<String> deque = history.get(chatId);
        if (deque == null || deque.isEmpty()) {
            return List.of();
        }
        List<String> snapshot;
        synchronized (deque) {
            snapshot = new ArrayList<>(deque);
        }
        if (snapshot.size() <= limit) {
            return snapshot;
        }
        return snapshot.subList(snapshot.size() - limit, snapshot.size());
    }

    public List<String> lastMessages(List<String> suppliedContext, int limit) {
        if (suppliedContext == null || suppliedContext.isEmpty() || limit <= 0) {
            return List.of();
        }
        int fromIndex = Math.max(0, suppliedContext.size() - limit);
        return new ArrayList<>(suppliedContext.subList(fromIndex, suppliedContext.size()));
    }

    public List<String> extractKeywords(String chatId, int limit) {
        return extractKeywords(lastMessages(chatId, MAX_HISTORY), limit);
    }

    public List<String> extractKeywords(List<String> messages, int limit) {
        if (messages == null || messages.isEmpty() || limit <= 0) {
            return List.of();
        }
        Map<String, Long> frequency = messages.stream()
                .filter(msg -> msg != null && !msg.isBlank())
                .flatMap(msg -> tokenize(msg).stream())
                .collect(Collectors.groupingBy(token -> token, Collectors.counting()));

        if (frequency.isEmpty()) {
            return List.of();
        }

        return frequency.entrySet().stream()
                .sorted((a, b) -> {
                    int cmp = Long.compare(b.getValue(), a.getValue());
                    if (cmp != 0) {
                        return cmp;
                    }
                    return a.getKey().compareTo(b.getKey());
                })
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private List<String> tokenize(String text) {
        if (text == null) {
            return List.of();
        }
        String[] parts = text.toLowerCase(Locale.ROOT).split("[^\\p{L}\\p{N}]+");
        List<String> tokens = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (part.length() < 3) {
                continue;
            }
            if (STOP_WORDS.contains(part)) {
                continue;
            }
            tokens.add(part);
        }
        return tokens;
    }
}
