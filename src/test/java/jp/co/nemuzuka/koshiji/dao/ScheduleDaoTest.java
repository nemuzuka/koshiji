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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Text;

/**
 * ScheduleDaoのテストクラス.
 * @author kazumune
 */
public class ScheduleDaoTest extends AppEngineTestCase4HRD {

    ScheduleDao scheduleDao = ScheduleDao.getInstance();
	MemberDao memberDao = MemberDao.getInstance();
    
	/**
	 * getListのテスト.
	 * スケジュール開始日＝指定開始日
	 * スケジュール終了日＝指定終了日
	 * のデータが取得できることを確認
	 * @throws Exception 例外
	 */
	@Test
	public void testGetList() throws Exception {
	    createInitData();
	    
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120429");
        Date endDate = sdf.parse("20120429");
        //スケジュールに参加するMemberとしていないMemberを設定
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(1));
        ScheduleModel actual = actualList.get(0);
        assertThat(actual.getKey(), is(Datastore.createKey(ScheduleModel.class, 9)));
	}
	
    /**
     * getListのテスト.
     * 指定Memberが参加しているスケジュールが存在しない
     * @throws Exception 例外
     */
    @Test
    public void testGetList2() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120429");
        Date endDate = sdf.parse("20120429");
        //スケジュールは存在するが、参加Memberでない
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 100)));
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 101)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(0));
    }

    /**
     * getListのテスト.
     * スケジュール開始日＝指定終了日
     * のデータが取得できることを確認
     * @throws Exception 例外
     */
    @Test
    public void testGetList3() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120429");
        Date endDate = sdf.parse("20120430");
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 2)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(2));
        assertThat(actualList.get(0).getKey(), is(Datastore.createKey(ScheduleModel.class, 9)));
        assertThat(actualList.get(1).getKey(), is(Datastore.createKey(ScheduleModel.class, 4)));
    }
    
    /**
     * getListのテスト.
     * 指定開始日 < スケジュール開始日 && スケジュール終了日 < 指定終了日
     * のデータが取得できることを確認
     * @throws Exception 例外
     */
    @Test
    public void testGetList4() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120430");
        Date endDate = sdf.parse("20120502");
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(4));
        assertThat(actualList.get(0).getKey(), is(Datastore.createKey(ScheduleModel.class, 4)));
        assertThat(actualList.get(1).getKey(), is(Datastore.createKey(ScheduleModel.class, 1)));
        assertThat(actualList.get(2).getKey(), is(Datastore.createKey(ScheduleModel.class, 5)));
        assertThat(actualList.get(3).getKey(), is(Datastore.createKey(ScheduleModel.class, 6)));
    }
    
    /**
     * getListのテスト.
     * スケジュール開始日 < 指定開始日 && 指定終了日 < スケジュール終了日
     * のデータが取得できることを確認
     * @throws Exception 例外
     */
    @Test
    public void testGetList5() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120506");
        Date endDate = sdf.parse("20120506");
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(1));
        assertThat(actualList.get(0).getKey(), is(Datastore.createKey(ScheduleModel.class, 8)));
    }
    
    /**
     * getListのテスト.
     * スケジュール終了日 = 指定開始日
     * のデータが取得できることを確認
     * @throws Exception 例外
     */
    @Test
    public void testGetList6() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120505");
        Date endDate = sdf.parse("20120506");
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(3));
        assertThat(actualList.get(0).getKey(), is(Datastore.createKey(ScheduleModel.class, 7)));
        assertThat(actualList.get(1).getKey(), is(Datastore.createKey(ScheduleModel.class, 2)));
        assertThat(actualList.get(2).getKey(), is(Datastore.createKey(ScheduleModel.class, 8)));
    }
    
    
    /**
     * getListのテスト.
     * ソートの確認
     * @throws Exception 例外
     */
    @Test
    public void testGetList7() throws Exception {
        createInitData();
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120601");
        Date endDate = sdf.parse("20120602");
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<ScheduleModel> actualList = scheduleDao.getList(startDate, endDate, memberSet);
        assertThat(actualList.size(), is(5));
        assertThat(actualList.get(0).getKey(), is(Datastore.createKey(ScheduleModel.class, 11)));
        assertThat(actualList.get(1).getKey(), is(Datastore.createKey(ScheduleModel.class, 13)));
        assertThat(actualList.get(2).getKey(), is(Datastore.createKey(ScheduleModel.class, 12)));
        assertThat(actualList.get(3).getKey(), is(Datastore.createKey(ScheduleModel.class, 14)));
        assertThat(actualList.get(4).getKey(), is(Datastore.createKey(ScheduleModel.class, 15)));
    }
    
	/**
	 * 事前データ作成.
	 * Member作成：
	 * Aさん(id=1)
	 * Bさん(id=2)
	 * 
	 * Schedule作成：AさんとBさんが参加
	 * 5/1〜5/1(id=1)
	 * 5/5〜5/5(id=2)
     * 5/3〜5/3(id=3)
     * 4/30〜5/1(id=4)
     * 5/1〜5/2(id=5)
     * 5/2〜5/3(id=6)
     * 5/4〜5/5(id=7)
     * 5/5〜5/7(id=8)
     * 4/29〜4/29(id=9)
     * 5/7〜5/7(id=10)
     * 6/1 10:00(id=11)
     * 6/1 10:10(id=12)
     * 6/1 10:00(id=13)
     * 6/2(id=14)
     * 6/2 10:00(id=15)
	 * @throws ParseException 例外
	 */
	private void createInitData() throws ParseException {
	    //Member作成
	    MemberModel member = new MemberModel();
	    member.setKey(Datastore.createKey(MemberModel.class, 1));
	    member.setName("Aさん");
	    memberDao.put(member);
	    
	    member = new MemberModel();
        member.setKey(Datastore.createKey(MemberModel.class, 2));
        member.setName("Bさん");
        memberDao.put(member);
        
        //Schedule作成
        Set<String> memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 2)));
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Date startDate = sdf.parse("20120501");
        Date endDate = sdf.parse("20120501");
        createSchedule(startDate, endDate, memberSet, 1);

        startDate = sdf.parse("20120505");
        endDate = sdf.parse("20120505");
        createSchedule(startDate, endDate, memberSet, 2);

        startDate = sdf.parse("20120503");
        endDate = sdf.parse("20120503");
        createSchedule(startDate, endDate, memberSet, 3);
        
        startDate = sdf.parse("20120430");
        endDate = sdf.parse("20120501");
        createSchedule(startDate, endDate, memberSet, 4);

        startDate = sdf.parse("20120501");
        endDate = sdf.parse("20120502");
        createSchedule(startDate, endDate, memberSet, 5);
        
        startDate = sdf.parse("20120502");
        endDate = sdf.parse("20120503");
        createSchedule(startDate, endDate, memberSet, 6);
        
        startDate = sdf.parse("20120504");
        endDate = sdf.parse("20120505");
        createSchedule(startDate, endDate, memberSet, 7);
        
        startDate = sdf.parse("20120505");
        endDate = sdf.parse("20120507");
        createSchedule(startDate, endDate, memberSet, 8);

        startDate = sdf.parse("20120429");
        endDate = sdf.parse("20120429");
        createSchedule(startDate, endDate, memberSet, 9);

        startDate = sdf.parse("20120507");
        endDate = sdf.parse("20120507");
        createSchedule(startDate, endDate, memberSet, 10);
        
        //ソートテスト用データ作成
        startDate = sdf.parse("20120601");
        endDate = sdf.parse("20120601");
        createSchedule(startDate, "1000", endDate, "1030", memberSet, 11);

        startDate = sdf.parse("20120601");
        endDate = sdf.parse("20120601");
        createSchedule(startDate, "1010", endDate, "1030", memberSet, 12);
        
        startDate = sdf.parse("20120601");
        endDate = sdf.parse("20120601");
        createSchedule(startDate, "1000", endDate, "1030", memberSet, 13);
        
        startDate = sdf.parse("20120602");
        endDate = sdf.parse("20120602");
        createSchedule(startDate, endDate, memberSet, 14);
        
        startDate = sdf.parse("20120602");
        endDate = sdf.parse("20120602");
        createSchedule(startDate, "1000", endDate, "1030", memberSet, 15);
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
	}


	/**
     * Scheduleデータ作成.
     * 開始時刻と終了時刻は空文字で作成します。
     * @param startDate 開始日
     * @param endDate 終了日
     * @param memberKeys 関連MemberKeyStringSet
     * @param id KeyのID値
     */
    private void createSchedule(Date startDate, Date endDate,
            Set<String> memberKeys, int id) {
        createSchedule(startDate, "", endDate, "", memberKeys, id);
    }

	/**
	 * Scheduleデータ作成.
	 * @param startDate 開始日
	 * @param startTime 開始時刻
	 * @param endDate 終了日
     * @param endTime 終了時刻
	 * @param memberKeys 関連MemberKeyStringSet
	 * @param id KeyのID値
	 */
	private void createSchedule(Date startDate, String startTime, Date endDate,
            String endTime, Set<String> memberKeys, int id) {
        ScheduleModel model = new ScheduleModel();
        model.setKey(Datastore.createKey(ScheduleModel.class, id));
        model.setTitle("スケジュール:" + id);
        model.setMemo(new Text("メモ:" + id));
        model.setStartDate(startDate);
        model.setStartTime(startTime);
        model.setEndDate(endDate);
        model.setEndTime(endTime);
        model.setConnMember(memberKeys);
        scheduleDao.put(model);
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
