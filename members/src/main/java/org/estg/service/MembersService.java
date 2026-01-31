package org.estg.service;

import java.util.List;
import java.util.stream.Collectors;

import org.estg.data.MembersRepository;
import org.estg.data.SessionRecordRepository;
import org.estg.domain.event.MemberActivatedEvent;
import org.estg.domain.event.MemberProfileUpdatedEvent;
import org.estg.domain.event.MemberRegisteredEvent;
import org.estg.domain.event.MemberSuspendedEvent;
import org.estg.domain.valueobject.Address;
import org.estg.domain.valueobject.Email;
import org.estg.domain.valueobject.PhoneNumber;
import org.estg.domain.valueobject.TrainingGoal;
import org.estg.dto.MemberProfileDTO;
import org.estg.dto.MembersDTO;
import org.estg.dto.SessionRecordDTO;
import org.estg.exceptions.DuplicateMemberException;
import org.estg.exceptions.MemberNotFoundException;
import org.estg.infrastructure.event.EventPublisher;
import org.estg.model.Members;
import org.estg.model.SessionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class MembersService {

    @Autowired
    private MembersRepository memberRepository;

    @Autowired
    private SessionRecordRepository sessionRecordRepository;

    @Autowired
    private EventPublisher eventPublisher;

    // RF-MEM-01 - Create Member
    public MembersDTO registerMember(MembersDTO memberDTO) {
        log.info("Registering new member: {}", memberDTO.getFullName());
        Members member = new Members();

        member.setFullName(memberDTO.getFullName());
        if (memberDTO.getEmail() != null) {
            member.setEmail(new Email(memberDTO.getEmail()));
        }
        member.setDateOfBirth(memberDTO.getDateOfBirth());

        if (memberDTO.getPhoneNumber() != null) {
            member.setPhoneNumber(new PhoneNumber(memberDTO.getPhoneNumber()));
        }
        if (memberDTO.getTrainingGoal() != null) {
            member.setTrainingGoal(new TrainingGoal(memberDTO.getTrainingGoal()));
        }

        member.setExperienceLevel(memberDTO.getExperienceLevel());

        if (memberDTO.getAddress() != null || memberDTO.getCity() != null || memberDTO.getZipCode() != null) {
            member.setAddress(new Address(memberDTO.getAddress(), memberDTO.getCity(), memberDTO.getZipCode()));
        }

        Members savedMember = registerInternal(member);
        log.debug("Member registered with ID: {}", savedMember.getId());
        return toDto(savedMember);
    }

    // RF-MEM-02 - Get Member by ID
    @Transactional(readOnly = true)
    public MembersDTO getMemberById(@NonNull String id) {
        log.debug("Fetching member from DB: {}", id);
        Members member = findMemberOrThrow(id);
        return toDto(member);
    }

    // RF-MEM-07 - View Member Profile
    @Transactional(readOnly = true)
    public MemberProfileDTO getProfile(@NonNull String id) {
        Members member = findMemberOrThrow(id);
        List<SessionRecordDTO> sessions = getMemberSessions(id);
        return new MemberProfileDTO(toDto(member), sessions);
    }

    // RF-MEM-03 - Update Member (partial update)
    public MembersDTO updateMember(@NonNull String id, MembersDTO memberDTO) {
        Members member = findMemberOrThrow(id);

        if (memberDTO.getFullName() != null) {
            member.setFullName(memberDTO.getFullName());
        }

        if (memberDTO.getEmail() != null && !memberDTO.getEmail().equals(getEmailValue(member))) {
            Email newEmail = new Email(memberDTO.getEmail());
            if (memberRepository.existsByEmailValue(newEmail.getValue())) {
                throw new DuplicateMemberException("Member with email " + newEmail.getValue() + " already exists");
            }
            member.setEmail(newEmail);
        }

        if (memberDTO.getDateOfBirth() != null) {
            member.setDateOfBirth(memberDTO.getDateOfBirth());
        }

        if (memberDTO.getPhoneNumber() != null) {
            member.setPhoneNumber(new PhoneNumber(memberDTO.getPhoneNumber()));
        }

        if (memberDTO.getTrainingGoal() != null) {
            member.setTrainingGoal(new TrainingGoal(memberDTO.getTrainingGoal()));
        }

        if (memberDTO.getExperienceLevel() != null) {
            member.setExperienceLevel(memberDTO.getExperienceLevel());
        }

        if (memberDTO.getAddress() != null || memberDTO.getCity() != null || memberDTO.getZipCode() != null) {
            Address existingAddress = member.getAddress();
            member.setAddress(new Address(
                    memberDTO.getAddress() != null ? memberDTO.getAddress()
                            : existingAddress != null ? existingAddress.getLine() : null,
                    memberDTO.getCity() != null ? memberDTO.getCity()
                            : existingAddress != null ? existingAddress.getCity() : null,
                    memberDTO.getZipCode() != null ? memberDTO.getZipCode()
                            : existingAddress != null ? existingAddress.getZipCode() : null));
        }

        member.validate();
        Members updatedMember = memberRepository.save(member);

        eventPublisher.publish(new MemberProfileUpdatedEvent(updatedMember.getId(), updatedMember.getUpdatedAt()));
        return toDto(updatedMember);
    }

    // RF-MEM-04 - Activate Member
    public MembersDTO activateMember(@NonNull String id) {
        Members member = findMemberOrThrow(id);
        Members savedMember = activateInternal(member);
        return toDto(savedMember);
    }

    // RF-MEM-04 - Suspend Member
    public void suspendMember(@NonNull String id) {
        Members member = findMemberOrThrow(id);
        member.suspend();
        memberRepository.save(member);

        eventPublisher.publish(new MemberSuspendedEvent(id, "Suspended by admin"));
    }

    // RF-MEM-04 - Deactivate Member (soft delete)
    public MembersDTO deactivateMember(@NonNull String id) {
        Members member = findMemberOrThrow(id);
        member.deactivate();
        Members saved = memberRepository.save(member);

        // Gymhubv16 requires emitting events when members are removed/deactivated.
        // MemberSuspendedEvent is used as the generic "membership disabled" event in this codebase.
        eventPublisher.publish(new MemberSuspendedEvent(id, "Deactivated"));

        return toDto(saved);
    }

    // RF-MEM-05 - List Members (pagination)
    @Transactional(readOnly = true)
    public Page<MembersDTO> getMembersByPage(@NonNull Pageable pageable) {
        return memberRepository.findByStatus(Members.MemberStatus.ACTIVE, pageable).map(this::toDto);
    }

    // RF-MEM-06 - Filter by Training Goal
    @Transactional(readOnly = true)
    public Page<MembersDTO> getMembersByTrainingGoal(@NonNull String trainingGoal, @NonNull Pageable pageable) {
        return memberRepository.findByTrainingGoal(trainingGoal, pageable).map(this::toDto);
    }

    // RF-MEM-07 - Session History
    @Transactional(readOnly = true)
    public List<SessionRecordDTO> getMemberSessions(@NonNull String memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new MemberNotFoundException("Member not found with id: " + memberId);
        }

        return sessionRecordRepository.findByMemberIdOrderBySessionDateTimeDesc(memberId)
                .stream()
                .map(session -> toSessionDto(session, memberId))
                .collect(Collectors.toList());
    }

    public SessionRecordDTO addSessionToMember(@NonNull String memberId, SessionRecordDTO sessionDTO) {
        Members member = findMemberOrThrow(memberId);

        SessionRecord session = toSessionEntity(sessionDTO);
        if (session == null) {
            throw new IllegalArgumentException("Failed to create session from DTO");
        }
        session.validate();
        member.addSession(session);

        SessionRecord savedSession = sessionRecordRepository.save(session);
        return toSessionDto(savedSession, memberId);
    }

    // --------------------
    // Internal domain operations
    // --------------------

    private Members registerInternal(@NonNull Members candidate) {
        if (candidate.getEmail() == null) {
            throw new IllegalArgumentException("Email is required");
        }

        if (memberRepository.existsByEmailValue(candidate.getEmail().getValue())) {
            throw new DuplicateMemberException("Member with email " + candidate.getEmail().getValue() + " already exists");
        }

        candidate.setStatus(Members.MemberStatus.ACTIVE);
        candidate.validate();

        Members saved = memberRepository.save(candidate);

        eventPublisher.publish(new MemberRegisteredEvent(
                saved.getId(),
                saved.getEmail().getValue(),
                saved.getFullName()));

        return saved;
    }

    private Members activateInternal(@NonNull Members member) {
        member.activate();
        Members saved = memberRepository.save(member);
        eventPublisher.publish(new MemberActivatedEvent(saved.getId()));
        return saved;
    }

    // --------------------
    // Helpers
    // --------------------

    private Members findMemberOrThrow(@NonNull String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Member not found with id: " + id));
    }

    @Nullable
    private MembersDTO toDto(@Nullable Members member) {
        if (member == null) {
            return null;
        }

        MembersDTO dto = new MembersDTO();
        dto.setId(member.getId());
        dto.setFullName(member.getFullName());
        dto.setEmail(getEmailValue(member));
        dto.setDateOfBirth(member.getDateOfBirth());
        dto.setPhoneNumber(getPhoneNumberValue(member));
        dto.setTrainingGoal(getTrainingGoalValue(member));
        dto.setExperienceLevel(member.getExperienceLevel());
        dto.setStatus(member.getStatus() != null ? member.getStatus().name() : null);

        if (member.getAddress() != null) {
            dto.setAddress(member.getAddress().getLine());
            dto.setCity(member.getAddress().getCity());
            dto.setZipCode(member.getAddress().getZipCode());
        }

        return dto;
    }

    @Nullable
    private SessionRecordDTO toSessionDto(@Nullable SessionRecord session, @NonNull String memberId) {
        if (session == null) {
            return null;
        }

        SessionRecordDTO dto = new SessionRecordDTO();
        dto.setId(session.getId());
        dto.setMemberId(memberId);
        dto.setSessionDateTime(session.getSessionDateTime());
        dto.setDescription(session.getDescription());
        dto.setTrainerName(session.getTrainerName());
        dto.setSessionType(session.getSessionType());
        dto.setCompleted(session.getCompleted());
        return dto;
    }

    @Nullable
    private SessionRecord toSessionEntity(@Nullable SessionRecordDTO dto) {
        if (dto == null) {
            return null;
        }

        SessionRecord session = new SessionRecord();
        session.setId(dto.getId());
        session.setSessionDateTime(dto.getSessionDateTime());
        session.setDescription(dto.getDescription());
        session.setTrainerName(dto.getTrainerName());
        session.setSessionType(dto.getSessionType());
        session.setCompleted(dto.getCompleted());
        return session;
    }

    private String getEmailValue(Members member) {
        return member.getEmail() != null ? member.getEmail().getValue() : null;
    }

    private String getTrainingGoalValue(Members member) {
        return member.getTrainingGoal() != null ? member.getTrainingGoal().getValue() : null;
    }

    private String getPhoneNumberValue(Members member) {
        return member.getPhoneNumber() != null ? member.getPhoneNumber().getValue() : null;
    }
}
