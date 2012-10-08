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

import java.util.List;

import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;

import com.google.appengine.api.datastore.Key;

/**
 * MemberGroupConnに関するService
 * @author kazumune
 */
public interface MemberGroupConnService {

    /**
     * MemberGroup関連追加.
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     * @param admin 管理者の場合、true
     * @return 処理対象Key(既に登録されている場合、null)
     */
    Key put(Key memberKey, Key groupKey, boolean admin);
    
    /**
     * 一覧取得.
     * 指定したMemberKeyに紐付く一覧を取得します。
     * @param key memberKey
     * @return 該当レコード
     */
    List<MemberGroupConnModel> getList(Key memberKey);
    
    /**
     * 一覧取得.
     * 指定したMemberKeyに紐付くグループの一覧を取得します。
     * @param memberKey MemberKey
     * @return 該当データ
     */
    List<LabelValueBean> getGroupList(Key memberKey);
    
    /**
     * 一覧取得.
     * 指定したグループに紐付く一覧を取得します。
     * @param Key groupKey
     * @return 該当レコード
     */
    List<MemberGroupConnModel> getMemberList(Key groupKey);
    
    /**
     * MemberGroup関連削除.
     * 引数のMemberが、対象グループの管理者の場合、指定グループに対する全ての関連を削除します。
     * 管理者でない場合、Memberとグループの関連を削除します。
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     */
    void deleteMemberGroupConn(Key memberKey, Key groupKey);
    
}
