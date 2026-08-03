package io.github.son1004007.opsmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "opsmate.security")
public class SecurityProperties {

    private String requesterPassword = "";
    private String approverPassword = "";
    private String buyerPassword = "";
    private String auditorPassword = "";

    public String getRequesterPassword() {
        return requesterPassword;
    }

    public void setRequesterPassword(String requesterPassword) {
        this.requesterPassword = requesterPassword;
    }

    public String getApproverPassword() {
        return approverPassword;
    }

    public void setApproverPassword(String approverPassword) {
        this.approverPassword = approverPassword;
    }

    public String getBuyerPassword() {
        return buyerPassword;
    }

    public void setBuyerPassword(String buyerPassword) {
        this.buyerPassword = buyerPassword;
    }

    public String getAuditorPassword() {
        return auditorPassword;
    }

    public void setAuditorPassword(String auditorPassword) {
        this.auditorPassword = auditorPassword;
    }
}
