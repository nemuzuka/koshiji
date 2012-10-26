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
package jp.co.nemuzuka.koshiji.controller.schedule.ajax;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleSearchServiceImpl;
import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;

/**
 * 週次グループスケジュール表示用Controller
 * @author kazumune
 */
public class WeekController extends JsonController {

    //リクエストパラメータ名.
    /** 日付指定時. */
    private static final String APPOINTMENT_DATE = "appointmentDate";
    /** 移動方向. */
    private static final String VIEW_TYPE = "viewType";
    /** 移動日数. */
    private static final String AMOUNT_TYPE = "amountType";
    
    private ScheduleSearchService scheduleSearchService = ScheduleSearchServiceImpl.getInstance();
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        ScheduleEntity entity = sessionScope(ScheduleEntity.KEY_NAME);
        UserInfo userInfo = getUserInfo();
        
        String targetDate = asString(APPOINTMENT_DATE);
        if(StringUtils.isNotEmpty(targetDate)) {
            //日付が指定されている場合、その日付を基準日とする
            SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
            entity.baseDate = ConvertUtils.toDate(targetDate, sdf);
        } else {
            //移動方向、移動日数を加味して基準日を算出
            entity.baseDate = calcBaseDate(entity.baseDate);
        }
        sessionScope(ScheduleEntity.KEY_NAME, entity);
        
        //基準日から1週間分のスケジュールを取得
        ScheduleSearchService.ScheduleView4Week result = 
                scheduleSearchService.createScheduleView4Week(createTargetMemberKeys(userInfo.getMemberList()), 
                    createViewDateList(entity.baseDate), userInfo.keyToString);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(result);
        jsonResult.setToken(setToken());
        return jsonResult;
    }

    /**
     * 表示対象日付List作成.
     * 基準日から7日分の日付Listを作成します。
     * @param baseDate 基準日
     * @return 表示対象日付
     */
    private List<Date> createViewDateList(Date baseDate) {
        Date endDate = DateTimeUtils.addDays(baseDate, 6);
        return DateTimeUtils.createDateList(baseDate, endDate);
    }

    /**
     * 表示対象MemberKeySet作成
     * @param memberList 選択グループに紐付くMemberList
     * @return MemberKeySet
     */
    private Set<String> createTargetMemberKeys(List<LabelValueBean> memberList) {
        Set<String> ret = new LinkedHashSet<String>();
        for(LabelValueBean target : memberList) {
            ret.add(target.getValue());
        }
        return ret;
    }
    
    /**
     * 基準日計算.
     * リクエストパラメータの値を元に、基準日を算出します。
     * 当日の場合、システム日付が基準日となります。
     * リフレッシュの場合、引数の基準日をそのまま返却します。
     * 翌週の場合、現在の基準日＋７日
     * 翌日の場合、現在の基準日＋１日
     * 前日の場合、現在の基準日−１日
     * 先週の場合、現在の基準日−７日
     * が基準日となります。
     * @param baseDate 基準日
     * @return 算出基準日
     */
    private Date calcBaseDate(Date baseDate) {
        
        String viewType = asString(VIEW_TYPE);
        int addType = 0;
        if("today".equals(viewType)) {
            return CurrentDateUtils.getInstance().getCurrentDate();
        } else if("refresh".equals(viewType)) {
            return baseDate;
        } else if("next".equals(viewType)) {
            addType = 1;
        } else if("prev".equals(viewType)) {
            addType = -1;
        }
        
        int amount = 0;
        String amountType = asString(AMOUNT_TYPE);
        if("day".equals(amountType)) {
            amount = 1;
        } else if("week".equals(amountType)) {
            amount = 7;
        }
        return DateTimeUtils.addDays(baseDate, addType * amount);
    }
}
