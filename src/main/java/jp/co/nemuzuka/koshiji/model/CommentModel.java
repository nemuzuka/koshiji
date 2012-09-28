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
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * コメントを管理するModel.
 * @author kazumune
 */
@Model(schemaVersion = 1)
public class CommentModel extends AbsModel {

    /** CommentKey. */
    //自動採番
    @Attribute(primaryKey=true)
    private Key key;
    
    /** MessageKey. */
    private Key messageKey;

    /** 本文. */
    @Attribute(unindexed=true)
    private Text body;
    
    /** 作成MemberKey. */
    private Key createMemberKey;
    
    /** 最終更新日. */
    private Date lastUpdate;

    /** シーケンス値. */
    private Long no;

    /**
     * 本文文字列取得.
     * @return 本文文字列
     */
    public String getBodyText() {
        if(body != null) {
            return body.getValue();
        }
        return "";
    }

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
    @JSONHint(ignore=true)
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
     * @return body
     */
    @JSONHint(ignore=true)
    public Text getBody() {
        return body;
    }

    /**
     * @param body セットする body
     */
    public void setBody(Text body) {
        this.body = body;
    }

    /**
     * @return createMemberKey
     */
    @JSONHint(ignore=true)
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
     * @return lastUpdate
     */
    @JSONHint(ignore=true)
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
     * @return no
     */
    public Long getNo() {
        return no;
    }

    /**
     * @param no セットする no
     */
    public void setNo(Long no) {
        this.no = no;
    }

    /**
     * @param key セットする key
     */
    public void setKey(Key key) {
        this.key = key;
    }
}
