/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.utils;

import be.ehealth.technicalconnector.exception.SessionManagementException;
import be.ehealth.technicalconnector.exception.TechnicalConnectorException;
import be.ehealth.technicalconnector.service.etee.Crypto;
import be.ehealth.technicalconnector.service.etee.domain.EncryptionToken;
import be.ehealth.technicalconnector.service.etee.domain.UnsealedData;
import be.ehealth.technicalconnector.service.etee.impl.AbstractEndToEndCrypto;
import be.ehealth.technicalconnector.service.kgss.domain.KeyResult;
import be.ehealth.technicalconnector.service.sts.security.Credential;
import be.ehealth.technicalconnector.service.sts.security.SAMLToken;
import be.ehealth.technicalconnector.session.SessionItem;
import be.ehealth.technicalconnector.session.SessionManager;
import be.ehealth.technicalconnector.session.SessionServiceWithCache;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.crypto.SecretKey;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * utilities to test with dummy sessions and tokens.
 * 
 * @author EH076
 * 
 * @since
 * 
 */
public final class SessionTestUtils {

    private static final Logger LOG = LoggerFactory.getLogger(SessionTestUtils.class);


    /**
     * 
     */
    private SessionTestUtils() {
        throw new UnsupportedOperationException("class may not be initialized, only static methods should be used");
    }


    public static SAMLToken getDummyToken() {
        return new SAMLToken() {

            @Override
            public PublicKey getPublicKey() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public String getProviderName() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public PrivateKey getPrivateKey() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public KeyStore getKeyStore() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public String getIssuerQualifier() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public String getIssuer() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public Certificate[] getCertificateChain() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public X509Certificate getCertificate() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public String getAssertionID() {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;
            }

            @Override
            public Element getAssertion() {
                LOG.debug("DUMMY TOKEN : getAssertion() called");
                return null;

            }

            @Override
            public void checkValidity() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : checkValidity() called");
            }

            @Override
            public DateTime getExpirationDateTime() throws TechnicalConnectorException {
                DateTime now = new DateTime();
                return now.plusMonths(5);
            }
            
            @Override
            public CertPath getCertPath() throws TechnicalConnectorException {
                LOG.debug("DUMMY TOKEN : getCertPath() called");
                return null;
            }
        };

    }

    public static SessionManager getDummySessionManager() {
        return new SessionManager() {

            @Override
            public void unloadSession() {
                LOG.debug("DUMMY SESSIONMANGER : unloadSession");
            }

            @Override
            public void registerSessionService(SessionServiceWithCache serviceWithCache) {
                LOG.debug("DUMMY SESSIONMANGER : registerSessionService");

            }

            @Override
            public void loadSession(SAMLToken token, String hokPwd, String encryptionPwd) throws TechnicalConnectorException, SessionManagementException {
                LOG.debug("DUMMY SESSIONMANGER : loadSession(SAMLToken token, String hokPwd, String encryptionPwd)");

            }

            @Override
            public void loadSession(SAMLToken token, String hokPwd) throws TechnicalConnectorException, SessionManagementException {
                LOG.debug("DUMMY SESSIONMANGER : loadSession(SAMLToken token, String hokPwd)");

            }

            @Override
            public void loadEncryptionKeys(String encryptionPwd) throws TechnicalConnectorException, SessionManagementException {
                LOG.debug("DUMMY SESSIONMANGER : loadEncryptionKeys(String encryptionPwd)");

            }

            @Override
            public boolean hasValidSession() throws SessionManagementException {
                LOG.debug("DUMMY SESSIONMANGER : hasValidSession(), returning true");
                return true;
            }

            @Override
            public SessionItem getSession() {
                LOG.debug("DUMMY SESSIONMANGER : getSession() , returning dummy session");
                return getDummySession();
            }

            @Override
            public SessionItem createSessionEidOnly() throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : createSessionEidOnly(), returning dummy session");
                return getDummySession();
            }

            @Override
            public SessionItem createSession(String hokPwd, String encryptionPwd) throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : createSession(String hokPwd, String encryptionPwd), returning dummy session");
                return getDummySession();
            }

            @Override
            public SessionItem createSession(String hokPwd) throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : createSession(String hokPwd), returning dummy session");
                return getDummySession();
            }

            @Override
            public SessionItem createFallbackSession(String identPwd, String hokPwd, String encryptionPwd) throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : createFallbackSession(String identPwd, String hokPwd, String encryptionPwd), returning dummy session");
                return getDummySession();
            }

            @Override
            public SessionItem createFallbackSession(String hokPwd, String encryptionPwd) throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : ");
                return getDummySession();
            }

            @Override
            public SessionItem createFallbackSession(String hokPwd) throws SessionManagementException, TechnicalConnectorException {
                LOG.debug("DUMMY SESSIONMANGER : ");
                return getDummySession();
            }

            @Override
            public void setKeyStore(Map<String, KeyStore> keystores) {
                LOG.debug("DUMMY SESSIONMANGER : setKeyStore");

            }
        };
    }

    public static SessionItem getDummySession() {
        return new SessionItem() {

            private SAMLToken token;

            @Override
            public void setSAMLToken(SAMLToken token) {
                LOG.debug("DUMMY SESSION : setToken()");
                this.token = token;
            }

            @Override
            public void setHolderOfKeyPrivateKeys(Map<String, PrivateKey> hokPrivateKeys) {
                LOG.debug("DUMMY SESSION : setHolderOfKeyPrivateKeys(Map<String, PrivateKey> hokPrivateKeys)");
            }

            @Override
            public void setHolderOfKeyCredential(Credential hokCredential) {
                LOG.debug("DUMMY SESSION : setHolderOfKeyCredential(Credential hokCredential)");

            }

            @Override
            public void setHeaderCredential(Credential headerCredential) throws TechnicalConnectorException {
                LOG.debug("DUMMY SESSION : setHeaderCredential(Credential headerCredential)");

            }

            @Override
            public void setEncryptionPrivateKeys(Map<String, PrivateKey> encryptionPrivateKeys) {
                LOG.debug("DUMMY SESSION : setEncryptionPrivateKeys(Map<String, PrivateKey> encryptionPrivateKeys)");

            }

            @Override
            public void setEncryptionCredential(Credential encryptionCredential) {
                LOG.debug("DUMMY SESSION : setEncryptionCredential(Credential encryptionCredential)");

            }

            @Override
            public SAMLToken getSAMLToken() {
                LOG.debug("DUMMY SESSION : getSAMLToken() , returning " + token);
                return token;
            }

            @Override
            public Map<String, PrivateKey> getHolderOfKeyPrivateKeys() {
                LOG.debug("DUMMY SESSION : getHolderOfKeyPrivateKeys(), returning empty map");
                return new HashMap<String, PrivateKey>();
            }

            @Override
            public Crypto getHolderOfKeyCrypto() throws TechnicalConnectorException {
                LOG.debug("DUMMY SESSION : getHolderOfKeyCrypto(), returning dummy crypto");
                return getDummyCrypto();
            }

            @Override
            public Credential getHolderOfKeyCredential() {
                LOG.debug("DUMMY SESSION : getHolderOfKeyCredential()");
                return null;
            }

            @Override
            public Credential getHeaderCredential() throws TechnicalConnectorException {
                LOG.debug("DUMMY SESSION : getHeaderCredential()");
                return null;
            }

            @Override
            public Map<String, PrivateKey> getEncryptionPrivateKeys() {
                LOG.debug("DUMMY SESSION : getEncryptionPrivateKeys() , returning empty map");
                return new HashMap<String, PrivateKey>();
            }

            @Override
            public Crypto getEncryptionCrypto() throws TechnicalConnectorException {
                LOG.debug("DUMMY SESSION : getEncryptionCrypto(), returning dummy crypto");
                return getDummyCrypto();
            }

            @Override
            public Credential getEncryptionCredential() {
                LOG.debug("DUMMY SESSION : getEncryptionCredential()");
                return null;
            }
        };
    }

    public static Crypto getDummyCrypto() {
        return new AbstractEndToEndCrypto() {
            
            @Override
            public void initialize(Map<String, Object> parameterMap) throws TechnicalConnectorException {
                LOG.debug("DUMMY CRYPTO : initialize(Map<String, Object> parameterMap)");
            }

            @Override
            public byte[] unsealForUnknown(SecretKey key, byte[] protectedMessage) throws TechnicalConnectorException {
                LOG.debug("DUMMY CRYPTO : unsealForUnknown(SecretKey key, byte[] protectedMessage), returning input unchanged");
                return protectedMessage;
            }

            @Override
            public byte[] seal(SigningPolicySelector type, Set<EncryptionToken> paramEncryptionTokenSet, KeyResult symmKey, byte[] content) throws TechnicalConnectorException {
                LOG.debug("DUMMY CRYPTO : seal(SigningPolicySelector type, Set<EncryptionToken> paramEncryptionTokenSet, KeyResult symmKey, byte[] content), returning input unchanged");
                return content;
            }

            @Override
            public UnsealedData unseal(SigningPolicySelector type, byte[] protectedMessage) throws TechnicalConnectorException {
                LOG.debug("DUMMY CRYPTO : unseal(SigningPolicySelector type, byte[] protectedMessage), returning input unchanged");
                UnsealedData result = new UnsealedData();
                result.setContent(new ByteArrayInputStream(protectedMessage));
                result.setSigningTime(new Date());

                return result;
            }

            @Override
            public UnsealedData unseal(SigningPolicySelector type, KeyResult symmKey, byte[] protectedMessage) throws TechnicalConnectorException {
                LOG.debug("DUMMY CRYPTO : unseal(SigningPolicySelector type, KeyResult symmKey, byte[] protectedMessage), returning input unchanged");
                UnsealedData result = new UnsealedData();
                result.setContent(new ByteArrayInputStream(protectedMessage));
                result.setSigningTime(new Date());
                return result;
            }


        };
    }
}
