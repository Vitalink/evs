package org.imec.ivlab.datagenerator.uploader.service.callback;

import org.imec.ivlab.datagenerator.uploader.exception.CallbackException;

public interface Callback {

    void pass() throws CallbackException;

    void fail(String message) throws CallbackException;

}
