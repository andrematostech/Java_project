package org.estg.controller;

import java.util.List;

import org.estg.dto.MemberProfileDTO;
import org.estg.dto.MembersDTO;
import org.estg.dto.SessionRecordDTO;
import org.estg.service.MembersService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

// REST controller for Members Service (aligned with RF-MEM-01..RF-MEM-07)
@RestController
@RequestMapping("/api/members")
public class MembersController {

    private final MembersService membersService;

    public MembersController(MembersService membersService) {
        this.membersService = membersService;
    }

    // RF-MEM-01 - Create Member
    @PostMapping("/register")
    public ResponseEntity<MembersDTO> registerMember(@RequestBody MembersDTO memberDTO) {
        MembersDTO result = membersService.registerMember(memberDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // RF-MEM-05 - List Members (pagination)
    // RF-MEM-06 - Filter Members by Training Goal (via request params)
    @GetMapping
    public ResponseEntity<Page<MembersDTO>> listMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String trainingGoal) {

        Pageable pageable = PageRequest.of(page, size);

        if (trainingGoal != null && !trainingGoal.isBlank()) {
            return ResponseEntity.ok(membersService.getMembersByTrainingGoal(trainingGoal, pageable));
        }

        return ResponseEntity.ok(membersService.getMembersByPage(pageable));
    }

    // RF-MEM-03 - Update Member
    @PutMapping("/{id}/update")
    public ResponseEntity<MembersDTO> updateMember(@NonNull @PathVariable String id, @RequestBody MembersDTO memberDTO) {
        MembersDTO updated = membersService.updateMember(id, memberDTO);
        return ResponseEntity.ok(updated);
    }

    // RF-MEM-04 - Activate Member
    @PostMapping("/{id}/activate")
    public ResponseEntity<MembersDTO> activateMember(@NonNull @PathVariable String id) {
        MembersDTO activated = membersService.activateMember(id);
        return ResponseEntity.ok(activated);
    }

    // RF-MEM-04 - Suspend Member
    @PostMapping("/{id}/suspend")
    public ResponseEntity<Void> suspend(@NonNull @PathVariable String id) {
        membersService.suspendMember(id);
        return ResponseEntity.noContent().build();
    }

    // RF-MEM-04 - Deactivate Member (soft delete)
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<MembersDTO> deactivate(@NonNull @PathVariable String id) {
        MembersDTO deactivated = membersService.deactivateMember(id);
        return ResponseEntity.ok(deactivated);
    }

    // RF-MEM-02 - Get Member by ID
    @GetMapping("/{id}")
    public ResponseEntity<MembersDTO> getMemberById(@NonNull @PathVariable String id) {
        return ResponseEntity.ok(membersService.getMemberById(id));
    }

    // RF-MEM-07 - Session History (list sessions)
    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionRecordDTO>> getSessions(@NonNull @PathVariable String id) {
        return ResponseEntity.ok(membersService.getMemberSessions(id));
    }

    // RF-MEM-07 - Session History (add session)
    @PostMapping("/{id}/sessions")
    public ResponseEntity<SessionRecordDTO> addSession(@NonNull @PathVariable String id, @RequestBody SessionRecordDTO sessionDTO) {
        SessionRecordDTO created = membersService.addSessionToMember(id, sessionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // RF-MEM-07 - View Member Profile
    @GetMapping("/{id}/profile")
    public ResponseEntity<MemberProfileDTO> getProfile(@NonNull @PathVariable String id) {
        return ResponseEntity.ok(membersService.getProfile(id));
    }
}
