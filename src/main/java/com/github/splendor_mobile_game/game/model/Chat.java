package com.github.splendor_mobile_game.game.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

public class Chat {
    private ArrayList<ChatMessage> chatMessages = new ArrayList<>();

    public ArrayList<ChatMessage> getChatMessages() {
        Collections.sort(chatMessages);
        return chatMessages;
    }

    public ArrayList<ChatMessage> getUsersMessages(UUID userUuid) {
        return chatMessages.stream().filter(message -> message.getSenderUUID() == userUuid).collect(Collectors
                .toCollection(ArrayList::new));
    }

    public ChatMessage getLastMessage() {
        Collections.sort(chatMessages);
        return chatMessages.get(chatMessages.size() - 1);
    }

    public void addMessage(String message, UUID senderUuid) {
        chatMessages.add(new ChatMessage(senderUuid, message));
    }

    public class ChatMessage implements Comparable<ChatMessage>  {
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

        @Override
        public int compareTo(ChatMessage o) {
            if (this.date.isBefore(o.getDate())) return -1;
            return 1;
        }
    }

}
