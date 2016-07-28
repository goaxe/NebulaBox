package com.tsinghua.nebulabox.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by huangjian on 7/29/16.
 */
public class SeafCommit {

    private Long revFileSize;
    private String revFileId;
    private Long ctime;
    private String creatorName;
    private String creator;
    private String rootId;
    private String revRenamedOldPath;
    private String deviceName;
    private String parentId;
    private Boolean newMerge;
    private String repoId;
    private int version;
    private String desc;
    private String id;
    private Boolean conflict;
    private String secondParentId;

    static SeafCommit fromJson(JSONObject obj) throws JSONException {
        SeafCommit commit = new SeafCommit();
        commit.revFileSize = obj.getLong("rev_file_size");
        commit.revFileId = obj.getString("rev_file_id");
        commit.ctime = obj.getLong("ctime");
        commit.creatorName = obj.getString("creator_name");
        commit.creator = obj.getString("creator");
        commit.rootId = obj.getString("root_id");
        commit.revRenamedOldPath = obj.optString("rev_renamed_old_path");
        commit.deviceName = obj.optString("device_name");
        commit.parentId = obj.getString("parent_id");
        commit.newMerge = obj.getBoolean("new_merge");
        commit.repoId = obj.getString("repo_id");
        commit.version = obj.getInt("version");
        commit.desc = obj.getString("desc");
        commit.id = obj.getString("id");
        commit.conflict = obj.getBoolean("conflict");
        commit.secondParentId = obj.optString("second_parent_id");
        return commit;
    }


    public SeafCommit() {}

    public Long getRevFileSize() {
        return revFileSize;
    }

    public void setRevFileSize(Long revFileSize) {
        this.revFileSize = revFileSize;
    }

    public String getRevFileId() {
        return revFileId;
    }

    public void setRevFileId(String revFileId) {
        this.revFileId = revFileId;
    }

    public Long getCtime() {
        return ctime;
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRootId() {
        return rootId;
    }

    public void setRootId(String rootId) {
        this.rootId = rootId;
    }

    public String getRevRenamedOldPath() {
        return revRenamedOldPath;
    }

    public void setRevRenamedOldPath(String revRenamedOldPath) {
        this.revRenamedOldPath = revRenamedOldPath;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Boolean getNewMerge() {
        return newMerge;
    }

    public void setNewMerge(Boolean newMerge) {
        this.newMerge = newMerge;
    }

    public String getRepoId() {
        return repoId;
    }

    public void setRepoId(String repoId) {
        this.repoId = repoId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getConflict() {
        return conflict;
    }

    public void setConflict(Boolean conflict) {
        this.conflict = conflict;
    }

    public String getSecondParentId() {
        return secondParentId;
    }

    public void setSecondParentId(String secondParentId) {
        this.secondParentId = secondParentId;
    }

    /**
     * "rev_file_size":300544,
     "rev_file_id": "b88ab96740ef53249b9d21fb3fa28050842266ba",
     "ctime": 1469631976,
     "creator_name": "a@qq.com",
     "creator": "0000000000000000000000000000000000000000",
     "root_id": "ff4b780a3041449287f3acd95585fcd7d7f2c30d",
     "rev_renamed_old_path": null,
     "device_name": null,
     "parent_id": "fbd423ec5d16f9f0c97cdfbe3470badf3194ee9a",
     "new_merge": false,
     "repo_id": "d8206f34-af9f-4534-861d-31f129071453",
     "version": 1,
     "desc": "Added \"seafile-tutorial.doc\"",
     "id": "9515920fe9d72066e945939882ee62e11ff5e627",
     "conflict": false,
     "second_parent_id": null
     */
}
