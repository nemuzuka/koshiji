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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * UserInfoServiceImplのテストクラス.
 * @author kazumune
 */
public class UserInfoServiceImplTest extends AppEngineTestCase4HRD {

    UserInfoServiceImpl service = UserInfoServiceImpl.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
	
	
	/**
	 * createUserInfoのテスト.
	 * デフォルト表示グループが未設定なので、先頭のプロジェクトが選択状態
	 */
	@Test
	public void testCreateUserInfo() {
	    createInitData();
	    
	    UserInfo actual = service.createUserInfo("hoge0@gmail.com");
	    assertThat(actual.keyToString, is(Datastore.keyToString(memberKeyList.get(0))));
        assertThat(actual.initGroupKeyString, is(""));
	    assertThat(actual.selectedGroupKeyString, is(Datastore.keyToString(groupKeyList.get(0))));
        assertThat(actual.groupManager, is(true));
        
        //指定グループに紐付くMember
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(3));
        assertThat(actualMemberList.get(0).getValue(), is(Datastore.keyToString(memberKeyList.get(0))));
        assertThat(actualMemberList.get(1).getValue(), is(Datastore.keyToString(memberKeyList.get(1))));
        assertThat(actualMemberList.get(2).getValue(), is(Datastore.keyToString(memberKeyList.get(3))));
        
        //メンバー0が参照できるグループ一覧
        List<LabelValueBean> actualGroupList = actual.groupList;
        assertThat(actualGroupList.size(), is(2));
        assertThat(actualGroupList.get(0).getValue(), is(Datastore.keyToString(groupKeyList.get(0))));
        assertThat(actualGroupList.get(1).getValue(), is(Datastore.keyToString(groupKeyList.get(1))));
	}

    /**
     * createUserInfoのテスト.
     * デフォルト表示グループが設定済なので、指定プロジェクトが選択状態
     */
    @Test
    public void testCreateUserInfo2() {
        createInitData();
        
        UserInfo actual = service.createUserInfo("hoge3@gmail.com");
        assertThat(actual.keyToString, is(Datastore.keyToString(memberKeyList.get(3))));
        assertThat(actual.initGroupKeyString, is(Datastore.keyToString(groupKeyList.get(1))));
        assertThat(actual.selectedGroupKeyString, is(Datastore.keyToString(groupKeyList.get(1))));
        assertThat(actual.groupManager, is(false));
        
        //指定グループに紐付くMember
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(3));
        assertThat(actualMemberList.get(0).getValue(), is(Datastore.keyToString(memberKeyList.get(2))));
        assertThat(actualMemberList.get(1).getValue(), is(Datastore.keyToString(memberKeyList.get(0))));
        assertThat(actualMemberList.get(2).getValue(), is(Datastore.keyToString(memberKeyList.get(3))));
        
        //メンバー3が参照できるグループ一覧
        List<LabelValueBean> actualGroupList = actual.groupList;
        assertThat(actualGroupList.size(), is(2));
        assertThat(actualGroupList.get(0).getValue(), is(Datastore.keyToString(groupKeyList.get(0))));
        assertThat(actualGroupList.get(1).getValue(), is(Datastore.keyToString(groupKeyList.get(1))));
    }

    /**
     * createUserInfoのテスト.
     * プロジェクトの変更
     */
    @Test
    public void testCreateUserInfo3() {
        createInitData();
        
        UserInfo actual = service.createUserInfo("hoge0@gmail.com");
        service.changeGroup(memberKeyList.get(0), 
            Datastore.keyToString(groupKeyList.get(1)), actual);
        
        //指定グループに紐付くMember
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(3));
        assertThat(actualMemberList.get(0).getValue(), is(Datastore.keyToString(memberKeyList.get(2))));
        assertThat(actualMemberList.get(1).getValue(), is(Datastore.keyToString(memberKeyList.get(0))));
        assertThat(actualMemberList.get(2).getValue(), is(Datastore.keyToString(memberKeyList.get(3))));
        
        //メンバー0が参照できるグループ一覧
        List<LabelValueBean> actualGroupList = actual.groupList;
        assertThat(actualGroupList.size(), is(2));
        assertThat(actualGroupList.get(0).getValue(), is(Datastore.keyToString(groupKeyList.get(0))));
        assertThat(actualGroupList.get(1).getValue(), is(Datastore.keyToString(groupKeyList.get(1))));
    }
    
    
	/**
	 * 事前データ作成.
	 * ユーザを4人作成します。
	 * グループを2つ作成します。
	 * ユーザとグループの関連を付与します。
     * グループ0/メンバー0(管理者)
     * グループ0/メンバー1
     * グループ0/メンバー3
     * グループ1/メンバー0
     * グループ1/メンバー2(管理者)
     * グループ1/メンバー3(デフォルト表示)
	 */
	private void createInitData() {

        groupKeyList = new ArrayList<Key>();
        for(int i = 0; i < 2; i++) {
            GroupModel groupModel = new GroupModel();
            groupModel.setGroupName("グループ" + i);
            
            groupDao.put(groupModel);
            groupKeyList.add(groupModel.getKey());

            GlobalTransaction.transaction.get().commit();
            GlobalTransaction.transaction.get().begin();
        }
	    
	    memberKeyList = new ArrayList<Key>();
		for(int i = 0; i < 4; i++) {
			MemberModel model = new MemberModel();
			model.setMail("hoge" + i + "@gmail.com");
			model.setName("name" + i);
			model.setMemo(new Text("メモですよん"));
            model.setTimeZone(TimeZone.GMT_P_9.getCode());
            if(i == 3) {
                model.setDefaultGroup(Datastore.keyToString(groupKeyList.get(1)));
            }

			memberDao.put(model);
			memberKeyList.add(model.getKey());
			
			GlobalTransaction.transaction.get().commit();
			GlobalTransaction.transaction.get().begin();
		}
				
		//グループ0/メンバー0(管理者)
		//グループ0/メンバー1
        //グループ0/メンバー3
		//グループ1/メンバー0
		//グループ1/メンバー2(管理者)
		//グループ1/メンバー3
		MemberGroupConnModel memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
		memberGroupConnModel.setMemberKey(memberKeyList.get(0));
		memberGroupConnModel.setSortNum(0L);
		memberGroupConnModel.setAdmin(true);
		memberGroupConnDao.put(memberGroupConnModel);
		
		memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
        memberGroupConnModel.setMemberKey(memberKeyList.get(1));
        memberGroupConnModel.setSortNum(1L);
        memberGroupConnDao.put(memberGroupConnModel);		

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
        memberGroupConnModel.setMemberKey(memberKeyList.get(3));
        memberGroupConnModel.setSortNum(1L);
        memberGroupConnDao.put(memberGroupConnModel);       

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(0));
        memberGroupConnModel.setSortNum(2L);
        memberGroupConnDao.put(memberGroupConnModel);       

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(2));
        memberGroupConnModel.setAdmin(true);
        memberGroupConnModel.setSortNum(3L);
        memberGroupConnDao.put(memberGroupConnModel);       

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(3));
        memberGroupConnModel.setSortNum(4L);
        memberGroupConnDao.put(memberGroupConnModel);       
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
	}

	/* (非 Javadoc)
	 * @see org.slim3.tester.AppEngineTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		TransactionEntity transactionEntity = new TransactionEntity();
		GlobalTransaction.transaction.set(transactionEntity);
	}
}
