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

import java.util.Date;
import java.util.List;

import jp.co.nemuzuka.koshiji.dao.ScheduleDao;
import jp.co.nemuzuka.koshiji.model.ScheduleModel;
import jp.co.nemuzuka.koshiji.service.ScheduleDeleteService;

import com.google.appengine.api.datastore.Key;

/**
 * ScheduleDeleteServiceの実装クラス.
 * @author kazumune
 */
public class ScheduleDeleteServiceImpl implements ScheduleDeleteService {

    ScheduleDao scheduleDao = ScheduleDao.getInstance();
    
    private static ScheduleDeleteServiceImpl impl = new ScheduleDeleteServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static ScheduleDeleteServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private ScheduleDeleteServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleDeleteService#getDeleteTarget(java.util.Date)
     */
    @Override
    public List<Key> getDeleteTarget(Date targetDate) {
        return scheduleDao.getKeyList(targetDate);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.ScheduleDeleteService#delete(com.google.appengine.api.datastore.Key)
     */
    @Override
    public void delete(Key scheduleKey) {
        ScheduleModel schedule = scheduleDao.get(scheduleKey);
        if(schedule == null) {
            return;
        }
        scheduleDao.delete(scheduleKey);
    }
}
