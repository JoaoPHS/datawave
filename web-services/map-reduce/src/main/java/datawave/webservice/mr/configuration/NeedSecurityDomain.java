package datawave.webservice.mr.configuration;

import org.jboss.security.JSSESecurityDomain;

/**
 * Use {@link NeedSSLContextInfo} instead.
 */
@Deprecated(forRemoval = true)
public interface NeedSecurityDomain {

    public void setSecurityDomain(JSSESecurityDomain jsseSecurityDomain);
}
