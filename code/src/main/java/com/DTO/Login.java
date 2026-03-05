

import jakarta.validation.constraints.NotBlank;

public class Login {
@NotBlank
private String identifier;

@NotBlank
@Size(min =6)
private String password;


public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
