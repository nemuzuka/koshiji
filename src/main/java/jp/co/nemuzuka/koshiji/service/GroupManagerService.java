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

import com.google.appengine.api.datastore.Key;

import jp.co.nemuzuka.entity.LabelValueBean;

/**
 * Group管理に関するService
 * @author kazumune
 */
public interface GroupManagerService {
    
    /**
     * グループ詳細情報取得.
     * @param groupKeyString グループKey文字列
     * @param memberKeyString ログインユーザのMemberKey文字列
     * @return グループ詳細データ
     */
    Detail getDetail(String groupKeyString, String memberKeyString);

    /**
     * グループデータ更新.
     * @param param 更新データ
     * @param memberKey ログインユーザのMemberKey
     */
    void put(PutParam param, Key memberKey);
    
    /**
     * グループ管理データ
     * @author kazumune
     */
    class Detail {
        /** グループ名称. */
        public String groupName;
        /** バージョンNo. */
        public String versionNo;

        /** 
         * グループに紐付くMemberList.
         * ただし、自分は除きます
         */
        public List<LabelValueBean> memberList = new ArrayList<LabelValueBean>();
    }
    
    /**
     * グループ更新データ.
     * @author kazumune
     */
    class PutParam {
        /** グループKey文字列. */
        public String groupKeyString;
        /** グループ名. */
        public String groupName;
        /** グループバージョンNo. */
        public String versionNo;
        /** 削除対象メンバーKey文字配列. */
        public String[] deleteMemberKeyStrings;
    }
}
