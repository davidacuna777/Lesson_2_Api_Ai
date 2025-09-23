package cr.una.leccion2.app.domain.model;

public class Message {
    public String role;
    public String content;
    public Message() {}
    public Message(String role, String content) { this.role=role; this.content=content; }
}
