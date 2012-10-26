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

import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.form.ScheduleForm;

import com.google.appengine.api.datastore.Key;

/**
 * スケジュール登録・更新に関するService
 * @author kazumune
 */
public interface ScheduleEditService {

    /**
     * スケジュール登録・更新.
     * スケジュールを登録・更新します。
     * 更新処理において、ログインユーザ≠スケジュール登録者の場合、
     * バージョン違いのExceptionをThrowします。
     * @param form 登録・更新Form
     * @param loginMemberKey ログインユーザのMemberKey
     * @return 対象ScheduleKey
     */
    Key put(ScheduleForm form, Key loginMemberKey);
    
    /**
     * スケジュール登録・更新Form取得.
     * 引数の情報を元に、スケジュール登録・更新Formを取得します。
     * スケジュールKey文字列が設定されていない場合、新規登録とみなします。
     * また、更新の場合、ログインMemberが作成者でない場合、例外をthrowします。
     * @param scheduleKeyString スケジュールKey文字列
     * @param memberKeyString 初期選択MemberKey文字列（登録時有効）
     * @param targetDate 初期設定日付（登録時有効）
     * @param userInfo User情報
     * @return スケジュール登録・更新Form（該当データが存在しない場合、null）
     */
    ScheduleForm getForm(String scheduleKeyString, String memberKeyString, 
            String targetDate, UserInfo userInfo);
    
}
