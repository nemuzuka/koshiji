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

import jp.co.nemuzuka.koshiji.dao.CommentDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.service.MessageDeleteService;

import com.google.appengine.api.datastore.Key;

/**
 * MessageDeleteServiceの実装クラス.
 * @author kazumune
 */
public class MessageDeleteServiceImpl implements MessageDeleteService {

    MessageDao messageDao = MessageDao.getInstance();
    CommentDao commentDao = CommentDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    
    private static MessageDeleteServiceImpl impl = new MessageDeleteServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageDeleteServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageDeleteServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageDeleteService#getDeleteTarget(java.util.Date)
     */
    @Override
    public List<Key> getDeleteTarget(Date targetDate) {
        return messageDao.getKeyList(targetDate);
    }

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageDeleteService#delete(com.google.appengine.api.datastore.Key)
     */
    @Override
    public void delete(Key messageKey) {
        MessageModel message = messageDao.get(messageKey);
        if(message == null) {
            return;
        }
        
        //Messageを削除
        messageDao.delete(messageKey);
        
        //Commentを削除
        commentDao.delete4MessageKey(messageKey);
        
        //MessageAddressを削除
        messageAddressDao.delete4MessageKey(messageKey, message.getGroupKey());
        
        //UnreadMessageを削除
        unreadMessageDao.delete4MessageKey(messageKey);
    }
}
