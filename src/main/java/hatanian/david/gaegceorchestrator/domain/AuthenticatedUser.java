package hatanian.david.gaegceorchestrator.domain;

public class AuthenticatedUser {
    private String email;
    private boolean admin;

    public AuthenticatedUser(String email, boolean admin) {
        this.email = email;
        this.admin = admin;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
