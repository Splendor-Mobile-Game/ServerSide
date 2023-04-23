package com.github.splendor_mobile_game.websocket.utils.json;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonMissingFieldException;
import com.github.splendor_mobile_game.websocket.utils.json.exceptions.JsonParserException;

public class JsonParserTest {

    @Test
    public void parseJson_missingRequiredField_throwException() {
        String json = "{ \"name\": \"John\", \"age\": 30 }";
        try {
            JsonParser.parseJson(json, Person.class);
            Assertions.fail("Expected JsonMissingFieldException was not thrown.");
        } catch (JsonParserException e) {
            Assertions.assertTrue(e instanceof JsonMissingFieldException);
            Assertions.assertEquals("Missing required field: email", e.getMessage());
        }
    }

    @Test
    public void parseJson_validJson_returnObject() throws JsonParserException {
        String json = "{ \"name\": \"John\", \"age\": 30, \"email\": \"john@example.com\" }";
        Person person = JsonParser.parseJson(json, Person.class);
        Assertions.assertEquals("John", person.getName());
        Assertions.assertEquals(30, person.getAge());
        Assertions.assertEquals("john@example.com", person.getEmail());
    }

    private static class Person {
        private String name;
        private int age;
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }


    private static class Room {
        
        List<Person> users;

        public Room(List<Person> users) {
            this.users = users;
        }

        public List<Person> getUsers() {
            return users;
        }
        
    }

    @Test
    public void array() throws JsonParserException {
        String json = """
        {
            "users": [
                {
                    "name": "John",
                    "age": 30,
                    "email": "john@example.com"
                },
                {
                    "name": "Alice",
                    "age": 23,
                    "email": "alice@example.com"
                }
            ]
        }""";

        Room room = JsonParser.parseJson(json, Room.class);
        // Assertions.assertEquals("John", person.getName());
        // Assertions.assertEquals(30, person.getAge());
        // Assertions.assertEquals("john@example.com", person.getEmail());
    }
}