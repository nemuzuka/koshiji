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
package jp.co.nemuzuka.koshiji.service;

import com.google.appengine.api.datastore.Key;

/**
 * Message登録・削除に関するService
 * @author kazumune
 */
public interface MessageEditService {
    /** 全てのメンバーが対象. */
    String TARGET_ALL = "all";
    
    /**
     * Message登録.
     * Messageを登録します。
     * 1.登録Memberが指定グループに紐付いていない場合、終了
     * 2.宛先MemberKey文字配列が未指定の場合、終了
     * 3.宛先判断
     *  3-1.宛先MemberKey文字配列に「グループ全員」が指定されている場合、
     *      宛先にはグループに紐付くMember全員が対象とする
     *  3-2.宛先MemberKey文字のMemberを宛先として設定
     *  3-3.登録Memberも宛先に設定
     * 4.Message登録
     * 5.宛先登録
     * 6.未読Message登録
     * @param param 登録パラメータ
     */
    void createMessage(CreateParam param);
    
    /**
     * Messeage登録パラメータ.
     * @author kazumune
     */
    class CreateParam {
        /** 登録MemberKey. */
        public Key createMemberKey;
        /** GroupKey. */
        public Key groupKey;
        /** 宛先MemberKey文字配列. */
        public String[] memberKeyStrings;
        /** メッセージ本文. */
        public String body;
    }
}
