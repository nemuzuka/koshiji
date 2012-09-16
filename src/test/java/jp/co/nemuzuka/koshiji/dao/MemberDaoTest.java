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
package jp.co.nemuzuka.koshiji.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MemberDaoのテストクラス.
 * @author kazumune
 */
public class MemberDaoTest extends AppEngineTestCase4HRD {

	MemberDao dao = MemberDao.getInstance();
	
	List<Key> memberKeyList;
	
	/**
	 * getListのテスト.
	 */
	@Test
	public void testGetList() {
		createInitData();

		List<MemberModel> actual = dao.getList(memberKeyList.get(1), memberKeyList.get(0));
		assertThat(actual.size(), is(2));
		assertThat(actual.get(0).getKey(), is(memberKeyList.get(0)));
        assertThat(actual.get(1).getKey(), is(memberKeyList.get(1)));
				
		//登録されているデータを削除
		dao.delete(memberKeyList.get(0));
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();

		//存在しない場合
		actual = dao.getList(memberKeyList.get(1), memberKeyList.get(0));
        assertThat(actual.size(), is(1));
        assertThat(actual.get(0).getKey(), is(memberKeyList.get(1)));
        
        //nullを渡した場合
        actual = dao.getList((Key[])null);
        assertThat(actual.size(), is(0));

        //size0のKeyを渡した場合
        actual = dao.getList(new Key[0]);
        assertThat(actual.size(), is(0));
	}
	
	/**
	 * 事前データ作成.
	 * ユーザを4人作成します。
	 */
	private void createInitData() {
		memberKeyList = new ArrayList<Key>();
		for(int i = 0; i < 4; i++) {
			MemberModel model = new MemberModel();
			model.setMail("hoge" + i + "@gmail.com");
			model.setName("name" + i);
			if(i == 0) {
				model.setMemo(new Text("メモですよん"));
			} else if(i == 1) {
                model.setMemo(new Text("メモですかね？"));
			}
			if(i == 0) {
	            model.setTimeZone(TimeZone.GMT_P_9_30.getCode());
			} else {
                model.setTimeZone(TimeZone.GMT_P_9.getCode());
			}
			dao.put(model);
			memberKeyList.add(model.getKey());
			
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
