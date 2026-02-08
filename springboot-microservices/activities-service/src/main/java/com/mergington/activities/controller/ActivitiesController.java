package com.mergington.activities.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mergington.activities.model.Activity;
import com.mergington.activities.service.ActivityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
public class ActivitiesController {
    private final ActivityRepository activityRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String signupServiceUrl;

    public ActivitiesController(
            ActivityRepository activityRepository,
            RestTemplate restTemplate,
            ObjectMapper objectMapper,
            @Value("${signup.service.url}") String signupServiceUrl) {
        this.activityRepository = activityRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.signupServiceUrl = signupServiceUrl;
    }

    @GetMapping("/activities")
    public Map<String, Activity> getActivities() {
        return activityRepository.getAllActivities();
    }

    @GetMapping("/activities/{activityName}")
    public Activity getActivity(@PathVariable String activityName) {
        Activity activity = activityRepository.getActivity(activityName);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }
        return activity;
    }

    @PostMapping("/activities/{activityName}/participants")
    public Map<String, String> addParticipant(@PathVariable String activityName, @RequestParam String email) {
        Activity activity = activityRepository.getActivity(activityName);
        if (activity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
        }

        List<String> participants = activity.getParticipants();
        if (participants.contains(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is already signed up");
        }
        if (participants.size() >= activity.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity is full");
        }

        participants.add(email);
        return Map.of("message", "Signed up " + email + " for " + activityName);
    }

    @PostMapping("/activities/{activityName}/signup")
    public ResponseEntity<Map<String, String>> signupProxy(
            @PathVariable String activityName,
            @RequestParam String email) {
        String decodedActivity = UriUtils.decode(activityName, StandardCharsets.UTF_8);
        String decodedEmail = UriUtils.decode(email, StandardCharsets.UTF_8);
        String encodedActivity = UriUtils.encodePathSegment(decodedActivity, StandardCharsets.UTF_8);
        String encodedEmail = UriUtils.encodeQueryParam(decodedEmail, StandardCharsets.UTF_8);
        String url = signupServiceUrl + "/activities/" + encodedActivity + "/signup?email=" + encodedEmail;

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(url, null, Map.class);
            @SuppressWarnings("unchecked")
            Map<String, String> body = response.getBody();
            return ResponseEntity.status(response.getStatusCode()).body(body);
        } catch (HttpStatusCodeException ex) {
            String detail = extractDetail(ex.getResponseBodyAsString());
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("detail", detail));
        }
    }

    private String extractDetail(String body) {
        if (body == null || body.isBlank()) {
            return "Signup failed";
        }
        try {
            JsonNode node = objectMapper.readTree(body);
            if (node.has("detail")) {
                return node.get("detail").asText();
            }
            if (node.has("message")) {
                return node.get("message").asText();
            }
        } catch (Exception ignored) {
        }
        return body;
    }
}
