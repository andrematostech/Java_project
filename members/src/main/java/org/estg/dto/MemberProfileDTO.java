package org.estg.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberProfileDTO {
    private MembersDTO member;
    private List<SessionRecordDTO> sessions;
}