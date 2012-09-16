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

import net.arnx.jsonic.JSONHint;

import org.slim3.datastore.Attribute;
import org.slim3.datastore.Model;

import com.google.appengine.api.datastore.Key;

/**
 * メンバーとグループの関連を管理するModel.
 * @author kazumune
 */
@Model(schemaVersion = 1)
public class MemberGroupConnModel extends AbsModel {

    /** メンバーグループ関連Key. */
    //自動採番
    @Attribute(primaryKey=true)
    private Key key;
    
    /** メンバーKey. */
    private Key memberKey;
    
    /** グループKey. */
    private Key groupKey;
    
    /** 管理者である場合、true. */
    private boolean admin;

    /** 並び順. */
    private Long sortNum;
    
    /**
     * @return key
     */
    @JSONHint(ignore=true)
    public Key getKey() {
        return key;
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

    /**
     * @return admin
     */
    public boolean isAdmin() {
        return admin;
    }

    /**
     * @param admin セットする admin
     */
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    /**
     * @return sortNum
     */
    public Long getSortNum() {
        return sortNum;
    }

    /**
     * @param sortNum セットする sortNum
     */
    public void setSortNum(Long sortNum) {
        this.sortNum = sortNum;
    }

    /**
     * @param key セットする key
     */
    public void setKey(Key key) {
        this.key = key;
    }
}
