package io.github.son1004007.opsmate.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 로컬 API 회귀 검증에서만 사용하는 Basic 활성화 flag와 역할별 비밀번호 설정. */
@ConfigurationProperties(prefix = "opsmate.security")
public class SecurityProperties {

    private boolean basicEnabled = true;
    private String requesterPassword = "";
    private String approverPassword = "";
    private String buyerPassword = "";
    private String auditorPassword = "";

    public boolean isBasicEnabled() {
        return basicEnabled;
    }

    public void setBasicEnabled(boolean basicEnabled) {
        this.basicEnabled = basicEnabled;
    }

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
