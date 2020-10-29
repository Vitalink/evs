/*
 * Copyright (c) eHealth
 */
package be.fgov.ehealth.technicalconnector.tests.utils;

import be.ehealth.technicalconnector.service.etee.domain.EncryptionToken;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.spi.LoggingEvent;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;


/**
 * Assert tools
 * 
 * @author EH053
 * 
 */
public class AssertTools {

    private static final Logger LOG = LoggerFactory.getLogger(AssertTools.class);

    /**
     * Verify the validity of an {@link EncryptionToken}
     * 
     * @param token
     */
    public static void assertEncryptionToken(EncryptionToken token) {
        try {
            token.getCertificate().checkValidity();
        } catch (CertificateNotYetValidException e) {
            Assert.fail(e.getMessage());
        } catch (CertificateExpiredException e) {
            Assert.fail(e.getMessage());
        }
    }

    /**
     * Verify that that a {@link LoggingEvent} a certain message contains.
     */
    @SuppressWarnings({
        "rawtypes", "unchecked"
    })
    public static void assertLoggingEvent(LoggingEvent event, String level, Class clazz, final String expectedMessage) {
        assertThat(event.getLevel().toString().toLowerCase(), is(level.toLowerCase()));
        assertThat((String) event.getMessage(), new BaseMatcher() {

            private Pattern pattern = Pattern.compile(expectedMessage);

            @Override
            public boolean matches(Object item) {
                return pattern.matcher(item.toString()).matches();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("matches regex ").appendValue(expectedMessage);
            }
        });
        assertThat(event.getLoggerName(), is(clazz.getName()));

    }
/**
 * Verifies that the object is really {@link java.io.Serializable}
 * @param expected
 * @param verifyTransientFields
 * @throws Exception
 */
    public static void assertSerializable(java.io.Serializable expected, boolean verifyTransientFields) throws Exception {
        byte[] serializedObject = SerializationUtils.serialize(expected);
        Object actual = SerializationUtils.deserialize(serializedObject);
        LOG.debug("Before serialization: " + ToStringBuilder.reflectionToString(expected));
        LOG.debug("After serialization: " + ToStringBuilder.reflectionToString(actual));

        Assert.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, false));
        if (verifyTransientFields) {
            for (int i = 0; i < expected.getClass().getDeclaredFields().length; i++) {
                Field field = expected.getClass().getDeclaredFields()[i];
                if (Modifier.isTransient(field.getModifiers())) {
                    field.setAccessible(true);
                    Object actualResult = field.get(actual);
                    Object expectedResult = field.get(expected);
                    LOG.debug("Before serialization: " + ToStringBuilder.reflectionToString(expectedResult));
                    LOG.debug("After serialization: " + ToStringBuilder.reflectionToString(actualResult));
                    Assert.assertTrue(EqualsBuilder.reflectionEquals(actualResult, expectedResult));
                }
            }
        }
    }

    public static void assertEquals(X509Certificate expected, X509Certificate actual) throws Exception {
        byte[] expectedCert = expected.getEncoded();
        byte[] actualCert = actual.getEncoded();
        Assert.assertTrue("X509Certificates aren't equal.", Arrays.equals(expectedCert, actualCert));

    }
}
