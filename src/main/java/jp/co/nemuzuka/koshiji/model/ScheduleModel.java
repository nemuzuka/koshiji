/*
 * Copyright 2012 Kazumune Katagiri. (http://d.hatena.ne.jp/nemuzuka)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.nemuzuka.koshiji.model;

import java.util.Date;
import java.util.Set;

import net.arnx.jsonic.JSONHint;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * スケジュールを管理するModel.
 * @author kazumune
 */
@Model(schemaVersion = 1)
public class ScheduleModel extends AbsModel {

    /** スケジュールKey. */
    @Attribute(primaryKey=true)
    private Key key;

    /** 件名. */
    @Attribute(unindexed=true)
    private String title;

    /** メモ. */
    private Text memo;
    
    /** 開始日. */
    private Date startDate;

    /** 
     * 開始時刻.
     * HHmm形式
     */
    private String startTime;

    /** 終了日. */
    private Date endDate;

    /** 
     * 終了時刻.
     * HHmm形式
     */
    @Attribute(unindexed=true)
    private String endTime;
    
    /** 非公開フラグ. */
    @Attribute(unindexed=true)
    private boolean closed;
    
    /** 作成MemberKey. */
    @Attribute(unindexed=true)
    private Key createMemberKey;

    /** 作成日. */
    @Attribute(unindexed=true)
    private Date createDateTime;

    /** 最終更新日. */
    private Date lastUpdate;
    
    /** 関連MemberKeyStringSet. */
    private Set<String> connMember;
    
    /**
     * @return key
     */
    @JSONHint(ignore=true)
    public Key getKey() {
        return key;
    }

    /**
     * Memo文字列取得.
     * @return Memo文字列
     */
    public String getMemoStr() {
        if(memo == null) {
            return "";
        }
        return memo.getValue();
    }
    
    /**
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title セットする title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return memo
     */
    @JSONHint(ignore=true)
    public Text getMemo() {
        return memo;
    }

    /**
     * @param memo セットする memo
     */
    public void setMemo(Text memo) {
        this.memo = memo;
    }

    /**
     * @return startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate セットする startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime セットする startTime
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate セットする endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime セットする endTime
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return closed
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * @param closed セットする closed
     */
    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    /**
     * @return createMemberKey
     */
    public Key getCreateMemberKey() {
        return createMemberKey;
    }

    /**
     * @param createMemberKey セットする createMemberKey
     */
    public void setCreateMemberKey(Key createMemberKey) {
        this.createMemberKey = createMemberKey;
    }

    /**
     * @return createDateTime
     */
    public Date getCreateDateTime() {
        return createDateTime;
    }

    /**
     * @param createDateTime セットする createDateTime
     */
    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    /**
     * @return lastUpdate
     */
    public Date getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate セットする lastUpdate
     */
    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    /**
     * @return connMember
     */
    public Set<String> getConnMember() {
        return connMember;
    }

    /**
     * @param connMember セットする connMember
     */
    public void setConnMember(Set<String> connMember) {
        this.connMember = connMember;
    }

    /**
     * @param key セットする key
     */
    public void setKey(Key key) {
        this.key = key;
    }
}
