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

import java.util.ArrayList;
import java.util.List;

import net.arnx.jsonic.JSONHint;

import jp.co.nemuzuka.koshiji.entity.CommentModelEx;
import jp.co.nemuzuka.koshiji.entity.MessageModelEx;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;

import com.google.appengine.api.datastore.Key;

/**
 * Message検索に関するService
 * @author kazumune
 */
public interface MessageSearchService {
    
    /**
     * Message一覧取得.
     * 検索条件に合致するMessageを取得します。
     * 1.宛先に指定したMemberKeyとGroupKeyが指定されている一覧を取得
     * 2.1で取得したListを元に、メッセージをソート(最終更新日の降順)
     * 3.2で取得したListに対して表示するページ、ページあたりの表示件数を元に一覧を取得
     * 4.3の一覧が検索結果
     * 5.検索結果が未読の場合、既読状態にする
     * @param param 検索条件
     * @return 検索結果
     */
    Result getList(SearchParam param);
    
    /**
     * Comment一覧取得.
     * 検索条件に合致するCommentを取得します。
     * ・紐付くMessageの宛先にMemberが含まれていない場合
     * ・紐付くMessageのグループとGroupが一致しない場合
     * ・紐付くMemberとグループが紐付いていない場合
     * サイズ0の結果を返します。
     * @param messageKey MessageKey
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     * @return 検索結果
     */
    CommentResult getCommentList(Key messageKey, Key memberKey, Key groupKey);
    
    /**
     * 未読Message一覧取得.
     * 検索条件に紐付く未読Messageを取得します。
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     * @return 検索結果
     */
    List<UnreadMessageModel> getUnreadMessage(Key memberKey, Key groupKey);
    
    /**
     * 検索条件.
     * @author kazumune
     */
    class SearchParam {
        /** MemberKey. */
        public Key memberKey;

        /** GroupKey. */
        public Key groupKey;

        /** 
         * 表示対象ページ. 
         * 1から始まる
         */
        public int pageNo;

        /** 1ページ辺りの表示件数. */
        public int limit;
        
        /** 表示対象MessageKeyStringList. */
        public List<String> messageKeyStrings;
        
    }
    
    /**
     * 検索結果.
     * @author kazumune
     */
    class Result {
        /** 表示対象List. */
        public List<MessageModelEx> list = new ArrayList<MessageModelEx>();
        
        /** 表示対象MessageKeyStringList. */
        @JSONHint(ignore=true)
        public List<String> messageKeyStrings;
        
        /** 次表示ページが存在するか. */
        public boolean hasNextPage;
    }
    
    /**
     * コメント検索結果.
     * @author kazumune
     */
    class CommentResult {
        /** 表示対象List. */
        public List<CommentModelEx> list = new ArrayList<CommentModelEx>();
        /** 宛先文字列. */
        public String address = "";
    }
}
