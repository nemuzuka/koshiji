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
package jp.co.nemuzuka.koshiji.service.impl;

import org.slim3.datastore.Datastore;

import jp.co.nemuzuka.exception.AlreadyExistKeyException;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.service.GroupService;
import jp.co.nemuzuka.koshiji.service.MemberCreateService;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;
import jp.co.nemuzuka.koshiji.service.MemberService;

import com.google.appengine.api.datastore.Key;

/**
 * MemberCreateServiceの実装クラス.
 * @author kazumune
 */
public class MemberCreateServiceImpl implements MemberCreateService {

    MemberService memberService = MemberServiceImpl.getInstance();
    GroupService groupService = GroupServiceImpl.getInstance();
    MemberGroupConnService memberGroupConnService = MemberGroupConnServiceImpl.getInstance();
    
    private static MemberCreateServiceImpl impl = new MemberCreateServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MemberCreateServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MemberCreateServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberCreateService#createAdminMember(java.lang.String, java.lang.String)
     */
    @Override
    public void createAdminMember(String email, String name) {
        MemberForm form = createMemberForm(email, name);
        Key memberKey = null;
        try {
            memberKey = memberService.put(form);
        } catch(AlreadyExistKeyException e) {
            //既に登録されている場合、処理終了
            return;
        }
        //グループを新規に追加
        Key groupKey = groupService.put(name + "'s Group");
        
        //Memberとグループの関連テーブルを管理者権限で作成
        memberGroupConnService.put(memberKey, groupKey, true);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MemberCreateService#createNormalMember(java.lang.String, java.lang.String)
     */
    @Override
    public void createNormalMember(String email, String groupKeyString) {
        //登録されているメールアドレスか判断する
        Key memberKey = memberService.getKey(email);
        if(memberKey == null) {
            //Memberを登録する
            MemberForm form = createMemberForm(email, email);
            try {
                memberKey = memberService.put(form);
            } catch(AlreadyExistKeyException e) {
                //既に登録されている場合、再度取得
                memberKey = memberService.getKey(email);
            }
        }
        //MemberKeyとGroupKeyで関連を作成
        Key groupKey = Datastore.stringToKey(groupKeyString);
        memberGroupConnService.put(memberKey, groupKey, false);
    }

    /**
     * MemberForm生成.
     * 登録用のMemberFormを生成します。
     * @param email メールアドレス
     * @param nickName ニックネーム
     * @return MemberForm
     */
    private MemberForm createMemberForm(String email, String nickName) {
        MemberForm form = new MemberForm();
        form.setMail(email);
        form.setName(nickName);
        form.setMemo("auto add.");
        return form;
    }
}
