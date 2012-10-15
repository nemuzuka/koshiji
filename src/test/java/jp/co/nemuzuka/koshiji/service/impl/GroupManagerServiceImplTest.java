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
import java.util.ConcurrentModificationException;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.service.GroupManagerService;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * GroupManagerServiceImplのテストクラス.
 * @author kazumune
 */
public class GroupManagerServiceImplTest extends AppEngineTestCase4HRD {

    GroupManagerServiceImpl service = GroupManagerServiceImpl.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
    
	/**
	 * getDetailのテスト.
	 */
	@Test
	public void testGetDetail() {
	    createInitData();
	    
	    GroupManagerService.Detail actual = service.getDetail(
	        Datastore.keyToString(groupKeyList.get(0)), Datastore.keyToString(memberKeyList.get(0)));
	    assertThat(actual.groupName, is("グループ0"));
        assertThat(actual.versionNo, is("1"));
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(1));
	    assertThat(actualMemberList.get(0).getValue(), is(Datastore.keyToString(memberKeyList.get(1))));
	}

	
    /**
     * putのテスト.
     * 削除Memberの指定無し
     */
    @Test
    public void testPut() {
        createInitData();
        
        GroupManagerService.PutParam param = new GroupManagerService.PutParam();
        param.deleteMemberKeyStrings = new String[0];
        param.groupKeyString = Datastore.keyToString(groupKeyList.get(0));
        param.groupName = "変更後グループ！";
        param.versionNo = "1";
        
        service.put(param, memberKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();

        GroupManagerService.Detail actual = service.getDetail(
            Datastore.keyToString(groupKeyList.get(0)), Datastore.keyToString(memberKeyList.get(0)));
        assertThat(actual.groupName, is("変更後グループ！"));
        assertThat(actual.versionNo, is("2"));
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(1));
        assertThat(actualMemberList.get(0).getValue(), is(Datastore.keyToString(memberKeyList.get(1))));
    
    }
	
    /**
     * putのテスト.
     * 削除Memberの指定有り
     */
    @Test
    public void testPut2() {
        createInitData();
        
        GroupManagerService.PutParam param = new GroupManagerService.PutParam();
        param.deleteMemberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(0)), Datastore.keyToString(memberKeyList.get(1))};
        param.groupKeyString = Datastore.keyToString(groupKeyList.get(0));
        param.groupName = "変更後グループ！";
        param.versionNo = "1";
        
        service.put(param, memberKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();

        GroupManagerService.Detail actual = service.getDetail(
            Datastore.keyToString(groupKeyList.get(0)), Datastore.keyToString(memberKeyList.get(0)));
        assertThat(actual.groupName, is("変更後グループ！"));
        assertThat(actual.versionNo, is("2"));
        List<LabelValueBean> actualMemberList = actual.memberList;
        assertThat(actualMemberList.size(), is(0));
    }
    
    /**
     * putのテスト.
     * 該当データなし
     */
    @Test
    public void testPut3() {
        createInitData();
        
        GroupManagerService.PutParam param = new GroupManagerService.PutParam();
        param.deleteMemberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(0)), Datastore.keyToString(memberKeyList.get(1))};
        param.groupKeyString = Datastore.keyToString(groupKeyList.get(0));
        param.groupName = "変更後グループ！";
        param.versionNo = "-1";
        
        try {
            service.put(param, memberKeyList.get(0));
            fail();
        } catch(ConcurrentModificationException e) {}
    }
    
	/**
	 * 事前データ作成.
	 * ユーザを4人作成します。
	 * グループを2つ作成します。
	 * ユーザとグループの関連を付与します。
     * グループ0/メンバー0
     * グループ0/メンバー1
     * グループ1/メンバー0
     * グループ1/メンバー2
     * グループ1/メンバー3
	 */
	private void createInitData() {
		memberKeyList = new ArrayList<Key>();
		for(int i = 0; i < 4; i++) {
			MemberModel model = new MemberModel();
			model.setMail("hoge" + i + "@gmail.com");
			model.setName("name" + i);
			model.setMemo(new Text("メモですよん"));
            model.setTimeZone(TimeZone.GMT_P_9.getCode());

			memberDao.put(model);
			memberKeyList.add(model.getKey());
			
			GlobalTransaction.transaction.get().commit();
			GlobalTransaction.transaction.get().begin();
		}
		
        groupKeyList = new ArrayList<Key>();
		for(int i = 0; i < 2; i++) {
	        GroupModel groupModel = new GroupModel();
	        groupModel.setGroupName("グループ" + i);
	        
	        groupDao.put(groupModel);
	        groupKeyList.add(groupModel.getKey());

            GlobalTransaction.transaction.get().commit();
            GlobalTransaction.transaction.get().begin();
		}
		
		//グループ0/メンバー0
		//グループ0/メンバー1
		//グループ1/メンバー0
		//グループ1/メンバー2
		//グループ1/メンバー3
		MemberGroupConnModel memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
		memberGroupConnModel.setMemberKey(memberKeyList.get(0));
		memberGroupConnModel.setSortNum(0L);
		memberGroupConnDao.put(memberGroupConnModel);
		
		memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
        memberGroupConnModel.setMemberKey(memberKeyList.get(1));
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
