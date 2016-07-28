package com.tsinghua.nebulabox.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangjian on 7/29/16.
 */
public class SeafShare {

    private String userName;
    private int viewCount;
    private String ctime;
    private String token;
    private String repoId;
    private String link;
    private String expireDate;
    private String path;
    private Boolean isExpired;

    static SeafShare fromJson(JSONObject obj) throws JSONException {
        SeafShare share = new SeafShare();
        share.userName = obj.getString("username");
        share.viewCount = obj.getInt("view_cnt");
        share.ctime = obj.getString("ctime");
        share.token = obj.getString("token");
        share.repoId = obj.getString("repo_id");
        share.link = obj.getString("link");
        share.expireDate = obj.optString("expire_data");
        share.path = obj.getString("path");
        share.isExpired = obj.getBoolean("is_expired");
        return share;
    }

    public SeafShare() {}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getExpired() {
        return isExpired;
    }

    public void setExpired(Boolean expired) {
        isExpired = expired;
    }
}
