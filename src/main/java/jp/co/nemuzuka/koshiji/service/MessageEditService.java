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
     * Message宛先削除.
     * 指定MessageのMemberの関連を削除します。
     * 削除対象Messageの作成Memberが引数のMemberと合致する場合、該当Messageと全ユーザの関連を削除します。
     * @param messageKey 削除対象MessageKey
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     */
    void deleteAddress(Key messageKey, Key memberKey, Key groupKey);
    
    /**
     * Comment登録.
     * Commentを登録します。
     * 1.登録Memberが指定グループに紐付いていない場合、終了
     * 2.指定Messageの宛先に、登録Member/指定グループに紐付いていない場合、終了
     * 3.指定Messageのグループと指定グループが一致しない場合、終了
     * 4.Messageの最終更新日、コメント有りに変更
     * 5.Messageの宛先に対して、未読状態の設定(Comment登録者は除く)
     * @param param 登録パラメータ
     */
    void createComment(CreateCommentParam param);
    
    /**
     * Comment削除.
     * Commentを削除します。削除処理は、コメント本文を固定値に設定することとします。
     * @param messageKey MessageKey
     * @param commentKey CommentKey
     * @param memberKey MemberKey
     */
    void deleteComment(Key messageKey, Key commentKey, Key memberKey);
    
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
    
    /**
     * Comment登録パラメータ.
     * @author kazumune
     */
    class CreateCommentParam extends CreateParam {
        /** MessageKey. */
        public Key messageKey;
    }
}
