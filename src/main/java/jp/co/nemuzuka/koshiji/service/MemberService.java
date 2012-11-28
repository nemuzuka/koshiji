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

import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Map;

import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.model.MemberModel;

import com.google.appengine.api.datastore.Key;

/**
 * Memberに関するService.
 * @author kazumune
 */
public interface MemberService {

    /**
     * 詳細情報取得.
     * @param  keyString キー文字列
     * @return 該当レコードがあれば更新用Form。該当レコードがなければ新規用Form
     */
    MemberForm get(String keyString);
    
    /**
     * put処理.
     * Form情報を元に、永続化します。
     * @param form put対象Form
     * @return 処理対象Key
     * @throws ConcurrentModificationException 更新レコードが存在しない場合
     * @throws AlreadyExistKeyException 既に登録されているメールアドレスを登録しようとした場合
     */
    Key put(MemberForm form);
    
    /**
     * delete処理.
     * keyとバージョンNoが合致するデータを削除します。
     * @param form delete対象Form
     * @throws ConcurrentModificationException 削除レコードが存在しない場合
     */
    void delete(MemberForm form);

    /**
     * 該当レコード取得.
     * 一覧を取得します。
     * @param mail 検索条件：メールアドレス
     * @return 該当レコード
     */
    List<MemberModel> getList(String mail);
    
    /**
     * Map取得.
     * 指定したKey配列に合致するデータを取得します。
     * Keyが未指定の場合、size0のListを返却します
     * @param keys key配列
     * @return 該当Map
     */
    Map<Key, MemberModel> getMap(Key...keys);

    /**
     * List取得.
     * 指定したKey配列に合致するデータを取得します。
     * Keyが未指定の場合、size0のListを返却します
     * @param keys key配列
     * @return 該当List
     */
    List<MemberModel> getList(Key...keys);

    /**
	 * Key取得.
	 * 指定したメールアドレスに合致するMemberModelのKeyを取得します。
	 * @param mail メールアドレス
	 * @return 該当MemberKey(存在しない場合、null)
	 */
	Key getKey(String mail);
	
	/**
	 * 未参加MemberList取得.
	 * 対象Memberが参加しているGroupのうち、
	 * 対象Groupにまだ参加していないMember情報を取得します。
	 * @param memberKey 対象MemberKey
	 * @param groupKey 対象GroupKey
	 * @return 未参加MemberList
	 */
	List<LabelValueBean> getList4Acquaintance(Key memberKey, Key groupKey);
}
