package org.imec.ivlab.core.model.upload.kmehrentrylist;

import java.io.UnsupportedEncodingException;

public class BusinessData {

    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BusinessData(String content) {
        this.content = content;
    }

    public BusinessData(byte[] contentAsBytes) {
        try {
            this.content = new String(contentAsBytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            this.content = new String(contentAsBytes);
        }
    }

    public byte[] getContentAsBytes() {

        byte[] businessDataBytes = null;

        if (content == null) {
            return businessDataBytes;
        }
        try {
            businessDataBytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            businessDataBytes = content.getBytes();
        }
        return businessDataBytes;
    }

}
