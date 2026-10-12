package service;

import data.SessionData;

public class AppServices {
    public final SessionData data;
    public final UserService users;
    public final AuthenticationService auth;
    public final DepartmentService departments;
    public final ClearanceService clearances;
    public final NameChangeRequestService names;
    public final AuditService audits;

    public AppServices(SessionData data) {
        this.data = data;
        this.users = new UserService(data);
        this.auth = new AuthenticationService(data, users);
        this.departments = new DepartmentService(data);
        this.clearances = new ClearanceService(data);
        this.names = new NameChangeRequestService(data);
        this.audits = new AuditService(data);
    }
}
