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
 * Message削除に関するService
 * @author kazumune
 */
public interface MessageDeleteService {
    
    /**
     * 削除対象Message取得.
     * 削除対象日以前のMessageのKeyを取得します。
     * @param targetDate 削除対象日
     * @return 対象MessageKeyList
     */
    List<Key> getDeleteTarget(Date targetDate);
    
    /**
     * Message削除.
     * 該当Messageを削除します。関連するModelも削除します。
     * @param messageKey 削除対象MessageKey
     */
    void delete(Key messageKey);
}
