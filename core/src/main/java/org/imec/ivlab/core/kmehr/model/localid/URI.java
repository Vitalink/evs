package org.imec.ivlab.core.kmehr.model.localid;

import java.io.Serializable;
import org.imec.ivlab.core.kmehr.model.localid.util.URIWriter;

public class URI implements LocalId, Serializable {

    private String subjectId;
    private String transactionType;
    private String transactionId;
    private Integer transactionVersion;

    private URIType uriType;

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getTransactionVersion() {
        return transactionVersion;
    }

    public void setTransactionVersion(Integer transactionVersion) {
        this.transactionVersion = transactionVersion;
    }

    public URIType getUriType() {
        return uriType;
    }

    public void setUriType(URIType uriType) {
        this.uriType = uriType;
    }

    public String format() {
        return URIWriter.toString(this);
    }

    @Override
    public String getValue() {
        return transactionId;
    }

}
