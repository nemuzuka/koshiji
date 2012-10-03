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
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.exception.AlreadyExistKeyException;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.form.MemberForm;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MemberServiceImplのテストクラス.
 * @author kazumune
 */
public class MemberServiceImplTest extends AppEngineTestCase4HRD {

	MemberServiceImpl service = MemberServiceImpl.getInstance();
	MemberDao memberDao = MemberDao.getInstance();
	
	List<Key> memberKeyList;
	
	/**
	 * getのテスト.
	 */
	@Test
	public void testGet() {
		createInitData();

		//新規の場合
		MemberForm actual = service.get("");
		assertThat(actual.keyToString, is(nullValue()));
		assertThat(actual.mail, is(nullValue()));
		assertThat(actual.name, is(nullValue()));
        assertThat(actual.memo, is(nullValue()));
        assertThat(actual.timeZone, is(TimeZone.GMT_P_9.getCode()));
		assertThat(actual.versionNo, is(nullValue()));
		
		//更新の場合
		String keyString = Datastore.keyToString(memberKeyList.get(0));
		actual = service.get(keyString);
		assertThat(actual.keyToString, is(keyString));
		assertThat(actual.mail, is("hoge0@gmail.com"));
		assertThat(actual.name, is("name0"));
        assertThat(actual.memo, is("メモですよん"));
        assertThat(actual.timeZone, is(TimeZone.GMT_P_9_30.getCode()));
		assertThat(actual.versionNo, is("1"));
				
		//登録されているデータを削除
		service.delete(actual);
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();

		//存在しない場合
		keyString = Datastore.keyToString(memberKeyList.get(0));
		actual = service.get(keyString);
		assertThat(actual.keyToString, is(nullValue()));
		assertThat(actual.mail, is(nullValue()));
		assertThat(actual.name, is(nullValue()));
        assertThat(actual.memo, is(nullValue()));
		assertThat(actual.versionNo, is(nullValue()));
	}

	/**
     * getKeyのテスト.
     */
    @Test
    public void testGetKey() {
        createInitData();
        
        Key actual = service.getKey("hoge1@gmail.com");
        assertThat(actual, is(memberKeyList.get(1)));
        
        actual = service.getKey("hogehoge@gmail.com");
        assertThat(actual, is(nullValue()));
    }

	
	/**
	 * putとdeleteのテスト.
	 */
	@Test
	public void testPutAndDelete() {
		createInitData();
		
		String keyString = testPut();
		testDelete(keyString);
	}
	
	/**
	 * deleteのテスト.
	 * @param keyString 対象Key文字列
	 */
	private void testDelete(String keyString) {
		
		//バージョン違い
		MemberForm form = service.get(keyString);
		form.setVersionNo("-1");
		try {
			service.delete(form);
			fail();
		} catch (ConcurrentModificationException e) {}
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();
		
		//削除
		form = service.get(keyString);
		service.delete(form);
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();
		
		List<MemberModel> list = service.getList("mail0123@hige.hage");
		assertThat(list.size(), is(0));
	}

	/**
	 * putのテスト.
	 * @return 登録Key文字列
	 */
	private String testPut() {
		
		MemberForm form = new MemberForm();
		form.setMail("mail0123@hige.hage");
		form.setName("name123");
        form.setMemo("メモも");
        form.setTimeZone("");
		service.put(form);
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();

		//登録されていることの確認
		List<MemberModel> list = service.getList("mail0123@hige.hage");
		assertThat(list.size(), is(1));
		assertThat(list.get(0).getMail(), is("mail0123@hige.hage"));
		assertThat(list.get(0).getName(), is("name123"));
		assertThat(list.get(0).getMemo().getValue(), is("メモも"));
        assertThat(list.get(0).getTimeZone(), is(TimeZone.GMT_P_9.getCode()));
		String keyString = Datastore.keyToString(list.get(0).getKey());
		
		//更新
		form = service.get(keyString);
		form.setMail("mail0123_2@hige.hage");
		form.setName("name123456");
        form.setMemo("へぶちん");
        form.setTimeZone(TimeZone.GMT.getCode());
		service.put(form);
		GlobalTransaction.transaction.get().commit();
		GlobalTransaction.transaction.get().begin();

		list = service.getList("mail0123@hige.hage");
		assertThat(list.size(), is(1));
		//メールアドレスは更新されない
		assertThat(list.get(0).getMail(), is("mail0123@hige.hage"));
		assertThat(list.get(0).getName(), is("name123456"));
        assertThat(list.get(0).getMemo().getValue(), is("へぶちん"));
        assertThat(list.get(0).getTimeZone(), is(TimeZone.GMT.getCode()));
		
		//バージョン違い
		form = service.get(keyString);
		form.setVersionNo("-1");
		try {
			service.put(form);
			fail();
		} catch(ConcurrentModificationException e) {}
		
		//既に同じメールアドレスが登録されている
		form = service.get("");
		form.setMail("mail0123@hige.hage");
		form.setName("登録できないデータ！");
        form.setMemo("へぶちん");
		try {
			service.put(form);
			fail();
		} catch(AlreadyExistKeyException e) {}
		
		return keyString;
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
			memberDao.put(model);
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
