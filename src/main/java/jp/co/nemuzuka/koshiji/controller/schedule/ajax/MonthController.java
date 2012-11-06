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

import jp.co.nemuzuka.entity.JsonResult;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.utils.DateTimeUtils;

/**
 * 月次グループスケジュール表示用Controller
 * @author kazumune
 */
public class MonthController extends BaseController {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.controller.JsonController#execute()
     */
    @Override
    protected Object execute() throws Exception {
        ScheduleEntity entity = getScheduleEntity();
        entity.baseYyyyMm = calcBaseYearMonth(entity.baseYyyyMm);
        sessionScope(ScheduleEntity.KEY_NAME, entity);
        
        UserInfo userInfo = getUserInfo();
        
        //基準日から1週間分のスケジュールを取得
        ScheduleSearchService.ScheduleView4Month result = 
                scheduleSearchService.createScheduleView4Month(entity.targetMemberKeyString,
                    entity.baseYyyyMm, userInfo.keyToString);
        
        JsonResult jsonResult = new JsonResult();
        jsonResult.setResult(result);
        jsonResult.setToken(setToken());
        return jsonResult;
    }

    /**
     * 基準年月計算.
     * リクエストパラメータの値を元に、基準年月を算出します。
     * 当月の場合、システム日付が基準年月となります。
     * リフレッシュの場合、引数の基準年月をそのまま返却します。
     * 翌日の場合、現在の基準年月＋１月
     * 前日の場合、現在の基準年月−１月
     * が基準年月となります。
     * @param baseYearMonth 基準年月
     * @return 算出基準年月
     */
    private String calcBaseYearMonth(String baseYearMonth) {
        
        String viewType = asString(VIEW_TYPE);
        int addType = 0;
        if("thisMonth".equals(viewType)) {
            return DateTimeUtils.getMonth();
        } else if("refresh".equals(viewType)) {
            return baseYearMonth;
        } else if("next".equals(viewType)) {
            addType = 1;
        } else if("prev".equals(viewType)) {
            addType = -1;
        }
        return DateTimeUtils.addMonth(baseYearMonth, addType);
    }
}
