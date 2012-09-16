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

import com.google.appengine.api.datastore.Key;

import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.service.GroupService;

/**
 * GroupServiceの実装クラス.
 * @author kazumune
 */
public class GroupServiceImpl implements GroupService {

    GroupDao groupDao = GroupDao.getInstance();
    
    private static GroupServiceImpl impl = new GroupServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static GroupServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private GroupServiceImpl(){}
    
    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.GroupService#put(java.lang.String)
     */
    @Override
    public Key put(String groupName) {
        GroupModel model = new GroupModel();
        model.setGroupName(groupName);
        groupDao.put(model);
        return model.getKey();
    }

}
