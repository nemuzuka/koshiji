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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.nemuzuka.koshiji.meta.MemberModelMeta;
import jp.co.nemuzuka.koshiji.model.MemberModel;

import org.apache.commons.lang.StringUtils;
import org.slim3.datastore.FilterCriterion;
import org.slim3.datastore.ModelMeta;

import com.google.appengine.api.datastore.Key;

/**
 * MemberModelに対するDao.
 * @author kazumune
 */
public class MemberDao extends AbsDao {

	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.dao.AbsDao#getModel()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	ModelMeta getModelMeta() {
		return MemberModelMeta.get();
	}

	/* (非 Javadoc)
	 * @see jp.co.nemuzuka.dao.AbsDao#getModelClass()
	 */
	@SuppressWarnings({ "rawtypes" })
	@Override
	Class getModelClass() {
		return MemberModel.class;
	}

	private static MemberDao dao = new MemberDao();
	
	/**
	 * インスタンス取得.
	 * @return インスタンス
	 */
	public static MemberDao getInstance() {
		return dao;
	}
	
	/**
	 * デフォルトコンストラクタ.
	 */
	private MemberDao() {}

	/**
	 * List取得.
	 * メールアドレスに合致するListを取得します。
	 * @param mail メール(完全一致)
	 * @return 該当レコード
	 */
	public List<MemberModel> getList(String mail) {
		MemberModelMeta e = (MemberModelMeta) getModelMeta();
		Set<FilterCriterion> filterSet = new HashSet<FilterCriterion>();
		if(StringUtils.isNotEmpty(mail)) {
			filterSet.add(e.mail.equal(mail));
		} else {
		    return new ArrayList<MemberModel>();
		}
		return getList(filterSet, null, e.key.asc);
	}
	
	/**
	 * Map取得.
	 * 指定したKey配列に合致するデータを取得します。
     * Keyが未指定の場合、size0のListを返却します
	 * @param keys key配列
	 * @return 該当Map
	 */
	public Map<Key, MemberModel> getMap(Key...keys) {
		Map<Key, MemberModel> map = new LinkedHashMap<Key, MemberModel>();
		List<MemberModel> list = getList(keys);
		for(MemberModel target : list) {
			map.put(target.getKey(), target);
		}
		return map;
	}
	
	/**
	 * List取得.
	 * 指定したKey配列に合致するデータを取得します。
	 * Keyが未指定の場合、size0のListを返却します
	 * @param keys Key配列
	 * @return 該当Map
	 */
	public List<MemberModel> getList(Key...keys) {
		MemberModelMeta e = (MemberModelMeta) getModelMeta();
		Set<FilterCriterion> filterSet = new HashSet<FilterCriterion>();
		if(keys != null && keys.length != 0) {
			filterSet.add(e.key.in(keys));
		} else {
			return new ArrayList<MemberModel>();
		}
		return getList(filterSet, null, e.key.asc);
	}
	
	/* (non-Javadoc)
	 * @see jp.co.nemuzuka.dao.AbsDao#getAllList()
	 */
	@SuppressWarnings("unchecked")
	public List<MemberModel> getAllList() {
		MemberModelMeta e = (MemberModelMeta) getModelMeta();
		List<MemberModel> list = super.getAllList(e.key.asc);
		return list;
	}
}
