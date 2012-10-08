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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.service.GroupService;
import jp.co.nemuzuka.koshiji.service.MemberGroupConnService;
import jp.co.nemuzuka.koshiji.service.MemberService;
import jp.co.nemuzuka.koshiji.service.UserInfoService;

/**
 * UserInfoServiceの実装クラス.
 * @author kazumune
 */
public class UserInfoServiceImpl implements UserInfoService {

    MemberService memberService = MemberServiceImpl.getInstance();
    MemberGroupConnService memberGroupConnService = MemberGroupConnServiceImpl.getInstance();
    GroupService groupService = GroupServiceImpl.getInstance();
    
    private static UserInfoServiceImpl impl = new UserInfoServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static UserInfoServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private UserInfoServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.UserInfoService#createUserInfo(java.lang.String)
     */
    @Override
    public UserInfo createUserInfo(String email) {
        
        UserInfo userInfo = new UserInfo();
        Key memberKey = memberService.getKey(email);
        if(memberKey == null) {
            return userInfo;
        }
        userInfo.keyToString = Datastore.keyToString(memberKey);
        MemberForm memberForm = memberService.get(userInfo.keyToString);
        userInfo.timeZone = TimeZone.fromCode(memberForm.timeZone);
        
        setGroupInfo(memberKey, userInfo, null);
        setGroupMemberInfo(userInfo);

        return userInfo;
    }
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.UserInfoService#changeGroup(com.google.appengine.api.datastore.Key, java.lang.String, jp.co.nemuzuka.entity.UserInfo)
     */
    @Override
    public void changeGroup(Key memberKey, String groupKeyString, UserInfo targetUserInfo) {
        targetUserInfo.clear();
        setGroupInfo(memberKey, targetUserInfo, groupKeyString);
        setGroupMemberInfo(targetUserInfo);
    }
    
    /**
     * グループに紐付くメンバー設定.
     * 表示対象に設定されたグループに紐付くメンバー情報を設定します。
     * @param userInfo 設定対象UserInfo
     */
    private void setGroupMemberInfo(UserInfo userInfo) {
        if(StringUtils.isEmpty(userInfo.selectedGroupKeyString)) {
            return;
        }
        
        Key groupKey = Datastore.stringToKey(userInfo.selectedGroupKeyString);
        List<MemberGroupConnModel> list = memberGroupConnService.getMemberList(groupKey);
        Set<Key> memberKeySet = new HashSet<Key>();
        for(MemberGroupConnModel target: list) {
            memberKeySet.add(target.getMemberKey());
        }
        Map<Key, MemberModel> memberMap = memberService.getMap(memberKeySet.toArray(new Key[0]));
        for(MemberGroupConnModel target: list) {
            MemberModel member = memberMap.get(target.getMemberKey());
            if(member == null) {
                continue;
            }
            userInfo.memberList.add(
                new LabelValueBean(member.getName(), member.getKeyToString()));
        }
    }
    
    /**
     * グループ情報設定.
     * ログインユーザに関連づいているグループ情報を設定します。
     * 初期設定グループの設定ルールは、
     * １．引数の初期設定グループKey文字列
     * ２．UserInfoの初期グループ
     * ３．グループの先頭
     * の優先度で設定します。
     * @param memberKey 対象MemberKey
     * @param userInfo 設定対象UserInfo
     * @param selectedGroupKeyString 初期設定グループKey文字列
     */
    private void setGroupInfo(Key memberKey, UserInfo userInfo, String selectedGroupKeyString) {
        //Memberに紐付くグループを取得
        List<MemberGroupConnModel> memberGroupConnList = 
                memberGroupConnService.getList(memberKey);
        Set<Key> grupKeySet = new HashSet<Key>();
        for(MemberGroupConnModel target : memberGroupConnList) {
            grupKeySet.add(target.getGroupKey());
        }
        
        //初期設定グループの判断
        String targetGroupKeyString = "";
        if(StringUtils.isNotEmpty(selectedGroupKeyString)) {
            targetGroupKeyString = selectedGroupKeyString;
        } else if(StringUtils.isNotEmpty(userInfo.initGroupKeyString)) {
            targetGroupKeyString = userInfo.initGroupKeyString;
        }
        
        Map<Key, GroupModel> groupMap = groupService.getMap(grupKeySet.toArray(new Key[0]));
        String firstGroupKeyString = "";
        boolean firstGroupAdmin = false;
        for(MemberGroupConnModel target : memberGroupConnList) {
            GroupModel groupModel = groupMap.get(target.getGroupKey());
            if(groupModel == null) {
                continue;
            }
            
            String groupKeyString = groupModel.getKeyToString();
            userInfo.groupList.add(
                new LabelValueBean(groupModel.getGroupName(), groupKeyString));

            if(StringUtils.isEmpty(firstGroupKeyString)) {
                firstGroupKeyString = groupKeyString;
                firstGroupAdmin = target.isAdmin();
            }
            
            if(StringUtils.isNotEmpty(targetGroupKeyString)) {
                //初期設定グループが設定されており、指定グループであれば初期選択値とする
                if(groupKeyString.equals(targetGroupKeyString)) {
                    userInfo.selectedGroupKeyString = groupKeyString;
                    userInfo.groupManager = target.isAdmin();
                }
            }
        }
        if(StringUtils.isEmpty(userInfo.selectedGroupKeyString)) {
            //未設定の場合、先頭データを設定
            userInfo.selectedGroupKeyString = firstGroupKeyString;
            userInfo.groupManager = firstGroupAdmin;
        }
    }
}
