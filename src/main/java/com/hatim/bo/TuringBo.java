package com.hatim.bo;

/**
 * Created by Hatim on 2017/5/9.
 */
public class TuringBo {

    private String code;
    private String text;

    @Override
    public String toString() {
        return "TuringBo{" +
                "code='" + code + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
