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

import com.google.appengine.api.datastore.Key;

/**
 * MemberGroupConnに関するService
 * @author kazumune
 */
public interface MemberGroupConnService {

    /**
     * MemberGroup関連追加.
     * @param memberKey MemberKey
     * @param groupKey GroupKey
     * @param admin 管理者の場合、true
     * @return 処理対象Key(既に登録されている場合、null)
     */
    Key put(Key memberKey, Key groupKey, boolean admin);
}
