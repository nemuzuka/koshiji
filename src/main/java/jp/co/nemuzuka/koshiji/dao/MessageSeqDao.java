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

import jp.co.nemuzuka.common.UniqueKey;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.koshiji.meta.MessageSeqModelMeta;
import jp.co.nemuzuka.koshiji.model.MessageSeqModel;
import jp.co.nemuzuka.utils.ConvertUtils;

import org.slim3.datastore.Datastore;

/**
 * MessageSeqModelに対するDao.
 * @author kazumune
 */
public class MessageSeqDao {

	private static MessageSeqDao dao = new MessageSeqDao();
	
	/**
	 * インスタンス取得.
	 * @return インスタンス
	 */
	public static MessageSeqDao getInstance() {
		return dao;
	}
	
	/**
	 * デフォルトコンストラクタ.
	 */
	private MessageSeqDao() {}

	
	/**
	 * Messageシーケンスの最大値を作成します。
	 * @return シーケンスの最大値
	 */
	public Long createMessageSeq() {
		
		Long no = getMaxValue();
		if(no == null) {
			no = 1L;
		} else {
			no = no + 1L;
		}
		
		//存在しないnoが取得されるまで繰り返す
		while(true) {
			if (Datastore.putUniqueValue(UniqueKey.messageSeq.name(), ConvertUtils.toString(no))) {
				break;
			}
			no++;
		}
		
		MessageSeqModel model = new MessageSeqModel();
		model.setNo(no);
		
		//永続化
		GlobalTransaction.transaction.get().getTransaction().put(model);
		return no;
	}
	
	/**
	 * 最大値取得.
	 * 最大値を取得します。
	 * @return 最大値。存在しなければnull
	 */
	private Long getMaxValue() {
		MessageSeqModelMeta e = MessageSeqModelMeta.get();;
		return Datastore.query(e).max(e.no);
	}
}
