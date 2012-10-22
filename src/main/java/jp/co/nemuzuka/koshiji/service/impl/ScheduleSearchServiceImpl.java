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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;

/**
 * MessageSearchServiceの実装クラス.
 * @author kazumune
 */
public class ScheduleSearchServiceImpl implements ScheduleSearchService {

    ScheduleDao scheduleDao = ScheduleDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
    
    private static ScheduleSearchServiceImpl impl = new ScheduleSearchServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static ScheduleSearchServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private ScheduleSearchServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleSearchService#createScheduleView4Week(java.util.Set, java.util.List, java.lang.String)
     */
    @Override
    public ScheduleView4Week createScheduleView4Week(
            Set<String> targetMemberKeys, List<Date> viewDateList,
            String loginMemberKeyString) {

        ScheduleView4Week scheduleView4Week = new ScheduleView4Week();
        scheduleView4Week.setViewDateRange(createViewDateRange(viewDateList));
        //日付データを作成
        scheduleView4Week.setViewDate(createViewDate4Week(viewDateList));
        //スケジュールデータを作成
        scheduleView4Week.setViewSchedule(getViewSchedule(targetMemberKeys, viewDateList, loginMemberKeyString));
        return scheduleView4Week;
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleSearchService#createScheduleView4Month(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public ScheduleView4Month createScheduleView4Month(
            String targetMemberKeyString, String targetYyyyMm,
            String loginMemberKeyString) {

        ScheduleView4Month scheduleView4Month = new ScheduleView4Month();
        scheduleView4Month.setViewMonth(createViewMonth(targetYyyyMm));

        //表示分の日付データを算出
        List<Date> viewDateList = DateTimeUtils.getStartEndDate4SunDayList(targetYyyyMm);
        
        //日付データを作成
        scheduleView4Month.setViewDate(createViewDate4Month(targetYyyyMm, viewDateList));
        //スケジュールデータを作成
        Set<String> targetMemberSet = new LinkedHashSet<String>();
        targetMemberSet.add(targetMemberKeyString);
        scheduleView4Month.setViewSchedule(getViewSchedule(targetMemberSet, viewDateList, loginMemberKeyString));
        return scheduleView4Month;
    }

    /**
     * 共通部分設定.
     * 共通部分を設定します。
     * @param viewDate 設定対象インスタンス
     * @param targetDate 対象日
     * @param cal カレンダー
     * @param currentDate システム日付
     * @param sdf フォーマッター
     */
    void commonSetting(ViewDate viewDate, Date targetDate, 
            Calendar cal, Date currentDate, SimpleDateFormat sdf) {
        //曜日を判断して設定
        String dayOfTheWeekName = DateTimeUtils.getDayOfTheWeekName(targetDate, cal);
        if(DateTimeUtils.SATURDAY.equals(dayOfTheWeekName)) {
            viewDate.setSaturday(true);
        } else if(DateTimeUtils.SUNDAY.equals(dayOfTheWeekName)) {
            viewDate.setSunday(true);
        }
        viewDate.setTargetDate(sdf.format(targetDate));
        //システム日付を判断して設定
        if(currentDate.getTime() == targetDate.getTime()) {
            viewDate.setToday(true);
        }
    }

    /**
     * スケジュール表示情報設定（時刻指定有り）.
     * 時刻指定有りの場合のスケジュール表示データを設定します。
     * @param viewSchedule 設定対象データ
     * @param viewData 表示文字列
     * @param targetDate 表示対象日付
     * @param schedule 設定元Schedule
     * @param sdf フォーマット
     */
    void setViewScheduleTitle4Time(ViewSchedule viewSchedule, StringBuilder viewData, 
            Date targetDate, ScheduleModel schedule, SimpleDateFormat sdf) {
        
        Date startDate = schedule.getStartDate();
        Date endDate = schedule.getEndDate();

        String startTime = schedule.getStartTime();
        String endTime = schedule.getEndTime();
        
        //表示対象日付 = スケジュール開始日 = スケジュール終了日
        if(targetDate.getTime() == startDate.getTime() &&
                targetDate.getTime() == endDate.getTime()) {
            //開始時刻-終了時刻を追加
            viewData.append(DateTimeUtils.formatTime(startTime)).append("-").append(DateTimeUtils.formatTime(endTime)).append(" ");
            viewSchedule.setStartTime(startTime);
            viewSchedule.setEndTime(endTime);

        } else if(targetDate.getTime() == startDate.getTime() &&
                targetDate.getTime() != endDate.getTime()) {
            //表示対象日付=スケジュール開始日≠スケジュール終了日
            //開始時刻-終了日 を追加
            viewData.append(DateTimeUtils.formatTime(startTime)).append("-").append(sdf.format(endDate)).append(" ");
            viewSchedule.setStartTime(startTime);
            viewSchedule.setEndTime("2359");

        } else if(startDate.getTime() < targetDate.getTime() &&
                targetDate.getTime() < endDate.getTime()) {
            //スケジュール開始日 < 表示対象日付 < スケジュール終了日
            //開始日-終了日 を追加
            viewData.append(sdf.format(startDate)).append("-").append(sdf.format(endDate)).append(" ");
            viewSchedule.setStartTime("0000");
            viewSchedule.setEndTime("2359");

        } else if(targetDate.getTime() != startDate.getTime() &&
                targetDate.getTime() == endDate.getTime()) {
            //表示対象日付 = スケジュール終了日 != スケジュール開始日
            //開始日-終了時刻 を追加
            viewData.append(sdf.format(startDate)).append("-").append(DateTimeUtils.formatTime(endTime)).append(" ");
            viewSchedule.setStartTime("0000");
            viewSchedule.setEndTime(endTime);
        }
    }
    
    /**
     * 表示対象年月生成.
     * @param targetYyyyMm 対象年月
     * @return 表示対象年月
     */
    private String createViewMonth(String targetYyyyMm) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        Date date;
        try {
            date = sdf.parse(targetYyyyMm);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy年M月");
        return sdf2.format(date);
    }
    
    /**
     * 月次日付データ生成.
     * 引数の表示対象日付Listを元に、日付データを取得します。
     * @param targetYyyyMm 表示対象年月
     * @param viewDates 表示対象日付List
     * @return 月次表示用日付データ
     */
    private List<ViewDate4Month> createViewDate4Month(String targetYyyyMm, List<Date> viewDates) {

        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
        SimpleDateFormat sdf2 = DateTimeUtils.createSdf("yyyyMM");

        Calendar cal = DateTimeUtils.getCalender();
        Date currentDate = CurrentDateUtils.getInstance().getCurrentDate();
        List<ViewDate4Month> retList = new ArrayList<ViewDate4Month>();

        int size = viewDates.size();

        for(int i = 0; i < size; i++) {
            Date targetDate = viewDates.get(i);
            ViewDate4Month viewDate4Month = new ViewDate4Month();

            //共通部分の設定
            commonSetting(viewDate4Month, targetDate, cal, currentDate, sdf);

            String yyyyMm = sdf2.format(targetDate);
            if(targetYyyyMm.equals(yyyyMm)) {
                viewDate4Month.setTargetMonth(true);
            }
            retList.add(viewDate4Month);
        }
        return retList;
    }
    
    /**
     * 週次日付データ生成.
     * 引数の表示対象日付Listを元に、日付データを取得します。
     * @param viewDateList 表示対象日付List
     * @return 週次表示用日付データ
     */
    private List<ViewDate4Week> createViewDate4Week(List<Date> viewDateList) {
        
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");

        Calendar cal = DateTimeUtils.getCalender();
        Date currentDate = CurrentDateUtils.getInstance().getCurrentDate();
        List<ViewDate4Week> retList = new ArrayList<ViewDate4Week>();
        
        int size = viewDateList.size();

        for(int i = 0; i < size; i++) {
            Date targetDate = viewDateList.get(i);
            ViewDate4Week viewDate4Week = new ViewDate4Week();

            //共通部分の設定
            commonSetting(viewDate4Week, targetDate, cal, currentDate, sdf);

            //個別部分の設定
            viewDate4Week.setDayOfTheWeekName(DateTimeUtils.getDayOfTheWeekName4JP(targetDate, cal));
            retList.add(viewDate4Week);
        }
        return retList;
    }

    /**
     * 表示対象期間生成.
     * 先頭と末尾の日付を元に、表示対象期間を生成します。
     * @param viewDateList 表示対象日List
     * @return 表示対象日付
     */
    private String createViewDateRange(List<Date> viewDateList) {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyy年M月d日");
        int lastIndex = viewDateList.size() -1;

        StringBuilder sb = new StringBuilder();
        sb.append(sdf.format(viewDateList.get(0))).append("～").append(sdf.format(viewDateList.get(lastIndex)));
        return sb.toString();
    }

    /**
     * Member毎のSchedule取得.
     * 指定Memberの対象期間に対する表示対象となるScheduleを作成します。
     * @param targetMemberKeys 表示対象MemberKeyStringSet
     * @param viewDates 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @return Schedule表示データ
     */
    List<MemberSchedule> getViewSchedule(Set<String> targetMemberKeys,
        List<Date> viewDates, String loginMemberKeyString) {

        if(targetMemberKeys == null || targetMemberKeys.size() == 0) {
            return new ArrayList<MemberSchedule>();
        }
    
        List<ScheduleModel> scheduleList = createScheduleList(targetMemberKeys, viewDates, loginMemberKeyString);
        Map<Key, MemberModel> memberMap = createMemberMap(targetMemberKeys);
        TargetSchedule targetSchedule = createTargetSchedule(scheduleList);
        return createResourceScheduleList(targetMemberKeys, viewDates, loginMemberKeyString, 
            targetSchedule, memberMap);
    }


    /**
     * 表示情報作成.
     * @param targetMemberKeys 表示対象MemberKeyStringSet
     * @param viewDates 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @param targetSchedule 処理対象スケジュールデータ
     * @param memberMap 表示対象MemberMap
     * @return Member毎の表示データList
     */
    private List<MemberSchedule> createResourceScheduleList(
            Set<String> targetMemberKeys, List<Date> viewDates,
            String loginMemberKeyString,
            TargetSchedule targetSchedule, Map<Key, MemberModel> memberMap) {
        
        List<MemberSchedule> retArray = new ArrayList<MemberSchedule>();
        for(String memberKeyString : targetMemberKeys) {
            //表示対象のMember毎に表示情報を作成する
            MemberSchedule targetMemberSchedule = createMemberSchedule(
                memberKeyString, viewDates, loginMemberKeyString, targetSchedule, memberMap);
            if(memberKeyString.equals(loginMemberKeyString)) {
                //ログインユーザが表示対象のMemberの場合、先頭に追加
                retArray.add(0, targetMemberSchedule);
            } else {
                retArray.add(targetMemberSchedule);
            }
        }
        return retArray;
    }

    /**
     * MemberSchedule作成.
     * 対象のMemberに対するMemberSchedleを作成します。
     * @param memberKeyString 対象MemberKeyString
     * @param viewDates 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @param targetSchedule 処理対象スケジュールデータ
     * @param memberMap 表示対象MemberMap
     * @return 対象Memberに対するMemberSchedule
     */
    private MemberSchedule createMemberSchedule(String memberKeyString,
            List<Date> viewDates, String loginMemberKeyString,
            TargetSchedule targetSchedule, Map<Key, MemberModel> memberMap) {
        
        MemberSchedule result = new MemberSchedule();
        
        //Member情報設定
        MemberModel member = memberMap.get(Datastore.stringToKey(memberKeyString));
        if(member != null) {
            result.setMemberKeyString(memberKeyString);
            result.setName(member.getName());
        } else {
            result.setMemberKeyString("");
            result.setName("");
        }
        
        //指定期間分のスケジュール情報作成
        int size = viewDates.size();
        for(int i = 0; i < size ; i++) {
            result.getDaySchedules().add(new DaySchedule());
        }

        //設定対象のスケジュール情報を取得
        Set<String> scheduleKeySet = targetSchedule.memberScheduleMap.get(memberKeyString);
        if(scheduleKeySet == null || scheduleKeySet.size() == 0) {
            return result;
        }
        for(String scheduleKeyString : scheduleKeySet) {
            setSchedule(result, viewDates, loginMemberKeyString, targetSchedule.scheduleMap.get(scheduleKeyString));
        }
        setDuplicateMark(result);
        
        return result;
    }

    /**
     * 重複チェック.
     * 同一日の設定されている時刻指定のスケジュールを参照し、時間が重複する場合、重複マークを付与します。
     * @param memberSchedule 設定対象スケジュール情報
     */
    private void setDuplicateMark(MemberSchedule memberSchedule) {

        List<DaySchedule> daySchedules = memberSchedule.getDaySchedules();
        for(DaySchedule target : daySchedules) {
            //時刻設定があるスケジュールのみ対象
            List<ViewSchedule> timeList = target.getTimeList();
            int size = timeList.size();
            for(int i = 0; i < size - 1; i++) {
                ViewSchedule fromViewSchedule = timeList.get(i);
                for(int j = i + 1; j < size; j++) {
                    ViewSchedule toViewSchedule = timeList.get(j);

                    //両方重複状態であればチェック不要
                    if(fromViewSchedule.isDuplicate() && toViewSchedule.isDuplicate()) {
                        continue;
                    }
                    //被っている場合、重複チェックを付与
                    if(DateTimeUtils.rangeCheck(fromViewSchedule.getStartTime(), fromViewSchedule.getEndTime(),
                            toViewSchedule.getStartTime(), toViewSchedule.getEndTime())) {
                        fromViewSchedule.setDuplicate(true);
                        toViewSchedule.setDuplicate(true);
                    }
                }
            }
        }
    }

    /**
     * スケジュールデータ設定.
     * 対象のスケジュールデータを設定します。
     * @param memberSchedule 設定対象データ
     * @param viewDates 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @param schedule 設定元Schedule
     */
    private void setSchedule(MemberSchedule memberSchedule, List<Date> viewDates,
            String loginMemberKeyString, ScheduleModel schedule) {
        
        //設定対象日付を1日ずつ移動し、設定日かを判断する
        for(Date targetDate : viewDates) {
            if(schedule.getStartDate().getTime() > targetDate.getTime() ||
                    targetDate.getTime() > schedule.getEndDate().getTime()) {
                //設定日でない場合、次の日へ
                continue;
            }
            //設定する
            int index = DateTimeUtils.getListIndex(targetDate, viewDates);
            setSchedule(index, memberSchedule, targetDate, loginMemberKeyString, schedule);
        }
    }

    /**
     * スケジュール設定.
     * @param index 設定位置
     * @param memberSchedule 設定対象データ
     * @param targetDate 表示対象日付
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @param schedule 設定元Schedule
     */
    private void setSchedule(int index, MemberSchedule memberSchedule,
            Date targetDate, String loginMemberKeyString, ScheduleModel schedule) {
        
        ViewSchedule viewSchedule = new ViewSchedule();
        StringBuilder viewData = new StringBuilder();
        SimpleDateFormat sdf = DateTimeUtils.createSdf("M/d");
        
        //設定対象日の情報を取得
        DaySchedule daySchedule = memberSchedule.getDaySchedules().get(index);
        //時刻の設定がある場合
        if(StringUtils.isNotEmpty(schedule.getStartTime())) {

            //時刻設定有りのListにadd
            daySchedule.getTimeList().add(viewSchedule);
            
            setViewScheduleTitle4Time(viewSchedule, viewData, 
                targetDate, schedule, sdf);

        } else {

            //時刻設定無しのListにadd
            daySchedule.getNoTimeList().add(viewSchedule);

            viewData.append("・");
        }
        setViewSchedule(viewSchedule, schedule, loginMemberKeyString, viewData);
    }

    /**
     * スケジュール表示情報共通設定.
     * 非公開フラグを参照し、ログインユーザが参照できる場合を意識した設定を行います。
     * @param viewSchedule 設定対象データ
     * @param schedule 設定元Schedule
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @param viewData 表示文字列
     */
    private void setViewSchedule(ViewSchedule viewSchedule,
            ScheduleModel schedule, String loginMemberKeyString,
            StringBuilder viewData) {
        
        String addString = schedule.getTitle();
        
        //非公開の場合
        String scheduleKeyString = schedule.getKeyToString();

        if(schedule.isClosed()) {
            String createMemberKeyString = Datastore.keyToString(schedule.getCreateMemberKey());
            //ログインユーザが登録している or 参加しているかによって表示を変更する
            if(createMemberKeyString.equals(loginMemberKeyString) || 
                    schedule.getConnMember().contains(loginMemberKeyString)) {
                //見せる
                addString = addString + "(非公開)";
            } else {
                //見せない
                addString = "(非公開)";
                scheduleKeyString = null;
            }
        }
        viewData.append(addString);
        
        viewSchedule.setScheduleKeyString(scheduleKeyString);
        viewSchedule.setViewData(viewData.toString());
    }

    /**
     * 処理対象スケジュールデータ作成.
     * @param scheduleList 表示対象スケジュールList
     * @return 処理対象スケジュールデータ
     */
    private TargetSchedule createTargetSchedule(List<ScheduleModel> scheduleList) {
        TargetSchedule result = new TargetSchedule();
        for(ScheduleModel target : scheduleList) {
            String scheduleKeyString = target.getKeyToString();
            result.scheduleMap.put(scheduleKeyString, target);

            //Scheduleに紐付くMemberの設定
            for(String memberKeyString : target.getConnMember()) {
                Set<String> targetSet = result.memberScheduleMap.get(memberKeyString);
                if(targetSet == null) {
                    targetSet = new LinkedHashSet<String>();
                    result.memberScheduleMap.put(memberKeyString, targetSet);
                }
                targetSet.add(scheduleKeyString);
            }
        }
        return result;
    }

    /**
     * Memberデータ取得
     * @param targetMemberKeys
     * @return
     */
    private Map<Key, MemberModel> createMemberMap(
            Set<String> targetMemberKeys) {
        
        Set<Key> keySet = new LinkedHashSet<Key>();
        for(String keyString : targetMemberKeys) {
            keySet.add(Datastore.stringToKey(keyString));
        }
        return memberDao.getMap(keySet.toArray(new Key[0]));
    }

    /**
     * 表示対象Scheduleデータ取得.
     * ログインユーザも意識して表示期間、表示対象MemberのScheduleを取得します。
     * @param targetMemberKeys 表示対象MemberKeyStringSet
     * @param viewDates 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @return 表示対象ScheduleList
     */
    private List<ScheduleModel> createScheduleList(Set<String> targetMemberKeys,
            List<Date> viewDates, String loginMemberKeyString) {
        
        //ログインユーザのKeyを含めて表示対象のMemberを作成する
        Set<String> allMembers = new LinkedHashSet<String>();
        allMembers.addAll(targetMemberKeys);
        allMembers.add(loginMemberKeyString);
        
        Date startDate = viewDates.get(0);
        Date endDate = viewDates.get(viewDates.size() -1);
        
        return scheduleDao.getList(startDate, endDate, allMembers);
    }

    /**
     * 処理対象スケジュールデータ.
     * @author k-katagiri
     */
    class TargetSchedule {
        /**
         * スケジュールMap.
         * キーはスケジュールKeyString、valueはスケジュールModel
         */
        public Map<String, ScheduleModel> scheduleMap = new HashMap<String, ScheduleModel>();

        /**
         * Member - スケジュールMap.
         * Member毎の参加スケジュールを判断する為に使用します。
         * キーはMemberKeyString、valueは紐づくスケジュールKeyStringのSet
         */
        public Map<String, Set<String>> memberScheduleMap = new HashMap<String, Set<String>>();

    }
}
