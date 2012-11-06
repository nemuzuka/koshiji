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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.CurrentDateUtils;
import jp.co.nemuzuka.utils.DateTimeUtils;

/**
 * 日次グループスケジュール表示用Controller
 * @author kazumune
 */
public class DayController extends BaseController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        ScheduleEntity entity = getScheduleEntity();
        
        String targetDate = asString(APPOINTMENT_DATE);
        if(StringUtils.isNotEmpty(targetDate)) {
            //日付が指定されている場合、その日付を基準日とする
            SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd");
            entity.baseDate = ConvertUtils.toDate(targetDate, sdf);
        } else {
            //基準日を算出
            entity.baseDate = calcBaseDate(entity.baseDate);
        }
        sessionScope(ScheduleEntity.KEY_NAME, entity);
        
        UserInfo userInfo = getUserInfo();
        
        //現在表示しているグループメンバーに対する基準日のスケジュールを取得
        List<Date> viewDateList = new ArrayList<Date>();
        viewDateList.add(entity.baseDate);
        ScheduleSearchService.ScheduleView4Week result = 
                scheduleSearchService.createScheduleView4Week(createTargetMemberKeys(userInfo.getMemberList()), 
                    viewDateList, userInfo.keyToString);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(result);
        jsonResult.setToken(setToken());
        return jsonResult;
    }

    /**
     * 基準日計算.
     * リクエストパラメータの値を元に、基準日を算出します。
     * 当日の場合、システム日付が基準日となります。
     * リフレッシュの場合、引数の基準日をそのまま返却します。
     * 翌日の場合、現在の基準日＋１日
     * 前日の場合、現在の基準日−１日
     * が基準日となります。
     * @param baseDate 基準日
     * @return 算出基準日
     */
    private Date calcBaseDate(Date baseDate) {
        
        String viewType = asString(VIEW_TYPE);
        int moveDate = 0;
        if("today".equals(viewType)) {
            return CurrentDateUtils.getInstance().getCurrentDate();
        } else if("refresh".equals(viewType)) {
            return baseDate;
        } else if("next".equals(viewType)) {
            moveDate = 1;
        } else if("prev".equals(viewType)) {
            moveDate = -1;
        }
        return DateTimeUtils.addDays(baseDate, moveDate);
    }
}
