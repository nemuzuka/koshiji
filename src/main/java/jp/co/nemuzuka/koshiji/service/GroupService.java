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

import java.util.Map;

import jp.co.nemuzuka.koshiji.model.GroupModel;

import com.google.appengine.api.datastore.Key;

/**
 * Groupに関するService
 * @author kazumune
 */
public interface GroupService {
    
    /**
     * グループ追加.
     * グループを追加します。
     * @param groupName グループ名
     * @return 処理対象Key
     */
    Key put(String groupName);
    
    /**
     * Map取得.
     * 指定したKey配列に合致するデータを取得します。
     * Keyが未指定の場合、size0のListを返却します
     * @param keys key配列
     * @return 該当Map
     */
    Map<Key, GroupModel> getMap(Key...keys);
}
