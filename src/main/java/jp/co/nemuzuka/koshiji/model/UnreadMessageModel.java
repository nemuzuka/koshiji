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

import net.arnx.jsonic.JSONHint;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.CreationDate;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * 未読MessageModel.
 * Memberに紐付く未読Messageを管理します。
 * @author kazumune
 */
@Model(schemaVersion = 1)
public class UnreadMessageModel extends AbsModel {

    /** 未読MessageKey. */
    //自動採番
    @Attribute(primaryKey=true)
    private Key key;

    /** MessageKey. */
    private Key messageKey;
    
    /** MemberKey. */
    private Key memberKey;
    
    /** GroupKey. */
    private Key groupKey;
    
    /** 登録日時(ソート用). */
    @Attribute(listener = CreationDate.class)
    private Date createAt;
    
    /**
     * @return key
     */
    @JSONHint(ignore=true)
    public Key getKey() {
        return key;
    }

    /**
     * @return messageKey
     */
    public Key getMessageKey() {
        return messageKey;
    }

    /**
     * @param messageKey セットする messageKey
     */
    public void setMessageKey(Key messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * @return memberKey
     */
    public Key getMemberKey() {
        return memberKey;
    }

    /**
     * @param memberKey セットする memberKey
     */
    public void setMemberKey(Key memberKey) {
        this.memberKey = memberKey;
    }

    /**
     * @param key セットする key
     */
    public void setKey(Key key) {
        this.key = key;
    }

    /**
     * @return createAt
     */
    public Date getCreateAt() {
        return createAt;
    }

    /**
     * @param createAt セットする createAt
     */
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /**
     * @return groupKey
     */
    public Key getGroupKey() {
        return groupKey;
    }

    /**
     * @param groupKey セットする groupKey
     */
    public void setGroupKey(Key groupKey) {
        this.groupKey = groupKey;
    }
}
