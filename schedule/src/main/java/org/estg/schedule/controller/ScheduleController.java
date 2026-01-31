package org.estg.schedule.controller;

import java.util.List;

import org.estg.schedule.dto.SessionDTO;
import org.estg.schedule.dto.SessionParticipantDTO;
import org.estg.schedule.service.SessionService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sessions")
public class ScheduleController {

    private final SessionService sessionService;

    public ScheduleController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    // Table 10 - POST /api/sessions/book
    @PostMapping("/book")
    public ResponseEntity<SessionDTO> scheduleSession(@RequestBody SessionDTO request) {
        return ResponseEntity.ok(sessionService.scheduleSession(request));
    }

    // Table 10 - GET /api/sessions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SessionDTO> getSessionById(@PathVariable("id") @NonNull String id) {
        return ResponseEntity.ok(sessionService.getSessionById(id));
    }

    // Table 10 - GET /api/sessions?memberId=... OR ?trainerId=...
    @GetMapping
    public ResponseEntity<List<SessionDTO>> listSessions(
            @RequestParam(value = "memberId", required = false) String memberId,
            @RequestParam(value = "trainerId", required = false) String trainerId) {
        return ResponseEntity.ok(sessionService.listSessions(memberId, trainerId));
    }

    // Table 10 - POST /api/sessions/{id}/confirm
    @PostMapping("/{id}/confirm")
    public ResponseEntity<SessionDTO> confirmSession(@PathVariable("id") @NonNull String id) {
        return ResponseEntity.ok(sessionService.confirmSession(id));
    }

    // Table 10 - PUT /api/sessions/{id}/reschedule
    @PutMapping("/{id}/reschedule")
    public ResponseEntity<SessionDTO> rescheduleSession(
            @PathVariable("id") @NonNull String id,
            @RequestBody SessionDTO request) {
        return ResponseEntity.ok(sessionService.rescheduleSession(id, request));
    }

    // Table 10 - POST /api/sessions/{id}/cancel
    @PostMapping("/{id}/cancel")
    public ResponseEntity<SessionDTO> cancelSession(
            @PathVariable("id") @NonNull String id,
            @RequestBody(required = false) CancelSessionRequest request) {
        String reason = request != null ? request.getReason() : null;
        return ResponseEntity.ok(sessionService.cancelSession(id, reason));
    }

    // Table 10 - POST /api/sessions/{id}/complete
    @PostMapping("/{id}/complete")
    public ResponseEntity<SessionDTO> completeSession(
            @PathVariable("id") @NonNull String id,
            @RequestBody CompleteSessionRequest request) {
        return ResponseEntity.ok(
                sessionService.completeSession(id, request.getCaloriesBurned(), request.getSessionNotes()));
    }

    // Participants
    @GetMapping("/{id}/participants")
    public ResponseEntity<List<SessionParticipantDTO>> getSessionParticipants(@PathVariable("id") @NonNull String id) {
        return ResponseEntity.ok(sessionService.getSessionParticipants(id));
    }

    @PostMapping("/{id}/participants/{memberId}")
    public ResponseEntity<SessionParticipantDTO> addParticipant(
            @PathVariable("id") @NonNull String id,
            @PathVariable("memberId") @NonNull String memberId) {
        return ResponseEntity.ok(sessionService.addParticipant(id, memberId));
    }

    public static class CancelSessionRequest {

        private String reason;

        public CancelSessionRequest() {
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }
    }

    public static class CompleteSessionRequest {

        private Integer caloriesBurned;
        private String sessionNotes;

        public CompleteSessionRequest() {
        }

        public Integer getCaloriesBurned() {
            return caloriesBurned;
        }

        public void setCaloriesBurned(Integer caloriesBurned) {
            this.caloriesBurned = caloriesBurned;
        }

        public String getSessionNotes() {
            return sessionNotes;
        }

        public void setSessionNotes(String sessionNotes) {
            this.sessionNotes = sessionNotes;
        }
    }
}
