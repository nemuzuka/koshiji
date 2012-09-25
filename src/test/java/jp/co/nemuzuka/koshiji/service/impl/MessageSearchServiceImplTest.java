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

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.entity.MessageModelEx;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageSearchService.Result;
import jp.co.nemuzuka.koshiji.service.MessageSearchService.SearchParam;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MessageSearchServiceImplのテストクラス.
 * @author kazumune
 */
public class MessageSearchServiceImplTest extends AppEngineTestCase4HRD {

    MessageSearchServiceImpl service = MessageSearchServiceImpl.getInstance();
    MessageDao messageDao = MessageDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
	List<Key> messageKeyList;
	
    /**
     * getListのテスト.
     */
    @Test
    public void testGetList() {
        createInitData();
        
        //メンバー0に対して未読メッセージの設定がされていること
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), 
                messageKeyList.get(0), messageKeyList.get(1), messageKeyList.get(2)).size(),
            is(3));
        
        //グループ0/メンバー0の検索
        SearchParam param = new SearchParam();
        param.groupKey = groupKeyList.get(0);
        param.memberKey = memberKeyList.get(0);
        param.limit = 3;
        param.pageNo = 1;
        
        Result actual = service.getList(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        assertThat(actual.hasNextPage, is(false));
        List<MessageModelEx> actualList = actual.list;
        assertThat(actualList.size(), is(2));
        
        MessageModelEx actualMessage = actualList.get(0);
        assertThat(actualMessage.getModel().getKey(), is(messageKeyList.get(1)));
        assertThat(actualMessage.getCreateMemberName(), is("name0"));
        assertThat(actualMessage.getLastUpdate(), is("20120101 1234"));
        assertThat(actualMessage.isUnread(), is(true));

        actualMessage = actualList.get(1);
        assertThat(actualMessage.getModel().getKey(), is(messageKeyList.get(0)));
        assertThat(actualMessage.getCreateMemberName(), is("name0"));
        assertThat(actualMessage.getLastUpdate(), is("20120101 0123"));
        assertThat(actualMessage.isUnread(), is(true));
        
        //グループ0/メンバー0のメッセージに対する未読が削除されていること
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), messageKeyList.get(0), messageKeyList.get(1)).size(),
            is(0));
        //指定したグループ以外(グループ1)での未読メッセージは削除されていないこと
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), messageKeyList.get(2)).size(),
            is(1));
    }

    /**
     * getListのテスト.
     */
    @Test
    public void testGetList2() {
        createInitData();
        
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(1), messageKeyList.get(0), messageKeyList.get(1)).size(),
            is(1));
        
        //グループ0/メンバー1の検索
        SearchParam param = new SearchParam();
        param.groupKey = groupKeyList.get(0);
        param.memberKey = memberKeyList.get(1);
        param.limit = 2;
        param.pageNo = 1;
        
        Result actual = service.getList(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        assertThat(actual.hasNextPage, is(false));
        List<MessageModelEx> actualList = actual.list;
        assertThat(actualList.size(), is(2));
        
        MessageModelEx actualMessage = actualList.get(0);
        assertThat(actualMessage.getModel().getKey(), is(messageKeyList.get(1)));
        assertThat(actualMessage.getCreateMemberName(), is("name0"));
        assertThat(actualMessage.getLastUpdate(), is("20120101 1234"));
        assertThat(actualMessage.isUnread(), is(false));

        actualMessage = actualList.get(1);
        assertThat(actualMessage.getModel().getKey(), is(messageKeyList.get(0)));
        assertThat(actualMessage.getCreateMemberName(), is("name0"));
        assertThat(actualMessage.getLastUpdate(), is("20120101 0123"));
        assertThat(actualMessage.isUnread(), is(true));
        
        //メンバー1の未読が削除されていること
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(1), messageKeyList.get(0), messageKeyList.get(1)).size(),
            is(0));
    }

    /**
     * getListのテスト.
     * 表示対象のListに含まれないMessageの未読は削除されないこと
     */
    @Test
    public void testGetList3() {
        createInitData();
        
        //メンバー0に対して未読メッセージの設定がされていること
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), 
                messageKeyList.get(0), messageKeyList.get(1), messageKeyList.get(2)).size(),
            is(3));
        
        //グループ0/メンバー0の検索
        SearchParam param = new SearchParam();
        param.groupKey = groupKeyList.get(0);
        param.memberKey = memberKeyList.get(0);
        param.limit = 1;
        param.pageNo = 1;
        
        Result actual = service.getList(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        assertThat(actual.hasNextPage, is(true));
        List<MessageModelEx> actualList = actual.list;
        assertThat(actualList.size(), is(1));
        assertThat(actualList.get(0).getModel().getKey(), is(messageKeyList.get(1)));
        
        //グループ0/メンバー0の表示対象メッセージに対する未読が削除されていること
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), messageKeyList.get(1)).size(),
            is(0));
        //表示対象でない or 指定したグループ以外(グループ1)での未読メッセージは削除されていないこと
        assertThat(
            unreadMessageDao.getList(memberKeyList.get(0), 
                messageKeyList.get(0), messageKeyList.get(2)).size(),
            is(2));
    }

    /**
     * getListのテスト.
     * 指定したメンバーがグループと紐付いていない場合
     */
    @Test
    public void testGetList4() {
        createInitData();
        
        //グループ1/メンバー1の検索(紐付いていない)
        SearchParam param = new SearchParam();
        param.groupKey = groupKeyList.get(1);
        param.memberKey = memberKeyList.get(1);
        param.limit = 1;
        param.pageNo = 1;
        
        Result actual = service.getList(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        assertThat(actual.hasNextPage, is(false));
        List<MessageModelEx> actualList = actual.list;
        assertThat(actualList.size(), is(0));
    }
    
	/**
	 * 事前データ作成.
	 * ユーザを4人作成します。
	 * グループを2つ作成します。
	 * ユーザとグループの関連を付与します。
     * グループ0/メンバー0
     * グループ0/メンバー1
     * グループ1/メンバー0
     * グループ1/メンバー2
     * グループ1/メンバー3
	 */
	private void createInitData() {
		memberKeyList = new ArrayList<Key>();
		for(int i = 0; i < 4; i++) {
			MemberModel model = new MemberModel();
			model.setMail("hoge" + i + "@gmail.com");
			model.setName("name" + i);
			model.setMemo(new Text("メモですよん"));
            model.setTimeZone(TimeZone.GMT_P_9.getCode());

			memberDao.put(model);
			memberKeyList.add(model.getKey());
			
			GlobalTransaction.transaction.get().commit();
			GlobalTransaction.transaction.get().begin();
		}
		
        groupKeyList = new ArrayList<Key>();
		for(int i = 0; i < 2; i++) {
	        GroupModel groupModel = new GroupModel();
	        groupModel.setGroupName("グループ" + i);
	        
	        groupDao.put(groupModel);
	        groupKeyList.add(groupModel.getKey());

            GlobalTransaction.transaction.get().commit();
            GlobalTransaction.transaction.get().begin();
		}
		
		//グループ0/メンバー0
		//グループ0/メンバー1
		//グループ1/メンバー0
		//グループ1/メンバー2
		//グループ1/メンバー3
		MemberGroupConnModel memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
		memberGroupConnModel.setMemberKey(memberKeyList.get(0));
		memberGroupConnModel.setSortNum(0L);
		memberGroupConnDao.put(memberGroupConnModel);
		
		memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(0));
        memberGroupConnModel.setMemberKey(memberKeyList.get(1));
        memberGroupConnModel.setSortNum(1L);
        memberGroupConnDao.put(memberGroupConnModel);		

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(0));
        memberGroupConnModel.setSortNum(2L);
        memberGroupConnDao.put(memberGroupConnModel);       

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(2));
        memberGroupConnModel.setSortNum(3L);
        memberGroupConnDao.put(memberGroupConnModel);       

        memberGroupConnModel = new MemberGroupConnModel();
        memberGroupConnModel.setGroupKey(groupKeyList.get(1));
        memberGroupConnModel.setMemberKey(memberKeyList.get(3));
        memberGroupConnModel.setSortNum(4L);
        memberGroupConnDao.put(memberGroupConnModel);       
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //Messageを登録する
        messageKeyList = new ArrayList<Key>();
        createMessage1();
        createMessage2();
        createMessage3();
	}

	
	/**
	 * Message登録.
	 * グループ0/メンバー0が作成した
	 * メンバー0/メンバー1宛のメッセージ
	 * 全員未読状態
	 */
	private void createMessage1() {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmmss");
        Date date = null;
        try {
            date = sdf.parse("20120101 012345");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        MessageModel message = new MessageModel();
        message.setGroupKey(groupKeyList.get(0));
        message.setCreateMemberKey(memberKeyList.get(0));
        message.setLastUpdate(date);
        message.setNo(1L);
        message.setBody(new Text("メッセージんぐ"));
        messageDao.put(message);
        messageKeyList.add(message.getKey());
        
        MessageAddressModel messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(0));
        messageAddress.setMemberKey(memberKeyList.get(0));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);
        
        messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(0));
        messageAddress.setMemberKey(memberKeyList.get(1));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);
        
        UnreadMessageModel unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(0));
        unreadMessageDao.put(unreadMessage);
        
        unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(1));
        unreadMessageDao.put(unreadMessage);

        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
    }

   /**
     * Message登録.
     * グループ0/メンバー1が作成した
     * メンバー0/メンバー1宛のメッセージ
     * メンバー1は、既読状態
     */
    private void createMessage2() {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmmss");
        Date date = null;
        try {
            date = sdf.parse("20120101 123456");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        MessageModel message = new MessageModel();
        message.setGroupKey(groupKeyList.get(0));
        message.setCreateMemberKey(memberKeyList.get(0));
        message.setLastUpdate(date);
        message.setNo(1L);
        message.setBody(new Text("メッセージんぐー！"));
        messageDao.put(message);
        messageKeyList.add(message.getKey());
        
        MessageAddressModel messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(0));
        messageAddress.setMemberKey(memberKeyList.get(0));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);
        
        messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(0));
        messageAddress.setMemberKey(memberKeyList.get(1));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);
        
        UnreadMessageModel unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(0));
        unreadMessageDao.put(unreadMessage);
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
    }

    /**
     * Message登録.
     * グループ1/メンバー0が作成した
     * メンバー0/メンバー2/メンバー3宛のメッセージ
     */
    private void createMessage3() {
        SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmmss");
        Date date = null;
        try {
            date = sdf.parse("20120201 123456");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        MessageModel message = new MessageModel();
        message.setGroupKey(groupKeyList.get(1));
        message.setCreateMemberKey(memberKeyList.get(0));
        message.setLastUpdate(date);
        message.setNo(1L);
        message.setBody(new Text("うほほー"));
        messageDao.put(message);
        messageKeyList.add(message.getKey());
        
        MessageAddressModel messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(1));
        messageAddress.setMemberKey(memberKeyList.get(0));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);
        
        messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(1));
        messageAddress.setMemberKey(memberKeyList.get(2));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);

        messageAddress = new MessageAddressModel();
        messageAddress.setGroupKey(groupKeyList.get(1));
        messageAddress.setMemberKey(memberKeyList.get(3));
        messageAddress.setMessageKey(message.getKey());
        messageAddressDao.put(messageAddress);

        UnreadMessageModel unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(0));
        unreadMessageDao.put(unreadMessage);

        unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(2));
        unreadMessageDao.put(unreadMessage);

        unreadMessage = new UnreadMessageModel();
        unreadMessage.setMessageKey(message.getKey());
        unreadMessage.setMemberKey(memberKeyList.get(3));
        unreadMessageDao.put(unreadMessage);

        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
    }

    /* (非 Javadoc)
	 * @see org.slim3.tester.AppEngineTestCase#setUp()
	 */
	@Override
	public void setUp() throws Exception {
		super.setUp();
		TransactionEntity transactionEntity = new TransactionEntity();
		GlobalTransaction.transaction.set(transactionEntity);
	}
}
