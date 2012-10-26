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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ConcurrentModificationException;
import java.util.List;

import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.form.ScheduleForm;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * ScheduleEditServiceImplのテストクラス.
 * @author kazumune
 */
public class ScheduleEditServiceImplTest extends AppEngineTestCase4HRD {

    ScheduleEditServiceImpl service = ScheduleEditServiceImpl.getInstance();
    ScheduleDao scheduleDao = ScheduleDao.getInstance();
	MemberDao memberDao = MemberDao.getInstance();
    
	/**
	 * putのテスト.
	 * 新規登録
	 * @throws ParseException 例外
	 */
	@Test
	public void testPut() throws ParseException {
	    createInitData();
	    
	    ScheduleForm form = new ScheduleForm();
	    form.setTitle("新規登録だぜぇ");
	    form.setMemo("Memoだぜぇ");
	    form.setStartDate("20121110");
	    form.setStartTime("1000");
	    form.setEndDate("20121111");
	    form.setEndTime("2359");
	    form.setClosedFlg("0");
	    form.setConnMemberKeyString(new String[]{"1","2"});
	    
	    Key actualKey = service.put(form, Datastore.createKey(MemberModel.class, 1));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        ScheduleModel actual = scheduleDao.get(actualKey);
        assertThat(actual.getTitle(), is("新規登録だぜぇ"));
        assertThat(actual.getMemo().getValue(), is("Memoだぜぇ"));
        assertThat(actual.getStartDate(), is(sdf.parse("20121110")));
        assertThat(actual.getStartTime(), is("1000"));
        assertThat(actual.getEndDate(), is(sdf.parse("20121111")));
        assertThat(actual.getEndTime(), is("2359"));
        assertThat(actual.isClosed(), is(false));
        assertThat(actual.getConnMember().size(), is(2));
        assertThat(actual.getCreateMemberKey(), is(Datastore.createKey(MemberModel.class, 1)));
	}
	
    /**
     * putのテスト.
     * 新規登録(非公開)
     * @throws ParseException 例外
     */
    @Test
    public void testPut2() throws ParseException {
        createInitData();
        
        ScheduleForm form = new ScheduleForm();
        form.setTitle("新規登録だぜぇ");
        form.setMemo("Memoだぜぇ");
        form.setStartDate("20121110");
        form.setStartTime("1000");
        form.setEndDate("20121111");
        form.setEndTime("2359");
        form.setClosedFlg("1");
        form.setConnMemberKeyString(new String[]{"1","2"});
        
        Key actualKey = service.put(form, Datastore.createKey(MemberModel.class, 1));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        ScheduleModel actual = scheduleDao.get(actualKey);
        assertThat(actual.getTitle(), is("新規登録だぜぇ"));
        assertThat(actual.getMemo().getValue(), is("Memoだぜぇ"));
        assertThat(actual.getStartDate(), is(sdf.parse("20121110")));
        assertThat(actual.getStartTime(), is("1000"));
        assertThat(actual.getEndDate(), is(sdf.parse("20121111")));
        assertThat(actual.getEndTime(), is("2359"));
        assertThat(actual.isClosed(), is(true));
        assertThat(actual.getConnMember().size(), is(2));
        assertThat(actual.getCreateMemberKey(), is(Datastore.createKey(MemberModel.class, 1)));
    }
    
    /**
     * putのテスト.
     * 更新
     * @throws ParseException 例外
     */
    @Test
    public void testPut3() throws ParseException {
        createInitData();
        Key createKey = createInitSchedule();
        
        //データ更新
        ScheduleForm form = new ScheduleForm();
        form.setKeyString(Datastore.keyToString(createKey));
        form.setVersionNo("1");
        form.setTitle("更新だぜぇ");
        form.setMemo("更新Memoだぜぇ");
        form.setStartDate("20121210");
        form.setStartTime("1100");
        form.setEndDate("20121211");
        form.setEndTime("2200");
        form.setClosedFlg("0");
        form.setConnMemberKeyString(new String[]{"3"});
        
        Key actualKey = service.put(form, Datastore.createKey(MemberModel.class, 1));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        ScheduleModel actual = scheduleDao.get(actualKey);
        assertThat(actual.getTitle(), is("更新だぜぇ"));
        assertThat(actual.getMemo().getValue(), is("更新Memoだぜぇ"));
        assertThat(actual.getStartDate(), is(sdf.parse("20121210")));
        assertThat(actual.getStartTime(), is("1100"));
        assertThat(actual.getEndDate(), is(sdf.parse("20121211")));
        assertThat(actual.getEndTime(), is("2200"));
        assertThat(actual.isClosed(), is(false));
        assertThat(actual.getConnMember().size(), is(1));
        
    }
    
    /**
     * putのテスト.
     * 更新(バージョンエラー)
     */
    @Test
    public void testPut4() {
        createInitData();
        Key createKey = createInitSchedule();
        
        //データ更新
        ScheduleForm form = new ScheduleForm();
        form.setKeyString(Datastore.keyToString(createKey));
        form.setVersionNo("-1");
        form.setTitle("更新だぜぇ");
        form.setMemo("更新Memoだぜぇ");
        form.setStartDate("20121210");
        form.setStartTime("1100");
        form.setEndDate("20121211");
        form.setEndTime("2200");
        form.setClosedFlg("0");
        form.setConnMemberKeyString(new String[]{"3"});
        
        try {
            service.put(form, Datastore.createKey(MemberModel.class, 1));
            fail();
        } catch(ConcurrentModificationException e) {}
    }
    
    /**
     * putのテスト.
     * 更新(更新ユーザがスケジュール作成者でない)
     */
    @Test
    public void testPut5() {
        createInitData();
        Key createKey = createInitSchedule();
        
        //データ更新
        ScheduleForm form = new ScheduleForm();
        form.setKeyString(Datastore.keyToString(createKey));
        form.setVersionNo("1");
        form.setTitle("更新だぜぇ");
        form.setMemo("更新Memoだぜぇ");
        form.setStartDate("20121210");
        form.setStartTime("1100");
        form.setEndDate("20121211");
        form.setEndTime("2200");
        form.setClosedFlg("0");
        form.setConnMemberKeyString(new String[]{"3"});
        
        try {
            service.put(form, Datastore.createKey(MemberModel.class, 2));
            fail();
        } catch(ConcurrentModificationException e) {}
    }
    
    /**
     * getFormのテスト.
     * 新規登録用Form取得.
     */
    @Test
    public void testGetForm() {
        createInitData();
        
        UserInfo userInfo = new UserInfo();
        //データ更新
        ScheduleForm actual = service.getForm("", 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 2)), 
            "20120106", userInfo);
        assertThat(actual.getKeyString(), is(nullValue()));
        assertThat(actual.getTitle(), is(nullValue()));
        assertThat(actual.getMemo(), is(nullValue()));
        assertThat(actual.getStartDate(), is("20120106"));
        assertThat(actual.getStartTime(), is(nullValue()));
        assertThat(actual.getEndDate(), is("20120106"));
        assertThat(actual.getEndTime(), is(nullValue()));
        assertThat(actual.getClosedFlg(), is("0"));
        assertThat(actual.getVersionNo(), is(nullValue()));
        List<LabelValueBean> actualSelectedMemberList = actual.getSelectedMemberList();
        assertThat(actualSelectedMemberList.size(), is(1));
        assertThat(actualSelectedMemberList.get(0).getValue(), 
            is(Datastore.keyToString(Datastore.createKey(MemberModel.class, 2))));
        assertThat(actual.getGroupList(), sameInstance(userInfo.getGroupList()));
        assertThat(actual.getMemberList(), sameInstance(userInfo.getMemberList()));
    }
    
    /**
     * getFormのテスト.
     * 更新用Form取得.
     */
    @Test
    public void testGetForm2() {
        createInitData();
        Key createKey = createInitSchedule();
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(Datastore.createKey(MemberModel.class, 1));
        
        //データ更新
        ScheduleForm actual = service.getForm(Datastore.keyToString(createKey), 
            "",
            "", userInfo);
        assertThat(actual.getKeyString(), is(Datastore.keyToString(createKey)));
        assertThat(actual.getTitle(), is("新規登録だぜぇ"));
        assertThat(actual.getMemo(), is("Memoだぜぇ"));
        assertThat(actual.getStartDate(), is("20121110"));
        assertThat(actual.getStartTime(), is("1000"));
        assertThat(actual.getEndDate(), is("20121111"));
        assertThat(actual.getEndTime(), is("2359"));
        assertThat(actual.getClosedFlg(), is("1"));
        assertThat(actual.getVersionNo(), is("1"));
        List<LabelValueBean> actualSelectedMemberList = actual.getSelectedMemberList();
        assertThat(actualSelectedMemberList.size(), is(2));
        assertThat(actualSelectedMemberList.get(0).getValue(), 
            is(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1))));
        assertThat(actualSelectedMemberList.get(1).getValue(), 
            is(Datastore.keyToString(Datastore.createKey(MemberModel.class, 2))));
    }

    /**
     * getFormのテスト.
     * 該当データ無し.
     */
    @Test
    public void testGetForm3() {
        createInitData();
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(Datastore.createKey(MemberModel.class, 1));
        
        //データ更新
        ScheduleForm actual = service.getForm(Datastore.keyToString(Datastore.createKey(ScheduleModel.class, -1)), 
            "",
            "", userInfo);
        assertThat(actual, is(nullValue()));
    }
    
    /**
     * getFormのテスト.
     * ログインユーザがスケジュール作成者でない.
     */
    @Test
    public void testGetForm4() {
        createInitData();
        Key createKey = createInitSchedule();
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(Datastore.createKey(MemberModel.class, 2));
        
        //データ更新
        ScheduleForm actual = service.getForm(Datastore.keyToString(createKey), 
            "",
            "", userInfo);
        assertThat(actual, is(nullValue()));
    }
    
    /**
     * 更新元Schedule作成.
     * @return 更新元データKey
     */
    private Key createInitSchedule() {
        //更新対象データ作成
        ScheduleForm form = new ScheduleForm();
        form.setTitle("新規登録だぜぇ");
        form.setMemo("Memoだぜぇ");
        form.setStartDate("20121110");
        form.setStartTime("1000");
        form.setEndDate("20121111");
        form.setEndTime("2359");
        form.setClosedFlg("1");
        form.setConnMemberKeyString(new String[]{
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)),
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 2))});
        Key createKey = service.put(form, Datastore.createKey(MemberModel.class, 1));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        return createKey;
    }
    
	/**
	 * 事前データ作成.
	 * Member作成：
	 * Aさん(id=1)
	 * Bさん(id=2)
     * Cさん(id=3)
	 */
	private void createInitData() {
	    //Member作成
	    MemberModel member = new MemberModel();
	    member.setKey(Datastore.createKey(MemberModel.class, 1));
	    member.setName("Aさん");
	    memberDao.put(member);
	    
	    member = new MemberModel();
        member.setKey(Datastore.createKey(MemberModel.class, 2));
        member.setName("Bさん");
        memberDao.put(member);
        
        member = new MemberModel();
        member.setKey(Datastore.createKey(MemberModel.class, 3));
        member.setName("Cさん");
        memberDao.put(member);
        
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
