package com.github.splendor_mobile_game.game.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

public class Chat {
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public ArrayList<ChatMessage> getUsersMessages(UUID userUuid) {
        return chatMessages.stream().filter(message -> message.getSenderUUID() == userUuid).collect(Collectors
                .toCollection(ArrayList::new));
    }

    public ChatMessage getLastMessage() {
        return chatMessages.get(chatMessages.size() - 1);
    }

    public void sendMessage(String message, UUID senderUuid) {
        chatMessages.add(new ChatMessage(senderUuid, message));
    }

    public class ChatMessage  {
        private final UUID senderUUID;
        private final LocalDateTime date;
        private final String message;

        public ChatMessage(UUID senderUUID, String message) {
            this.senderUUID = senderUUID;
            this.message = message;

            this.date = LocalDateTime.now();
        }


        public String getMessage() {
            return message;
        }

        public LocalDateTime getDate() {
            return date;
        }

        public UUID getSenderUUID() {
            return senderUUID;
        }

    }

}
