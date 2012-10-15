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
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MemberGroupConnServiceImplのテストクラス.
 * @author kazumune
 */
public class MemberGroupConnServiceImplTest extends AppEngineTestCase4HRD {

    MemberGroupConnServiceImpl service = MemberGroupConnServiceImpl.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
    
	/**
	 * deleteMemberGroupConnのテスト
	 * メンバーが脱退
	 */
	@Test
	public void testDeleteMemberGroupConn() {
	    createInitData();
	    service.deleteMemberGroupConn(memberKeyList.get(1), groupKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<MemberGroupConnModel> actualList = service.getMemberList(groupKeyList.get(0));
        assertThat(actualList.size(), is(1));
        MemberGroupConnModel actual = actualList.get(0);
        assertThat(actual.getMemberKey(), is(memberKeyList.get(0)));
	}

    /**
     * deleteMemberGroupConnのテスト
     * 管理者が脱退
     */
    @Test
    public void testDeleteMemberGroupConn2() {
        createInitData();
        service.deleteMemberGroupConn(memberKeyList.get(0), groupKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<MemberGroupConnModel> actualList = service.getMemberList(groupKeyList.get(0));
        assertThat(actualList.size(), is(0));
    }
	
	/**
	 * 事前データ作成.
	 * ユーザを4人作成します。
	 * グループを2つ作成します。
	 * ユーザとグループの関連を付与します。
     * グループ0/メンバー0(管理者)
     * グループ0/メンバー1
     * グループ1/メンバー0
     * グループ1/メンバー2(管理者)
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
		memberGroupConnModel.setAdmin(true);
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
        memberGroupConnModel.setAdmin(true);
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
