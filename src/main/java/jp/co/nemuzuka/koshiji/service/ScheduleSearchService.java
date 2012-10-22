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
package jp.co.nemuzuka.koshiji.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * スケジュール検索に関するService
 * @author kazumune
 */
public interface ScheduleSearchService {

    /**
     * 週次スケジュール表示用データ作成.
     * 指定したMemberKeyStringSetに対する指定期間のスケジュールデータを作成します。
     * @param targetMemberKeys 表示対象MemberKeyStringSet
     * @param viewDateList 表示対象日付List
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @return 週次スケジュール表示用データ
     */
    ScheduleView4Week createScheduleView4Week(Set<String> targetMemberKeys, 
            List<Date> viewDateList, String loginMemberKeyString);

    /**
     * 月次スケジュール表示用データ作成.
     * 指定したMemberKeyに対する指定期間のスケジュールデータを作成します。
     * @param targetMemberKeyString 表示対象MemberKeyString
     * @param targetYyyyMm 表示対象年月
     * @param loginMemberKeyString ログインユーザのMemberKeyString
     * @return 月次スケジュール表示用データ
     */
    ScheduleView4Month createScheduleView4Month(String targetMemberKeyString, 
            String targetYyyyMm, String loginMemberKeyString);
    
    /**
     * 週次表示用スケジュールデータ.
     * @author k-katagiri
     */
    class ScheduleView4Week implements Serializable {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /** 表示対象期間. */
        private String viewDateRange;
        /** 週次データ表示時の日付データ. */
        //Listの1要素が1日分のデータ
        private List<ViewDate4Week> viewDate;
        /** 週次データ表示時のスケジュールデータ. */
        //Listの1要素が1Member分のデータ
        private List<MemberSchedule> viewSchedule;
        /**
         * @return viewDateRange
         */
        public String getViewDateRange() {
            return viewDateRange;
        }
        /**
         * @param viewDateRange セットする viewDateRange
         */
        public void setViewDateRange(String viewDateRange) {
            this.viewDateRange = viewDateRange;
        }
        /**
         * @return viewDate
         */
        public List<ViewDate4Week> getViewDate() {
            return viewDate;
        }
        /**
         * @param viewDate セットする viewDate
         */
        public void setViewDate(List<ViewDate4Week> viewDate) {
            this.viewDate = viewDate;
        }
        /**
         * @return viewSchedule
         */
        public List<MemberSchedule> getViewSchedule() {
            return viewSchedule;
        }
        /**
         * @param viewSchedule セットする viewSchedule
         */
        public void setViewSchedule(List<MemberSchedule> viewSchedule) {
            this.viewSchedule = viewSchedule;
        }
    }

    /**
     * 月次表示用スケジュールデータ.
     * @author k-katagiri
     */
    class ScheduleView4Month implements Serializable {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;

        /** 表示対象期間. */
        private String viewMonth;
        /** 月次データ表示時の日付データ. */
        private List<ViewDate4Month> viewDate;
        /** 月次データ表示時のスケジュールデータ. */
        private List<MemberSchedule> viewSchedule;
        /**
         * @return viewMonth
         */
        public String getViewMonth() {
            return viewMonth;
        }
        /**
         * @param viewMonth セットする viewMonth
         */
        public void setViewMonth(String viewMonth) {
            this.viewMonth = viewMonth;
        }
        /**
         * @return viewDate
         */
        public List<ViewDate4Month> getViewDate() {
            return viewDate;
        }
        /**
         * @param viewDate セットする viewDate
         */
        public void setViewDate(List<ViewDate4Month> viewDate) {
            this.viewDate = viewDate;
        }
        /**
         * @return viewSchedule
         */
        public List<MemberSchedule> getViewSchedule() {
            return viewSchedule;
        }
        /**
         * @param viewSchedule セットする viewSchedule
         */
        public void setViewSchedule(List<MemberSchedule> viewSchedule) {
            this.viewSchedule = viewSchedule;
        }
    }

    /**
     * 日付データ基底クラス.
     * @author k-katagiri
     */
    abstract class ViewDate implements Serializable {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** 当日フラグ. */
        private boolean today;
        /** 土曜フラグ. */
        private boolean saturday;
        /** 日曜フラグ. */
        private boolean sunday;
        /** 祝日フラグ. */
        private boolean holiday;
        /** 対象日付(yyyyMMdd形式). */
        private String targetDate;
        /** 祝日名. */
        private String holidayName = "";

        /**
         * @return holidayName
         */
        public String getHolidayName() {
            return holidayName;
        }
        /**
         * @param holidayName セットする holidayName
         */
        public void setHolidayName(String holidayName) {
            this.holidayName = holidayName;
        }
        /**
         * @return targetDate
         */
        public String getTargetDate() {
            return targetDate;
        }
        /**
         * @param targetDate セットする targetDate
         */
        public void setTargetDate(String targetDate) {
            this.targetDate = targetDate;
        }
        /**
         * @return today
         */
        public boolean isToday() {
            return today;
        }
        /**
         * @param today セットする today
         */
        public void setToday(boolean today) {
            this.today = today;
        }
        /**
         * @return saturday
         */
        public boolean isSaturday() {
            return saturday;
        }
        /**
         * @param saturday セットする saturday
         */
        public void setSaturday(boolean saturday) {
            this.saturday = saturday;
        }
        /**
         * @return sunday
         */
        public boolean isSunday() {
            return sunday;
        }
        /**
         * @param sunday セットする sunday
         */
        public void setSunday(boolean sunday) {
            this.sunday = sunday;
        }
        /**
         * @return holiday
         */
        public boolean isHoliday() {
            return holiday;
        }
        /**
         * @param holiday セットする holiday
         */
        public void setHoliday(boolean holiday) {
            this.holiday = holiday;
        }
    }

    /**
     * 週次データ表示時の日付データ.
     * @author k-katagiri
     */
    class ViewDate4Week extends ViewDate {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** 曜日名. */
        private String dayOfTheWeekName;
        /**
         * @return dayOfTheWeekName
         */
        public String getDayOfTheWeekName() {
            return dayOfTheWeekName;
        }
        /**
         * @param dayOfTheWeekName セットする dayOfTheWeekName
         */
        public void setDayOfTheWeekName(String dayOfTheWeekName) {
            this.dayOfTheWeekName = dayOfTheWeekName;
        }
    }

    /**
     * 月次データ表示時の日付データ.
     * @author k-katagiri
     */
    class ViewDate4Month extends ViewDate {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** 表示対象年月か. */
        private boolean targetMonth;

        /**
         * @return targetMonth
         */
        public boolean isTargetMonth() {
            return targetMonth;
        }
        /**
         * @param targetMonth セットする targetMonth
         */
        public void setTargetMonth(boolean targetMonth) {
            this.targetMonth = targetMonth;
        }
    }
    /**
     * Member毎のスケジュール保持クラス.
     * @author k-katagiri
     */
    class MemberSchedule implements Serializable {

        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** 対象MemberKeyString. */
        private String memberKeyString;
        /** 表示名称. */
        private String name;

        /** 表示対象期間のスケジュール. */
        //Listの1要素が1日分のデータ。スケジュールが存在しなくても、要素を追加する
        private List<DaySchedule> daySchedules = new ArrayList<DaySchedule>();

        /**
         * @return memberKeyString
         */
        public String getMemberKeyString() {
            return memberKeyString;
        }
        /**
         * @param memberKeyString セットする memberKeyString
         */
        public void setMemberKeyString(String memberKeyString) {
            this.memberKeyString = memberKeyString;
        }
        /**
         * @return name
         */
        public String getName() {
            return name;
        }
        /**
         * @param name セットする name
         */
        public void setName(String name) {
            this.name = name;
        }
        /**
         * @return daySchedules
         */
        public List<DaySchedule> getDaySchedules() {
            return daySchedules;
        }
        /**
         * @param daySchedules セットする daySchedules
         */
        public void setDaySchedules(List<DaySchedule> daySchedules) {
            this.daySchedules = daySchedules;
        }

    }

    /**
     * 1日当たりの表示スケジュール.
     * @author k-katagiri
     */
    class DaySchedule implements Serializable {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** 時刻指定無しList. */
        private List<ViewSchedule> noTimeList = new ArrayList<ViewSchedule>();
        /** 時刻指定有りList. */
        private List<ViewSchedule> timeList = new ArrayList<ViewSchedule>();
        /**
         * @return noTimeList
         */
        public List<ViewSchedule> getNoTimeList() {
            return noTimeList;
        }
        /**
         * @param noTimeList セットする noTimeList
         */
        public void setNoTimeList(List<ViewSchedule> noTimeList) {
            this.noTimeList = noTimeList;
        }
        /**
         * @return timeList
         */
        public List<ViewSchedule> getTimeList() {
            return timeList;
        }
        /**
         * @param timeList セットする timeList
         */
        public void setTimeList(List<ViewSchedule> timeList) {
            this.timeList = timeList;
        }

    }

    /**
     * スケジュール表示情報.
     * @author k-katagiri
     */
    class ViewSchedule implements Serializable {
        /**
         * serialVersionUID.
         */
        private static final long serialVersionUID = 1L;
        /** スケジュールKey. */
        private String scheduleKeyString;
        /** 表示名. */
        private String viewData;
        /** 重複フラグ. */
        private boolean duplicate;

        /** 開始時刻. */
        private String startTime;
        /** 終了時刻. */
        private String endTime;
        /**
         * @return scheduleKeyString
         */
        public String getScheduleKeyString() {
            return scheduleKeyString;
        }
        /**
         * @param scheduleKeyString セットする scheduleKeyString
         */
        public void setScheduleKeyString(String scheduleKeyString) {
            this.scheduleKeyString = scheduleKeyString;
        }
        /**
         * @return viewData
         */
        public String getViewData() {
            return viewData;
        }
        /**
         * @param viewData セットする viewData
         */
        public void setViewData(String viewData) {
            this.viewData = viewData;
        }
        /**
         * @return duplicate
         */
        public boolean isDuplicate() {
            return duplicate;
        }
        /**
         * @param duplicate セットする duplicate
         */
        public void setDuplicate(boolean duplicate) {
            this.duplicate = duplicate;
        }
        /**
         * @return startTime
         */
        public String getStartTime() {
            return startTime;
        }
        /**
         * @param startTime セットする startTime
         */
        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }
        /**
         * @return endTime
         */
        public String getEndTime() {
            return endTime;
        }
        /**
         * @param endTime セットする endTime
         */
        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }
    }
}
