/*
 * Copyright (c) eHealth
 */
package be.ehealth.businessconnector.testcommons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import static org.junit.Assert.assertTrue;


/**
 * tests {@link SessionTestUtils}.
 * 
 * @author EH076
 * 
 */
public class SessionTestUtilsTest {

    @Test
    public void testConstructorIsPrivate() throws Exception {
        Constructor<SessionTestUtils> constructor = SessionTestUtils.class.getDeclaredConstructor();
        assertTrue("Constructor is not private", Modifier.isPrivate(constructor.getModifiers()));

        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            Assert.fail("should have thrown UnsupportedOperationException");
        } catch (InvocationTargetException e) {
            Assert.assertTrue(e.getTargetException() instanceof UnsupportedOperationException);
            UnsupportedOperationException usoe = (UnsupportedOperationException) e.getTargetException();
            Assert.assertEquals("class may not be initialized, only static methods should be used", usoe.getMessage());
        }
    }

}
