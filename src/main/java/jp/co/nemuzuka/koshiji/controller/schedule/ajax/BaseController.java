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

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.controller.JsonController;
import jp.co.nemuzuka.entity.LabelValueBean;
import jp.co.nemuzuka.koshiji.entity.ScheduleEntity;
import jp.co.nemuzuka.koshiji.service.ScheduleSearchService;
import jp.co.nemuzuka.koshiji.service.impl.ScheduleSearchServiceImpl;

/**
 * グループスケジュール表示用基底Controller
 * @author kazumune
 */
public abstract class BaseController extends JsonController {

    //リクエストパラメータ名.
    /** 日付指定時. */
    protected static final String APPOINTMENT_DATE = "appointmentDate";
    /** 移動方向. */
    protected static final String VIEW_TYPE = "viewType";
    /** 移動日数. */
    protected static final String AMOUNT_TYPE = "amountType";
    
    protected ScheduleSearchService scheduleSearchService = ScheduleSearchServiceImpl.getInstance();
    
    /**
     * ScheduleEntity取得.
     * Sessionに格納されているScheduleEntityを取得します。
     * @return Session格納情報（存在しない場合、newしたインスタンス）
     */
    protected ScheduleEntity getScheduleEntity() {
        ScheduleEntity entity = sessionScope(ScheduleEntity.KEY_NAME);
        if(entity == null) {
            entity = new ScheduleEntity();
        }
        return entity;
    }
    
    /**
     * 表示対象MemberKeySet作成
     * @param memberList 選択グループに紐付くMemberList
     * @return MemberKeySet
     */
    protected Set<String> createTargetMemberKeys(List<LabelValueBean> memberList) {
        Set<String> ret = new LinkedHashSet<String>();
        for(LabelValueBean target : memberList) {
            ret.add(target.getValue());
        }
        return ret;
    }
}
