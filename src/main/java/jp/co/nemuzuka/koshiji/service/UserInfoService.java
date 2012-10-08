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

import jp.co.nemuzuka.entity.UserInfo;

/**
 * ユーザ情報に関するService
 * @author kazumune
 */
public interface UserInfoService {

    /**
     * ユーザ情報作成.
     * メールアドレスに紐付くユーザ情報を作成します。
     * @param email メールアドレス
     * @return ユーザ情報
     */
    UserInfo createUserInfo(String email);
    
    /**
     * グループ情報変更
     * 指定したグループを選択状態にしてユーザ情報を更新します。
     * @param memberKey MemberKey
     * @param groupKeyString GroupKey文字列
     * @param targetUserInfo 設定対象ユーザ情報
     */
    void changeGroup(Key memberKey, String groupKeyString, UserInfo targetUserInfo);
}
