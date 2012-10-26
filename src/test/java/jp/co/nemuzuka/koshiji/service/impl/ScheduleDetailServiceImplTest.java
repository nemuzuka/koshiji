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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleDetailService;
import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MemberGroupConnServiceImplのテストクラス.
 * @author kazumune
 */
public class ScheduleDetailServiceImplTest extends AppEngineTestCase4HRD {

    ScheduleDetailServiceImpl service = ScheduleDetailServiceImpl.getInstance();
    ScheduleDao scheduleDao = ScheduleDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
    
	/**
	 * getのテスト.
	 * 公開
	 * 作成者＝参照ユーザ
	 * 開始日＝終了日
	 * 時間設定なし
	 */
	@Test
	public void testGet() {
	    createInitData();
	    Key createMemberKey = memberKeyList.get(0);
	    Set<String> memberKeys = new LinkedHashSet<String>();
	    memberKeys.add(Datastore.keyToString(memberKeyList.get(2)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
	    SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
	    createSchedule(ConvertUtils.toDate("20120101", sdf), "", 
	        ConvertUtils.toDate("20120101", sdf), "", memberKeys, false, createMemberKey, 1);
	    
	    UserInfo userInfo = new UserInfo();
	    userInfo.keyToString = Datastore.keyToString(memberKeyList.get(0));
	    userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(1));
	    
	    ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
	    assertThat(actual.model.getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
	    assertThat(actual.viewDate, is("2012年1月1日"));
        assertThat(actual.connMemberNames, is("name3,name2"));
        assertThat(actual.createMemberName, is("name0"));
        assertThat(actual.created, is(true));
	}

    /**
     * getのテスト.
     * 非公開
     * 作成者＝参照ユーザ
     * 開始日＝終了日
     * 時間設定なし
     */
    @Test
    public void testGet2() {
        createInitData();
        Key createMemberKey = memberKeyList.get(0);
        Set<String> memberKeys = new LinkedHashSet<String>();
        memberKeys.add(Datastore.keyToString(memberKeyList.get(2)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        createSchedule(ConvertUtils.toDate("20120101", sdf), "", 
            ConvertUtils.toDate("20120101", sdf), "", memberKeys, true, createMemberKey, 1);
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(0));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(1));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual.model.getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
        assertThat(actual.viewDate, is("2012年1月1日"));
        assertThat(actual.connMemberNames, is("name3,name2"));
        assertThat(actual.createMemberName, is("name0"));
        assertThat(actual.created, is(true));
    }
	
    /**
     * getのテスト.
     * 公開
     * 参加者＝参照ユーザ
     * 開始日≠終了日
     * 時間設定あり
     */
    @Test
    public void testGet3() {
        createInitData();
        Key createMemberKey = memberKeyList.get(0);
        Set<String> memberKeys = new LinkedHashSet<String>();
        memberKeys.add(Datastore.keyToString(memberKeyList.get(2)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        createSchedule(ConvertUtils.toDate("20120101", sdf), "1230", 
            ConvertUtils.toDate("20120102", sdf), "0900", memberKeys, false, createMemberKey, 1);
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(3));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(1));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual.model.getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
        assertThat(actual.viewDate, is("2012年1月1日 12:30 〜 2012年1月2日 09:00"));
        assertThat(actual.connMemberNames, is("name3,name2"));
        assertThat(actual.createMemberName, is("name0"));
        assertThat(actual.created, is(false));
    }
    
    /**
     * getのテスト.
     * 公開
     * 参加者＝登録者でも参照ユーザでもない
     * 開始日=終了日
     * 時間設定あり
     */
    @Test
    public void testGet4() {
        createInitData();
        Key createMemberKey = memberKeyList.get(0);
        Set<String> memberKeys = new LinkedHashSet<String>();
        memberKeys.add(Datastore.keyToString(memberKeyList.get(0)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        createSchedule(ConvertUtils.toDate("20120101", sdf), "1230", 
            ConvertUtils.toDate("20120101", sdf), "1300", memberKeys, false, createMemberKey, 1);
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(1));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(0));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual.model.getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
        assertThat(actual.viewDate, is("2012年1月1日 12:30 〜 13:00"));
        assertThat(actual.connMemberNames, is("name0 (他1名)"));
        assertThat(actual.createMemberName, is("name0"));
        assertThat(actual.created, is(false));
    }
    
    /**
     * getのテスト.
     * 公開
     * 参加者＝登録者でも参照ユーザでもない
     * 開始日≠終了日
     * 時間設定無し
     */
    @Test
    public void testGet5() {
        createInitData();
        Key createMemberKey = memberKeyList.get(3);
        Set<String> memberKeys = new LinkedHashSet<String>();
        memberKeys.add(Datastore.keyToString(memberKeyList.get(0)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        createSchedule(ConvertUtils.toDate("20120101", sdf), "", 
            ConvertUtils.toDate("20120115", sdf), "", memberKeys, false, createMemberKey, 1);
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(1));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(0));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual.model.getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
        assertThat(actual.viewDate, is("2012年1月1日 〜 2012年1月15日"));
        assertThat(actual.connMemberNames, is("name0 (他1名)"));
        assertThat(actual.createMemberName, is("※他グループのメンバー"));
        assertThat(actual.created, is(false));
    }
    
    /**
     * getのテスト.
     * 非公開
     * 参加者＝登録者でも参照ユーザでもない
     * 開始日≠終了日
     * 時間設定無し
     */
    @Test
    public void testGet6() {
        createInitData();
        Key createMemberKey = memberKeyList.get(3);
        Set<String> memberKeys = new LinkedHashSet<String>();
        memberKeys.add(Datastore.keyToString(memberKeyList.get(0)));
        memberKeys.add(Datastore.keyToString(memberKeyList.get(3)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        createSchedule(ConvertUtils.toDate("20120101", sdf), "", 
            ConvertUtils.toDate("20120115", sdf), "", memberKeys, true, createMemberKey, 1);
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(1));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(0));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual, is(nullValue()));
    }
    
    /**
     * getのテスト.
     * 存在しないスケジュール
     */
    @Test
    public void testGet7() {
        createInitData();
        
        UserInfo userInfo = new UserInfo();
        userInfo.keyToString = Datastore.keyToString(memberKeyList.get(1));
        userInfo.selectedGroupKeyString = Datastore.keyToString(groupKeyList.get(0));
        
        ScheduleDetailService.Detail actual = service.get(Datastore.createKey(ScheduleModel.class, 1), userInfo);
        assertThat(actual, is(nullValue()));
    }
    
    /**
     * Scheduleデータ作成.
     * @param startDate 開始日
     * @param startTime 開始時刻
     * @param endDate 終了日
     * @param endTime 終了時刻
     * @param memberKeys 関連MemberKeyStringSet
     * @param closed 非公開フラグ
     * @param createMemberKey 作成MemberKey
     * @param id KeyのID値
     */
    private void createSchedule(Date startDate, String startTime, Date endDate,
            String endTime, Set<String> memberKeys, boolean closed, Key createMemberKey, int id) {
        ScheduleModel model = new ScheduleModel();
        model.setKey(Datastore.createKey(ScheduleModel.class, id));
        model.setTitle("スケジュール:" + id);
        model.setMemo(new Text("メモ:" + id));
        model.setStartDate(startDate);
        model.setStartTime(startTime);
        model.setEndDate(endDate);
        model.setEndTime(endTime);
        model.setConnMember(memberKeys);
        model.setClosed(closed);
        model.setCreateMemberKey(createMemberKey);
        model.setLastUpdate(CurrentDateUtils.getInstance().getCurrentDateTime());
        scheduleDao.put(model);

        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
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
