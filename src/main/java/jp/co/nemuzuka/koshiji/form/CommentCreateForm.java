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
package jp.co.nemuzuka.koshiji.form;

import java.io.Serializable;

/**
 * Message登録Form.
 * @author kazumune
 */
public class CommentCreateForm implements Serializable {

    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    /** MessageKey文字列. */
    public String messageKeyString;
    /** メッセージ本文. */
    public String body;
    /**
     * @return messageKeyString
     */
    public String getMessageKeyString() {
        return messageKeyString;
    }
    /**
     * @param messageKeyString セットする messageKeyString
     */
    public void setMessageKeyString(String messageKeyString) {
        this.messageKeyString = messageKeyString;
    }
    /**
     * @return body
     */
    public String getBody() {
        return body;
    }
    /**
     * @param body セットする body
     */
    public void setBody(String body) {
        this.body = body;
    }
}
