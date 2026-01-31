package org.estg.schedule.infrastructure.client.dto;

import java.util.List;

public class MemberProfileResponse {

    private MemberResponse member;
    private List<Object> sessions;

    public MemberProfileResponse() {
    }

    public MemberResponse getMember() {
        return member;
    }

    public void setMember(MemberResponse member) {
        this.member = member;
    }

    public List<Object> getSessions() {
        return sessions;
    }

    public void setSessions(List<Object> sessions) {
        this.sessions = sessions;
    }

    public static class MemberResponse {

        private String id;
        private String fullName;
        private String email;
        private String status;

        public MemberResponse() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
