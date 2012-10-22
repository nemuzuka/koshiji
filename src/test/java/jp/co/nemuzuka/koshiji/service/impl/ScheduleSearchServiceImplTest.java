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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService.DaySchedule;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService.MemberSchedule;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService.ViewDate4Month;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService.ViewSchedule;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * ScheduleSearchServiceImplのテストクラス.
 * @author kazumune
 */
public class ScheduleSearchServiceImplTest extends AppEngineTestCase4HRD {

    ScheduleSearchServiceImpl service = ScheduleSearchServiceImpl.getInstance();
    ScheduleDao scheduleDao = ScheduleDao.getInstance();
	MemberDao memberDao = MemberDao.getInstance();
    
	/**
	 * commonSettingのテスト.
	 * 出力日がシステム日付と合致する場合
	 */
	@Test
	public void testCommonSetting() throws ParseException {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        Calendar cal = DateTimeUtils.getCalender();
        //対象日をシステム日付にする
        Date currentDate = CurrentDateUtils.getInstance().getCurrentDate();
        ScheduleSearchService.ViewDate4Week actual = new ScheduleSearchService.ViewDate4Week();
        service.commonSetting(actual, currentDate, cal, currentDate, sdf);
        assertThat(actual.isToday(), is(true));
	    
        //対象日を1日ずらす
        Date afterDate = DateTimeUtils.addDays(currentDate, 1);
        actual = new ScheduleSearchService.ViewDate4Week();        
        service.commonSetting(actual, afterDate, cal, currentDate, sdf);
        assertThat(actual.isToday(), is(false));
	}
	
	/**
	 * createScheduleView4Weekのテスト.
	 * 登録されていないMemberを指定し、
	 * 指定期間分の日付データが作成されることの確認
	 * @throws ParseException 例外
	 */
	@Test
	public void testCreateScheduleView4Week() throws ParseException {
	    createInitData();
	    SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
	    List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120428"));
	    viewDates.add(sdf.parse("20120429"));
        viewDates.add(sdf.parse("20120430"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 101)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
	        Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
	    assertThat(actual.getViewDateRange(), is("2012年4月28日～2012年4月30日"));
	    List<ScheduleSearchService.ViewDate4Week> actualViewDateList = actual.getViewDate();
	    assertThat(actualViewDateList.size(), is(3));
	    ScheduleSearchService.ViewDate4Week actualViewDate = actualViewDateList.get(0);
        assertThat(actualViewDate.getDayOfTheWeekName(), is("土"));
        assertThat(actualViewDate.getTargetDate(), is("20120428"));
        assertThat(actualViewDate.isHoliday(), is(false));
        assertThat(actualViewDate.isSaturday(), is(true));
        assertThat(actualViewDate.isSunday(), is(false));
        assertThat(actualViewDate.isToday(), is(false));
        
        actualViewDate = actualViewDateList.get(1);
        assertThat(actualViewDate.getDayOfTheWeekName(), is("日"));
        assertThat(actualViewDate.getTargetDate(), is("20120429"));
        assertThat(actualViewDate.isHoliday(), is(false));
        assertThat(actualViewDate.isSaturday(), is(false));
        assertThat(actualViewDate.isSunday(), is(true));
        assertThat(actualViewDate.isToday(), is(false));
        
        actualViewDate = actualViewDateList.get(2);
        assertThat(actualViewDate.getDayOfTheWeekName(), is("月"));
        assertThat(actualViewDate.getTargetDate(), is("20120430"));	
        assertThat(actualViewDate.isHoliday(), is(false));
        assertThat(actualViewDate.isSaturday(), is(false));
        assertThat(actualViewDate.isSunday(), is(false));
        assertThat(actualViewDate.isToday(), is(false));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        assertThat(actualViewSchedule.size(), is(1));
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        assertThat(actualMemberSchedule.getMemberKeyString(), is(""));
        assertThat(actualMemberSchedule.getName(), is(""));
        
        //スケジュールは登録されていなくても指定期間分のサイズのListが作成されること
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(3));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(1).getNoTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(1).getTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(2).getNoTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(2).getTimeList().size(), is(0));
        
    }
	
    /**
     * createScheduleView4Weekのテスト.
     * 時間未指定のスケジュールが取得できることの確認
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week2() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120429"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        assertThat(actualViewSchedule.size(), is(1));
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        assertThat(actualMemberSchedule.getMemberKeyString(), is(
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1))));
        assertThat(actualMemberSchedule.getName(), is("Aさん"));
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(1));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getNoTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("・スケジュール:9"));
        assertThat(viewSchedule.getStartTime(), is(nullValue()));
        assertThat(viewSchedule.getEndTime(), is(nullValue()));
        assertThat(viewSchedule.getScheduleKeyString(), is(
            Datastore.keyToString(Datastore.createKey(ScheduleModel.class, 9))));
        assertThat(viewSchedule.isDuplicate(), is(false));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(0));
        
    }
	
    /**
     * createScheduleView4Weekのテスト.
     * 時間指定のスケジュールが取得できることの確認
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week3() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120602"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(1));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(1));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("10:00-10:30 スケジュール:15"));
        assertThat(viewSchedule.getStartTime(), is("1000"));
        assertThat(viewSchedule.getEndTime(), is("1030"));
        assertThat(viewSchedule.getScheduleKeyString(), is(
            Datastore.keyToString(Datastore.createKey(ScheduleModel.class, 15))));
        assertThat(viewSchedule.isDuplicate(), is(false));
    }
    
    /**
     * createScheduleView4Weekのテスト.
     * 時間指定/重複ありのスケジュールが取得できることの確認
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week4() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120601"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(0));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(3));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("10:00-10:30 スケジュール:11"));
        assertThat(viewSchedule.isDuplicate(), is(true));

        viewSchedule = actualDaySchedules.get(0).getTimeList().get(1);
        assertThat(viewSchedule.getViewData(), is("10:00-10:30 スケジュール:13"));
        assertThat(viewSchedule.isDuplicate(), is(true));
        
        viewSchedule = actualDaySchedules.get(0).getTimeList().get(2);
        assertThat(viewSchedule.getViewData(), is("10:10-10:30 スケジュール:12"));
        assertThat(viewSchedule.isDuplicate(), is(true));
    }

    /**
     * createScheduleView4Weekのテスト.
     * 非公開スケジュールでログインユーザが参加者の場合
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week5() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120603"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        assertThat(actualViewSchedule.size(), is(1));
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(1));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getNoTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("・スケジュール:16(非公開)"));
        assertThat(viewSchedule.getStartTime(), is(nullValue()));
        assertThat(viewSchedule.getEndTime(), is(nullValue()));
        assertThat(viewSchedule.getScheduleKeyString(), is(
            Datastore.keyToString(Datastore.createKey(ScheduleModel.class, 16))));
        assertThat(viewSchedule.isDuplicate(), is(false));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(0));
        
    }
    
    /**
     * createScheduleView4Weekのテスト.
     * 非公開スケジュールでログインユーザが登録者の場合
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week6() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120603"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        assertThat(actualViewSchedule.size(), is(1));
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(1));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getNoTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("・スケジュール:16(非公開)"));
        assertThat(viewSchedule.getStartTime(), is(nullValue()));
        assertThat(viewSchedule.getEndTime(), is(nullValue()));
        assertThat(viewSchedule.getScheduleKeyString(), is(
            Datastore.keyToString(Datastore.createKey(ScheduleModel.class, 16))));
        assertThat(viewSchedule.isDuplicate(), is(false));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(0));
        
    }
    
    /**
     * createScheduleView4Weekのテスト.
     * 非公開スケジュールでログインユーザがメンバーでもなく登録者でもない場合
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Week7() throws ParseException {
        createInitData();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        List<Date> viewDates = new ArrayList<Date>();
        viewDates.add(sdf.parse("20120603"));
        Set<String> targetMemberKeys = new LinkedHashSet<String>();
        targetMemberKeys.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));
        
        ScheduleSearchService.ScheduleView4Week actual = service.createScheduleView4Week(targetMemberKeys, viewDates, 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 2)));
        
        List<MemberSchedule> actualViewSchedule = actual.getViewSchedule();
        assertThat(actualViewSchedule.size(), is(1));
        MemberSchedule actualMemberSchedule = actualViewSchedule.get(0);
        
        List<DaySchedule> actualDaySchedules = actualMemberSchedule.getDaySchedules();
        assertThat(actualDaySchedules.size(), is(1));
        assertThat(actualDaySchedules.get(0).getNoTimeList().size(), is(1));
        ViewSchedule viewSchedule = actualDaySchedules.get(0).getNoTimeList().get(0);
        assertThat(viewSchedule.getViewData(), is("・(非公開)"));
        assertThat(viewSchedule.getStartTime(), is(nullValue()));
        assertThat(viewSchedule.getEndTime(), is(nullValue()));
        assertThat(viewSchedule.getScheduleKeyString(), is(nullValue()));
        assertThat(viewSchedule.isDuplicate(), is(false));
        assertThat(actualDaySchedules.get(0).getTimeList().size(), is(0));
        
    }
    
    /**
     * setViewScheduleTitle4Timeのテスト.
     * 表示対象日付=スケジュール開始日≠スケジュール終了日
     * @throws ParseException ParseException
     */
    @Test
    public void testSetViewScheduleTitle4Time() throws ParseException {
        ViewSchedule viewSchedule = new ViewSchedule();
        StringBuilder viewData = new StringBuilder();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyy/MM/dd");
        Date targetDate = sdf.parse("2012/01/01");
        ScheduleModel schedule = new ScheduleModel();
        schedule.setTitle("タイトルA");
        schedule.setStartDate(sdf.parse("2012/01/01"));
        schedule.setStartTime("1000");
        schedule.setEndDate(sdf.parse("2012/01/02"));
        schedule.setEndTime("1100");
        
        service.setViewScheduleTitle4Time(viewSchedule, viewData, targetDate, schedule, sdf);
        assertThat(viewData.toString(), is("10:00-2012/01/02 "));
        assertThat(viewSchedule.getStartTime(), is("1000"));
        assertThat(viewSchedule.getEndTime(), is("2359"));
    }
    
    /**
     * setViewScheduleTitle4Timeのテスト.
     * スケジュール開始日 < 表示対象日付 < スケジュール終了日
     * @throws ParseException ParseException
     */
    @Test
    public void testSetViewScheduleTitle4Time2() throws ParseException {
        ViewSchedule viewSchedule = new ViewSchedule();
        StringBuilder viewData = new StringBuilder();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyy/MM/dd");
        Date targetDate = sdf.parse("2012/01/01");
        ScheduleModel schedule = new ScheduleModel();
        schedule.setTitle("タイトルA");
        schedule.setStartDate(sdf.parse("2011/12/31"));
        schedule.setStartTime("1000");
        schedule.setEndDate(sdf.parse("2012/01/02"));
        schedule.setEndTime("1100");
        
        service.setViewScheduleTitle4Time(viewSchedule, viewData, targetDate, schedule, sdf);
        assertThat(viewData.toString(), is("2011/12/31-2012/01/02 "));
        assertThat(viewSchedule.getStartTime(), is("0000"));
        assertThat(viewSchedule.getEndTime(), is("2359"));
    }
    
    /**
     * setViewScheduleTitle4Timeのテスト.
     * 表示対象日付 = スケジュール終了日 != スケジュール開始日
     * @throws ParseException ParseException
     */
    @Test
    public void testSetViewScheduleTitle4Time3() throws ParseException {
        ViewSchedule viewSchedule = new ViewSchedule();
        StringBuilder viewData = new StringBuilder();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyy/MM/dd");
        Date targetDate = sdf.parse("2012/01/01");
        ScheduleModel schedule = new ScheduleModel();
        schedule.setTitle("タイトルA");
        schedule.setStartDate(sdf.parse("2011/12/31"));
        schedule.setStartTime("1000");
        schedule.setEndDate(sdf.parse("2012/01/01"));
        schedule.setEndTime("1100");
        
        service.setViewScheduleTitle4Time(viewSchedule, viewData, targetDate, schedule, sdf);
        assertThat(viewData.toString(), is("2011/12/31-11:00 "));
        assertThat(viewSchedule.getStartTime(), is("0000"));
        assertThat(viewSchedule.getEndTime(), is("1100"));
    }
    
    /**
     * createScheduleView4Monthのテスト.
     * @throws ParseException 例外
     */
    @Test
    public void testCreateScheduleView4Month() throws ParseException {
        createInitData();
        
        ScheduleSearchService.ScheduleView4Month actual = service.createScheduleView4Month(
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)), "201206", 
            Datastore.keyToString(Datastore.createKey(MemberModel.class, 1)));
        
        assertThat(actual.getViewMonth(), is("2012年6月"));
        List<ViewDate4Month> actualViewDate = actual.getViewDate();
        assertThat(actualViewDate.size(), is(35));
        assertThat(actualViewDate.get(0).getTargetDate(), is("20120527"));
        assertThat(actualViewDate.get(34).getTargetDate(), is("20120630"));
        
    }
    
	/**
	 * 事前データ作成.
	 * Member作成：
	 * Aさん(id=1)
	 * Bさん(id=2)
     * Cさん(id=3)
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
     * 6/3(id=16)非公開：作成者Aさん：参加者Cさん
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
        
        member = new MemberModel();
        member.setKey(Datastore.createKey(MemberModel.class, 3));
        member.setName("Cさん");
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
        createSchedule(startDate, "1000", endDate, "1030", memberSet, false, 
            Datastore.createKey(MemberModel.class, 1), 11);

        startDate = sdf.parse("20120601");
        endDate = sdf.parse("20120601");
        createSchedule(startDate, "1010", endDate, "1030", memberSet, false, 
            Datastore.createKey(MemberModel.class, 1), 12);
        
        startDate = sdf.parse("20120601");
        endDate = sdf.parse("20120601");
        createSchedule(startDate, "1000", endDate, "1030", memberSet, false, 
            Datastore.createKey(MemberModel.class, 1), 13);
        
        startDate = sdf.parse("20120602");
        endDate = sdf.parse("20120602");
        createSchedule(startDate, endDate, memberSet, 14);
        
        startDate = sdf.parse("20120602");
        endDate = sdf.parse("20120602");
        createSchedule(startDate, "1000", endDate, "1030", memberSet, false, 
            Datastore.createKey(MemberModel.class, 1), 15);
        
        //非公開スケジュール作成
        memberSet = new LinkedHashSet<String>();
        memberSet.add(Datastore.keyToString(Datastore.createKey(MemberModel.class, 3)));

        startDate = sdf.parse("20120603");
        endDate = sdf.parse("20120603");

        createSchedule(startDate, "", endDate, "", memberSet, true, 
            Datastore.createKey(MemberModel.class, 1), 16);
        
        
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
        createSchedule(startDate, "", endDate, "", memberKeys, false, 
            Datastore.createKey(MemberModel.class, 1), id);
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
