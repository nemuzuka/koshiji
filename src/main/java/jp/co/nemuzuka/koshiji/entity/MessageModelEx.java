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
package jp.co.nemuzuka.koshiji.entity;

import java.io.Serializable;

import jp.co.nemuzuka.koshiji.model.MessageModel;

/**
 * Message表示用Entitiy.
 * @author kazumune
 */
public class MessageModelEx implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    
    /** MessageModel. */
    private MessageModel model;

    /** 作成者名. */
    private String createMemberName;

    /** 最終更新日. */
    private String lastUpdate;

    /** 未読Messageか. */
    private boolean unread;

    /** 自分が作成者か. */
    private boolean create;
    
    /**
     * @return model
     */
    public MessageModel getModel() {
        return model;
    }

    /**
     * @param model セットする model
     */
    public void setModel(MessageModel model) {
        this.model = model;
    }

    /**
     * @return createMemberName
     */
    public String getCreateMemberName() {
        return createMemberName;
    }

    /**
     * @param createMemberName セットする createMemberName
     */
    public void setCreateMemberName(String createMemberName) {
        this.createMemberName = createMemberName;
    }

    /**
     * @return lastUpdate
     */
    public String getLastUpdate() {
        return lastUpdate;
    }

    /**
     * @param lastUpdate セットする lastUpdate
     */
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * @return unread
     */
    public boolean isUnread() {
        return unread;
    }

    /**
     * @param unread セットする unread
     */
    public void setUnread(boolean unread) {
        this.unread = unread;
    }

    /**
     * @return create
     */
    public boolean isCreate() {
        return create;
    }

    /**
     * @param create セットする create
     */
    public void setCreate(boolean create) {
        this.create = create;
    }

}
