package service;

import data.SessionData;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.*;
import util.ValidationUtil;

public class NameChangeRequestService {
    private final SessionData data;

    public NameChangeRequestService(SessionData data) {
        this.data = data;
    }

    public NameChangeRequest submit(Student student, String proposedName, String reason) {
        ValidationUtil.requireText(proposedName, "Proposed name");
        ValidationUtil.requireText(reason, "Reason");
        if (student.getDisplayName().equalsIgnoreCase(proposedName.trim())) {
            throw new IllegalArgumentException("Proposed name must be different from the current displayed name.");
        }
        for (NameChangeRequest request : data.getNameChangeRequests()) {
            if (request.getStudentUserId().equals(student.getId()) && request.getStatus() == RequestStatus.PENDING) {
                throw new IllegalArgumentException("You already have a pending displayed-name change request.");
            }
        }
        NameChangeRequest request = new NameChangeRequest(data.nextNameRequestId(), student.getId(),
                student.getDisplayName(), proposedName.trim(), reason.trim());
        data.getNameChangeRequests().add(request);
        data.addAudit(student, "Name-change request submitted", request.getId() + ":" + student.getId(), student.getDisplayName(), proposedName, reason);
        return request;
    }

    public List<NameChangeRequest> forStudent(Student student) {
        List<NameChangeRequest> result = new ArrayList<NameChangeRequest>();
        for (NameChangeRequest request : data.getNameChangeRequests()) {
            if (request.getStudentUserId().equals(student.getId())) {
                result.add(request);
            }
        }
        return result;
    }

    public List<NameChangeRequest> all(AdminUser admin) {
        requireAdmin(admin);
        return new ArrayList<NameChangeRequest>(data.getNameChangeRequests());
    }

    public void decide(AdminUser admin, NameChangeRequest request, boolean approve, String remarks) {
        requireAdmin(admin);
        if (request == null) {
            throw new IllegalArgumentException("Select a request.");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalArgumentException("Only pending requests can be decided.");
        }
        Student student = (Student) data.getUsers().get(request.getStudentUserId());
        String previous = student.getDisplayName();
        request.setReviewingAdminId(admin.getId());
        request.setDecisionAt(LocalDateTime.now());
        request.setDecisionRemarks(remarks);
        if (approve) {
            request.setStatus(RequestStatus.APPROVED);
            student.setDisplayedName(request.getProposedDisplayedName());
        } else {
            request.setStatus(RequestStatus.REJECTED);
        }
        data.addAudit(admin, approve ? "Name-change request approved" : "Name-change request rejected",
                request.getId() + ":" + student.getId(), previous,
                approve ? student.getDisplayName() : "Rejected", remarks);
    }

    private void requireAdmin(User user) {
        if (user == null || user.getRole() != Role.ADMIN) {
            throw new SecurityException("Administrator access is required.");
        }
    }
}
