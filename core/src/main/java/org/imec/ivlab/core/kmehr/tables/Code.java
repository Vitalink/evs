package org.imec.ivlab.core.kmehr.tables;

import java.util.Objects;

public class Code {

    private String S;
    private String SV;
    private String L;
    private String value;

    public String getS() {
        return S;
    }

    public void setS(String s) {
        S = s;
    }

    public String getSV() {
        return SV;
    }

    public void setSV(String SV) {
        this.SV = SV;
    }

    public String getL() {
        return L;
    }

    public void setL(String l) {
        L = l;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Code(String s, String SV, String l, String value) {
        S = s;
        this.SV = SV;
        L = l;
        this.value = value;
    }

    public Code(String s, String SV, String l) {
        S = s;
        this.SV = SV;
        L = l;
    }

    public Code(String s, String SV) {
        S = s;
        this.SV = SV;
    }


    @Override
    public boolean equals(Object o) {
        // self check
        if (this == o)
            return true;
        // null check
        if (o == null)
            return false;
        // type check and cast
        if (getClass() != o.getClass())
            return false;
        Code code = (Code) o;
        // field comparison
        return Objects.equals(S, code.getS())
                && Objects.equals(SV, code.getSV()) && Objects.equals(L, code.getL())  && Objects.equals(value, code.getValue());
    }
}
