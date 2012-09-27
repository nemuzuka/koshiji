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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.service.GroupMemberService;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;
import jp.co.nemuzuka.koshiji.service.MemberService;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * GroupMemberServiceの実装クラス.
 * @author kazumune
 */
public class GroupMemberServiceImpl implements GroupMemberService {

    MemberGroupConnService memberGroupConnService = MemberGroupConnServiceImpl.getInstance();
    MemberService memberService = MemberServiceImpl.getInstance();
    
    private static GroupMemberServiceImpl impl = new GroupMemberServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static GroupMemberServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private GroupMemberServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.GroupMemberService#getMemberList(com.google.appengine.api.datastore.Key)
     */
    @Override
    public List<LabelValueBean> getMemberList(Key groupKey) {
        
        //グループに紐付くMember一覧を取得
        List<MemberGroupConnModel> memberGroupConnList = memberGroupConnService.getMemberList(groupKey);
        Set<Key> memberKeySet = new HashSet<Key>();
        for(MemberGroupConnModel target : memberGroupConnList) {
            memberKeySet.add(target.getMemberKey());
        }
        List<MemberModel> memberList = memberService.getList(memberKeySet.toArray(new Key[0]));
        List<LabelValueBean> retList = new ArrayList<LabelValueBean>();
        for(MemberModel target : memberList) {
            retList.add(
                new LabelValueBean(target.getName(), Datastore.keyToString(target.getKey())));
        }
        return retList;
    }
}
