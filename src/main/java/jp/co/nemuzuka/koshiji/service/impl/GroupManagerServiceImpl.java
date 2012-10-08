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

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.service.GroupManagerService;
import jp.co.nemuzuka.koshiji.service.GroupMemberService;

/**
 * GroupManagerServiceの実装クラス.
 * @author kazumune
 */
public class GroupManagerServiceImpl implements GroupManagerService {

    GroupDao groupDao = GroupDao.getInstance();
    GroupMemberService groupMemberService = GroupMemberServiceImpl.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    
    private static GroupManagerServiceImpl impl = new GroupManagerServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static GroupManagerServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private GroupManagerServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.GroupManagerService#getDetail(java.lang.String, java.lang.String)
     */
    @Override
    public Detail getDetail(String groupKeyString, String memberKeyString) {
        Detail detail = new Detail();
        Key groupKey = Datastore.stringToKey(groupKeyString);
        GroupModel groupModel = groupDao.get(groupKey);
        if(groupModel == null) {
            return detail;
        }
        detail.groupName = groupModel.getGroupName();
        detail.versionNo = String.valueOf(groupModel.getVersion());
        
        //指定MemberはListから除外する
        List<LabelValueBean> memberList = groupMemberService.getMemberList(groupKey);
        int size = memberList.size();
        for(int i = 0; i < size; i++) {
            LabelValueBean target = memberList.get(i);
            if(target.getValue().equals(memberKeyString)) {
                memberList.remove(i);
                break;
            }
        }
        detail.memberList = memberList;
        return detail;
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.GroupManagerService#put(jp.co.nemuzuka.koshiji.service.GroupManagerService.PutParam, com.google.appengine.api.datastore.Key)
     */
    @Override
    public void put(PutParam param, Key memberKey) {
        Key groupKey = Datastore.stringToKey(param.groupKeyString);
        
        //Groupの更新
        GroupModel group = groupDao.get(groupKey, Long.valueOf(param.versionNo));
        if(group == null) {
            //該当データが存在しない場合、バージョンエラーとする
            throw new ConcurrentModificationException();
        }
        group.setGroupName(param.groupName);
        groupDao.put(group);
        
        Set<Key> memberKeySet = new HashSet<Key>();
        for(String memberKeyString : param.deleteMemberKeyStrings) {
            memberKeySet.add(Datastore.stringToKey(memberKeyString));
        }
        
        //削除対象のMemberの関連を削除する
        List<MemberGroupConnModel> list = memberGroupConnDao.getMemberList(groupKey);
        for(MemberGroupConnModel target : list) {
            if(target.getMemberKey().equals(memberKey)) {
                continue;
            }
            if(memberKeySet.contains(target.getMemberKey())) {
                memberGroupConnDao.deleteMember(target.getKey(), target.getMemberKey(), target.getGroupKey());
            }
        }
    }
}
