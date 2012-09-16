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
import jp.co.nemuzuka.common.UniqueKey;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Text;

/**
 * MemberCreateServiceImplのテストクラス.
 * @author kazumune
 */
public class MemberCreateServiceImplTest extends AppEngineTestCase4HRD {

	MemberCreateServiceImpl service = MemberCreateServiceImpl.getInstance();
	MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
	MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
	
	private List<MemberModel> createMemberList;
	private List<GroupModel> createGroupList;
	private List<MemberGroupConnModel> createMemberGroupConnList;
	
	/**
	 * createAdminMemberのテスト.
	 */
	@Test
	public void testCreateAdminMember() {
	    service.createAdminMember("hoge@hige.hage", "ふがが");
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<MemberModel> memberList = memberDao.getAllList();
        assertThat(memberList.size(), is(1));
        MemberModel actualMember = memberList.get(0);
        assertThat(actualMember.getMail(), is("hoge@hige.hage"));
        assertThat(actualMember.getName(), is("ふがが"));
        assertThat(actualMember.getMemo().getValue(), is("auto add."));
        assertThat(actualMember.getTimeZone(), is(TimeZone.GMT_P_9.getCode()));
        
        List<GroupModel> groupList = groupDao.getAllList();
        assertThat(groupList.size(), is(1));
        GroupModel actualGroup = groupList.get(0);
        assertThat(actualGroup.getGroupName(), is("ふがが's Group"));
        
        List<MemberGroupConnModel> memberGroupConnList = memberGroupConnDao.getAllList();
        assertThat(memberGroupConnList.size(), is(1));
        MemberGroupConnModel actualMemberGroupConn = memberGroupConnList.get(0);
        assertThat(actualMemberGroupConn.getMemberKey(), is(actualMember.getKey()));
        assertThat(actualMemberGroupConn.getGroupKey(), is(actualGroup.getKey()));
        
        //既に登録されているMemberの場合
        service.createAdminMember("hoge@hige.hage", "ふがが");
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        //レコードが増えていないこと
        memberList = memberDao.getAllList();
        assertThat(memberList.size(), is(1));
        groupList = groupDao.getAllList();
        assertThat(groupList.size(), is(1));
        memberGroupConnList = memberGroupConnDao.getAllList();
        assertThat(memberGroupConnList.size(), is(1));
	}
	
	/**
	 * createNormalMemberのテスト.
	 * 新しいユーザを追加
     * Group数変更なし、Member,MemberGroupConn1件追加
	 */
	@Test
	public void testCreateNormalMember() {
	    createInitData();
	    
	    //新しくユーザを作成
	    service.createNormalMember("hogehoge@gmail.com", 
	        Datastore.keyToString(createGroupList.get(0).getKey()));

	    GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
	    
        List<MemberModel> memberList = memberDao.getAllList();
        assertThat(memberList.size(), is(4));
        MemberModel actualMember = memberList.get(3);
        assertThat(actualMember.getMail(), is("hogehoge@gmail.com"));
        assertThat(actualMember.getName(), is("hogehoge@gmail.com"));
        assertThat(actualMember.getMemo().getValue(), is("auto add."));
        assertThat(actualMember.getTimeZone(), is(TimeZone.GMT_P_9.getCode()));
        
        List<GroupModel> groupList = groupDao.getAllList();
        assertThat(groupList.size(), is(3));
        
        List<MemberGroupConnModel> memberGroupConnList = memberGroupConnDao.getAllList();
        assertThat(memberGroupConnList.size(), is(4));
        MemberGroupConnModel actualMemberGroupConn = memberGroupConnList.get(3);
        assertThat(actualMemberGroupConn.getMemberKey(), is(actualMember.getKey()));
        assertThat(actualMemberGroupConn.getGroupKey(), is(createGroupList.get(0).getKey()));
        assertThat(actualMemberGroupConn.isAdmin(), is(false));
	    
	}

	/**
     * createNormalMemberのテスト.
     * 既に存在するメールアドレスに未参加のグループへの関連付け
     * Member,Group数変更なし、MemberGroupConn1件追加
     */
    @Test
    public void testCreateNormalMember2() {
        createInitData();
        
        service.createNormalMember("hoge2@gmail.com", 
            Datastore.keyToString(createGroupList.get(0).getKey()));

        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<MemberModel> memberList = memberDao.getAllList();
        assertThat(memberList.size(), is(3));
        MemberModel actualMember = memberList.get(2);
        
        List<GroupModel> groupList = groupDao.getAllList();
        assertThat(groupList.size(), is(3));
        
        List<MemberGroupConnModel> memberGroupConnList = memberGroupConnDao.getAllList();
        assertThat(memberGroupConnList.size(), is(4));
        MemberGroupConnModel actualMemberGroupConn = memberGroupConnList.get(3);
        assertThat(actualMemberGroupConn.getMemberKey(), is(actualMember.getKey()));
        assertThat(actualMemberGroupConn.getGroupKey(), is(createGroupList.get(0).getKey()));
        assertThat(actualMemberGroupConn.isAdmin(), is(false));
        
    }

    /**
     * createNormalMemberのテスト.
     * 既に存在するメールアドレスに参加済のグループへの関連付け
     * Member,Group,MemberGroupConn数変更なし
     */
    @Test
    public void testCreateNormalMember3() {
        createInitData();
        
        service.createNormalMember("hoge0@gmail.com", 
            Datastore.keyToString(createGroupList.get(0).getKey()));

        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<MemberModel> memberList = memberDao.getAllList();
        assertThat(memberList.size(), is(3));
        
        List<GroupModel> groupList = groupDao.getAllList();
        assertThat(groupList.size(), is(3));
        
        List<MemberGroupConnModel> memberGroupConnList = memberGroupConnDao.getAllList();
        assertThat(memberGroupConnList.size(), is(3));
    }
    
	/**
	 * 初期データ作成.
	 */
	private void createInitData() {
	    createMemberList = new ArrayList<MemberModel>();
	    createGroupList = new ArrayList<GroupModel>();
	    createMemberGroupConnList = new ArrayList<MemberGroupConnModel>();
	    
	    for(int i = 0; i < 3; i++) {
	        MemberModel memberModel = new MemberModel();
	        String mail = "hoge" + i + "@gmail.com";
	        memberModel.setMail(mail);
	        memberModel.setName("Name" + i);
	        memberModel.setMemo(new Text("Memo" + i));
	        memberDao.put(memberModel);
	        Datastore.putUniqueValue(UniqueKey.member.name(), mail);
	        createMemberList.add(memberModel);
	        
	        GroupModel groupModel = new GroupModel();
	        groupModel.setGroupName("Group" + i);
	        groupDao.put(groupModel);
	        createGroupList.add(groupModel);
	        
	        MemberGroupConnModel memberGroupConnModel = new MemberGroupConnModel();
	        memberGroupConnModel.setMemberKey(memberModel.getKey());
	        memberGroupConnModel.setGroupKey(groupModel.getKey());
	        memberGroupConnModel.setAdmin(true);
	        memberGroupConnModel.setSortNum(Long.MAX_VALUE);
	        memberGroupConnDao.put(memberGroupConnModel);
	        createMemberGroupConnList.add(memberGroupConnModel);

	        String memberKeyString = Datastore.keyToString(memberModel.getKey());
	        String groupKeyString = Datastore.keyToString(groupModel.getKey());
	        String uniqueKey = memberKeyString + ":" + groupKeyString;
	        Datastore.putUniqueValue(UniqueKey.memberGroupConn.name(), uniqueKey);
	        
	        GlobalTransaction.transaction.get().commit();
	        GlobalTransaction.transaction.get().begin();
	    }
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
