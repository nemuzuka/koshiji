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

import java.util.Date;
import java.util.List;

import com.google.appengine.api.datastore.Key;

/**
 * Schedule削除に関するService
 * 本Serviceはbackends経由で呼び出されることを想定しています。
 * @author kazumune
 */
public interface ScheduleDeleteService {
    
    /**
     * 削除対象Schedule取得.
     * Scheduleの終了日が削除対象日以前のKeyを取得します。
     * @param targetDate 削除対象日
     * @return 対象ScheduleKeyList
     */
    List<Key> getDeleteTarget(Date targetDate);
    
    /**
     * Schedule削除.
     * 該当Scheduleを削除します。関連するModelも削除します。
     * @param scheduleKey 削除対象ScheduleKey
     */
    void delete(Key scheduleKey);

}
