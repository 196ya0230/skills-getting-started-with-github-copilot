package com.mergington.signup.service;

import com.mergington.signup.dto.Activity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class SignupService {
    private final RestTemplate restTemplate;
    private final String activitiesServiceUrl;
    private final String studentsServiceUrl;

    public SignupService(
            RestTemplate restTemplate,
            @Value("${activities.service.url}") String activitiesServiceUrl,
            @Value("${students.service.url}") String studentsServiceUrl) {
        this.restTemplate = restTemplate;
        this.activitiesServiceUrl = activitiesServiceUrl;
        this.studentsServiceUrl = studentsServiceUrl;
    }

    public Map<String, String> signup(String activityName, String email) {
        String normalizedActivity = UriUtils.decode(activityName, StandardCharsets.UTF_8);
        String normalizedEmail = UriUtils.decode(email, StandardCharsets.UTF_8);

        ensureStudentExists(normalizedEmail);
        Activity activity = fetchActivity(normalizedActivity);

        List<String> participants = activity.getParticipants();
        if (participants.contains(normalizedEmail)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student is already signed up");
        }
        if (participants.size() >= activity.getMaxParticipants()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Activity is full");
        }

        addParticipant(normalizedActivity, normalizedEmail);
        return Map.of("message", "Signed up " + normalizedEmail + " for " + normalizedActivity);
    }

    private void ensureStudentExists(String email) {
        String encodedEmail = UriUtils.encodePathSegment(email, StandardCharsets.UTF_8);
        String url = studentsServiceUrl + "/students/" + encodedEmail;

        try {
            restTemplate.getForEntity(url, String.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Student not found");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Students service unavailable");
        }
    }

    private Activity fetchActivity(String activityName) {
        String encodedActivity = UriUtils.encodePathSegment(activityName, StandardCharsets.UTF_8);
        String url = activitiesServiceUrl + "/activities/" + encodedActivity;

        try {
            ResponseEntity<Activity> response = restTemplate.getForEntity(url, Activity.class);
            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Activity not found");
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Activities service unavailable");
        }
    }

    private void addParticipant(String activityName, String email) {
        String encodedActivity = UriUtils.encodePathSegment(activityName, StandardCharsets.UTF_8);
        String encodedEmail = UriUtils.encodeQueryParam(email, StandardCharsets.UTF_8);
        String url = activitiesServiceUrl + "/activities/" + encodedActivity + "/participants?email=" + encodedEmail;

        try {
            restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        } catch (HttpStatusCodeException ex) {
            if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getResponseBodyAsString());
            }
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Activities service unavailable");
        }
    }
}
