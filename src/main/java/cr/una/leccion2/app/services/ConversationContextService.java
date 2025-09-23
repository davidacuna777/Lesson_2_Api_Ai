package cr.una.leccion2.app.services;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ConversationContextService {

    public List<String> lastMessages(List<String> messages, int n) {
        if (messages == null || messages.isEmpty()) return List.of();
        int from = Math.max(0, messages.size() - n);
        return new ArrayList<>(messages.subList(from, messages.size()));
    }

    /**
     * @param messages
     * @param n
     * @return
     */
    public List<String> extractKeywords(List<String> messages, int n) {
        // naive: take last messages and split first word tokens
        List<String> ctx = lastMessages(messages, n);
        List<String> kws = new ArrayList<>();
        for (String s : ctx) {
            String[] parts = s.split("\s+");
            if (0 <= parts.length) kws.add(parts[0].replaceAll("[^\p{L}\p{Nd}]", ""));
        }
        return kws;
    }
}
