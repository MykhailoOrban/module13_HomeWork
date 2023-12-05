package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class JsonPlaceholderApiInteraction {

    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";
    private static final String USERS_ENDPOINT = "/users";
    private static final String POSTS_ENDPOINT = "/posts";
    private static final String COMMENTS_ENDPOINT = "/comments";
    private static final String TODOS_ENDPOINT = "/todos";

    public static void main(String[] args) {
        JsonPlaceholderApiInteraction apiInteraction = new JsonPlaceholderApiInteraction();

        // Завдання 1
        apiInteraction.createAndUpdateUser();
        apiInteraction.deleteUser();
        apiInteraction.getAllUsers();
        apiInteraction.getUserById(1);
        apiInteraction.getUserByUsername("Bret");

        // Завдання 2
        apiInteraction.getAndWriteCommentsToFile(1);

        // Завдання 3
        apiInteraction.getOpenTodosForUser(1);
    }

    private void createAndUpdateUser() {
        try {

            String newUserJson = "{\"name\":\"John Doe\",\"username\":\"johndoe\",\"email\":\"john.doe@example.com\"}";
            String createdUserJson = sendPostRequest(USERS_ENDPOINT, newUserJson);
            System.out.println("Created User: " + createdUserJson);


            int userIdToUpdate = getUserIdFromJson(createdUserJson);
            String updatedUserJson = sendPutRequest(USERS_ENDPOINT + "/" + userIdToUpdate, "{\"name\":\"John Doe Updated\"}");
            System.out.println("Updated User: " + updatedUserJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteUser() {
        try {

            int userIdToDelete = 1;
            boolean deleteResult = sendDeleteRequest(USERS_ENDPOINT + "/" + userIdToDelete);
            System.out.println("Delete Result: " + deleteResult);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAllUsers() {
        try {

            String allUsersJson = sendGetRequest(USERS_ENDPOINT);
            System.out.println("All Users: " + allUsersJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUserById(int userId) {
        try {

            String userByIdJson = sendGetRequest(USERS_ENDPOINT + "/" + userId);
            System.out.println("User by ID: " + userByIdJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getUserByUsername(String username) {
        try {

            String userByUsernameJson = sendGetRequest(USERS_ENDPOINT + "?username=" + username);
            System.out.println("User by Username: " + userByUsernameJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getAndWriteCommentsToFile(int userId) {
        try {

            String userPostsJson = sendGetRequest(USERS_ENDPOINT + "/" + userId + POSTS_ENDPOINT);
            int latestPostId = getLatestPostIdFromJson(userPostsJson);


            String commentsJson = sendGetRequest(POSTS_ENDPOINT + "/" + latestPostId + COMMENTS_ENDPOINT);


            String fileName = "user-" + userId + "-post-" + latestPostId + "-comments.json";
            writeJsonToFile(commentsJson, fileName);
            System.out.println("Comments written to file: " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getOpenTodosForUser(int userId) {
        try {

            String todosJson = sendGetRequest(USERS_ENDPOINT + "/" + userId + TODOS_ENDPOINT);
            System.out.println("Open Todos for User " + userId + ": " + todosJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String sendGetRequest(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            StringBuilder response = new StringBuilder();
            int data;
            while ((data = reader.read()) != -1) {
                response.append((char) data);
            }
            return response.toString();
        }
    }

    private String sendPostRequest(String endpoint, String postData) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            StringBuilder response = new StringBuilder();
            int data;
            while ((data = reader.read()) != -1) {
                response.append((char) data);
            }
            return response.toString();
        }
    }

    private String sendPutRequest(String endpoint, String putData) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = putData.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader reader = new InputStreamReader(inputStream)) {
            StringBuilder response = new StringBuilder();
            int data;
            while ((data = reader.read()) != -1) {
                response.append((char) data);
            }
            return response.toString();
        }
    }

    private boolean sendDeleteRequest(String endpoint) throws IOException {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        int responseCode = connection.getResponseCode();
        return responseCode >= 200 && responseCode < 300;
    }

    private int getUserIdFromJson(String userJson) {

        return Integer.parseInt(userJson.split("\"id\":")[1].split(",")[0].trim());
    }

    private int getLatestPostIdFromJson(String postsJson) {

        String[] posts = postsJson.split("\\},\\{");
        String lastPost = posts[posts.length - 1];
        return Integer.parseInt(lastPost.split("\"id\":")[1].split(",")[0].trim());
    }

    private void writeJsonToFile(String jsonData, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName)) {
            fileWriter.write(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

