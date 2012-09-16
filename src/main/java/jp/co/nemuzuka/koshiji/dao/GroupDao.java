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
package jp.co.nemuzuka.koshiji.dao;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.GroupModelMeta;
import jp.co.nemuzuka.koshiji.model.GroupModel;

import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * GroupModelに対するDao.
 * @author kazumune
 */
public class GroupDao extends AbsDao {

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelMeta()
     */
    @SuppressWarnings("rawtypes")
    @Override
    ModelMeta getModelMeta() {
        return GroupModelMeta.get();
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.dao.AbsDao#getModelClass()
     */
    @SuppressWarnings("rawtypes")
    @Override
    Class getModelClass() {
        return GroupModel.class;
    }
    
    private static GroupDao dao = new GroupDao();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static GroupDao getInstance() {
        return dao;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private GroupDao(){}

    /**
     * グループ一覧取得.
     * 指定したKeyに紐付くグループ一覧を取得します。
     * Keyが未指定の場合、size0のListを返却します
     * @param keys Key配列
     * @return 該当レコード
     */
    public List<GroupModel> getList(Key...keys) {
        GroupModelMeta e = (GroupModelMeta) getModelMeta();
        Set<FilterCriterion> filter = new HashSet<FilterCriterion>();
        if(keys != null && keys.length != 0) {
            filter.add(e.key.in(keys));
        } else {
            return new ArrayList<GroupModel>();
        }
        return getList(filter, null, e.key.asc);
    }
}
