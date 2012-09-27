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

import java.util.List;

import jp.co.nemuzuka.entity.LabelValueBean;

import com.google.appengine.api.datastore.Key;

/**
 * グループに紐付くメンバーに関するService
 * @author kazumune
 */
public interface GroupMemberService {
    
    /**
     * グループに紐付くMemberList取得.
     * @param groupKey グループKey
     * @return グループに紐付くメンバーList
     */
    List<LabelValueBean> getMemberList(Key groupKey);
}
