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

import java.util.LinkedHashSet;
import java.util.Set;

import org.slim3.datastore.Datastore;

import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.MessageSeqDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.utils.CurrentDateUtils;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MessageSearchServiceの実装クラス.
 * @author kazumune
 */
public class MessageEditServiceImpl implements MessageEditService {

    MessageDao messageDao = MessageDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    MessageSeqDao messageSeqDao = MessageSeqDao.getInstance();
    
    private static MessageEditServiceImpl impl = new MessageEditServiceImpl();
    
    /**
     * インスタンス取得.
     * @return インスタンス
     */
    public static MessageEditServiceImpl getInstance() {
        return impl;
    }
    
    /**
     * デフォルトコンストラクタ.
     */
    private MessageEditServiceImpl(){}

    /* (非 Javadoc)
     * @see jp.co.nemuzuka.koshiji.service.MessageEditService#createMessage(jp.co.nemuzuka.koshiji.service.MessageEditService.CreateParam)
     */
    @Override
    public void createMessage(CreateParam param) {
        //MemberがGroupと関連づいていない場合、処理終了
        if(memberGroupConnDao.isJoinMember(param.createMemberKey, param.groupKey) == false) {
            return;
        }
        Key[] memberKeys = createAddressMemberKeys(param);
        if(memberKeys.length == 0) {
            return;
        }
        
        //メッセージの作成
        Key messageKey = putMessage(param);
        
        //宛先・未読の作成
        createUnreadMessageAddress(messageKey, memberKeys, param.groupKey);
    }
    
    /**
     * メッセージの宛先、未読情報作成.
     * @param messageKey MessageKey
     * @param memberKeys 宛先MemberKey配列
     * @param groupKey GroupKey
     */
    private void createUnreadMessageAddress(Key messageKey, Key[] memberKeys, Key groupKey) {
        for(Key memberKey : memberKeys) {
            MessageAddressModel model = new MessageAddressModel();
            model.setGroupKey(groupKey);
            model.setMemberKey(memberKey);
            model.setMessageKey(messageKey);
            messageAddressDao.put(model);
            
            UnreadMessageModel unreadMessage = new UnreadMessageModel();
            unreadMessage.setMemberKey(memberKey);
            unreadMessage.setMessageKey(messageKey);
            unreadMessageDao.put(unreadMessage);
        }
    }

    /**
     * Message登録.
     * 引数の情報で新規Messageを登録します。
     * @param param 作成パラメータ
     * @return MessageKey
     */
    private Key putMessage(CreateParam param) {
        MessageModel message = new MessageModel();
        message.setBody(new Text(param.body));
        message.setCreateMemberKey(param.createMemberKey);
        message.setGroupKey(param.groupKey);
        message.setLastUpdate(CurrentDateUtils.getInstance().getCurrentDate());
        message.setNo(messageSeqDao.createMessageSeq());
        messageDao.put(message);
        return message.getKey();
    }

    /**
     * 宛先MemberKey配列作成.
     * Messageの宛先になるMemberのKey配列を作成します。
     * @param param 作成パラメータ
     * @return 宛先MemberKey配列(登録する宛先が存在しない場合、サイズ0の配列)
     */
    private Key[] createAddressMemberKeys(CreateParam param) {
        if(param.memberKeyStrings == null || param.memberKeyStrings.length == 0) {
            return new Key[0];
        }
        //グループに紐付く全Memberの情報を取得する
        Set<Key> allMemberKey = memberGroupConnDao.getMemberSet(param.groupKey);
        
        Set<Key> targetMemberKeySet = new LinkedHashSet<Key>();
        for(String targetMemberKey : param.memberKeyStrings) {
            if(TARGET_ALL.equals(targetMemberKey)) {
                //全てのMemberが対象の場合、グループに紐付く全Member
                return allMemberKey.toArray(new Key[0]);
            }
            Key memberKey = Datastore.stringToKey(targetMemberKey);
            if(allMemberKey.contains(memberKey)) {
                targetMemberKeySet.add(memberKey);
            }
        }
        //作成者も宛先に含める
        targetMemberKeySet.add(param.createMemberKey);
        return targetMemberKeySet.toArray(new Key[0]);
    }
}
