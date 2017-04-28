package com.hatim.model;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 消息.
 *
 * @author ScienJus
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @date 15/12/19.
 */
public class MessageModel {

    private long time;

    private String content;

    private long userId;

    private FontModel font;

    public MessageModel(JSONObject json) {
        JSONArray cont = json.getJSONArray("content");
        this.font = cont.getJSONArray(0).getObject(1, FontModel.class);

        final int size = cont.size();
        final StringBuilder contentBuilder = new StringBuilder();
        for (int i = 1; i < size; i++) {
            contentBuilder.append(cont.getString(i));
        }
        this.content = contentBuilder.toString();

        this.time = json.getLongValue("time");
        this.userId = json.getLongValue("from_uin");
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public FontModel getFont() {
        return font;
    }

    public void setFont(FontModel font) {
        this.font = font;
    }

}