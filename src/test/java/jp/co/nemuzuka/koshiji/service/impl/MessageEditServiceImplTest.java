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

import java.util.ArrayList;
import java.util.List;

import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.koshiji.dao.CommentDao;
import jp.co.nemuzuka.koshiji.dao.GroupDao;
import jp.co.nemuzuka.koshiji.dao.MemberDao;
import jp.co.nemuzuka.koshiji.dao.MemberGroupConnDao;
import jp.co.nemuzuka.koshiji.dao.MessageAddressDao;
import jp.co.nemuzuka.koshiji.dao.MessageDao;
import jp.co.nemuzuka.koshiji.dao.UnreadMessageDao;
import jp.co.nemuzuka.koshiji.model.CommentModel;
import jp.co.nemuzuka.koshiji.model.GroupModel;
import jp.co.nemuzuka.koshiji.model.MemberGroupConnModel;
import jp.co.nemuzuka.koshiji.model.MemberModel;
import jp.co.nemuzuka.koshiji.model.MessageAddressModel;
import jp.co.nemuzuka.koshiji.model.MessageModel;
import jp.co.nemuzuka.koshiji.model.UnreadMessageModel;
import jp.co.nemuzuka.koshiji.service.MessageEditService;
import jp.co.nemuzuka.koshiji.service.MessageEditService.CreateParam;
import jp.co.nemuzuka.tester.AppEngineTestCase4HRD;

import org.junit.Test;
import org.slim3.datastore.Datastore;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

/**
 * MessageEditServiceImplのテストクラス.
 * @author kazumune
 */
public class MessageEditServiceImplTest extends AppEngineTestCase4HRD {

    MessageEditServiceImpl service = MessageEditServiceImpl.getInstance();
    MessageDao messageDao = MessageDao.getInstance();
    MessageAddressDao messageAddressDao = MessageAddressDao.getInstance();
    MemberGroupConnDao memberGroupConnDao = MemberGroupConnDao.getInstance();
    UnreadMessageDao unreadMessageDao = UnreadMessageDao.getInstance();
    MemberDao memberDao = MemberDao.getInstance();
	GroupDao groupDao = GroupDao.getInstance();
	CommentDao commentDao = CommentDao.getInstance();
    
	List<Key> memberKeyList;
	List<Key> groupKeyList;
    
	/**
	 * createMessageのテスト.
	 * グループ0/メンバー1が全員に当てたメッセージ
	 */
	@Test
	public void testCreateMessage() {
	    createInitData();
	    
	    CreateParam param = new CreateParam();
	    param.body = "ほえほえほえ";
	    param.createMemberKey = memberKeyList.get(1);
	    param.groupKey = groupKeyList.get(0);
	    param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
	    
	    service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
	    
        //Messageの確認
	    List<MessageModel> actualMessages = messageDao.getAllList();
	    assertThat(actualMessages.size(), is(1));
	    MessageModel actualMessage = actualMessages.get(0);
	    assertThat(actualMessage.getBody().getValue(), is("ほえほえほえ"));
	    assertThat(actualMessage.getCreateMemberKey(), is(memberKeyList.get(1)));
	    assertThat(actualMessage.getGroupKey(), is(groupKeyList.get(0)));
        
	    //宛先の確認
	    List<MessageAddressModel> actualMessageAddresses = messageAddressDao.getAllList();
	    assertThat(actualMessageAddresses.size(), is(2));
	    MessageAddressModel actualMessageAddress = actualMessageAddresses.get(0);
	    assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(0)));
	    assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(0)));
        assertThat(actualMessageAddress.getMessageKey(), is(actualMessage.getKey()));
        
        actualMessageAddress = actualMessageAddresses.get(1);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(0)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(1)));
        assertThat(actualMessageAddress.getMessageKey(), is(actualMessage.getKey()));        
        
        //未読の確認
        //作成者は未読Messageとして登録しない
        List<UnreadMessageModel> actualUnreadMessagees = unreadMessageDao.getAllList();
        assertThat(actualUnreadMessagees.size(), is(1));
        UnreadMessageModel actualUnreadMessage = actualUnreadMessagees.get(0);
        assertThat(actualUnreadMessage.getMemberKey(), is(memberKeyList.get(0)));
        assertThat(actualUnreadMessage.getMessageKey(), is(actualMessage.getKey()));
	}

    /**
     * createMessageのテスト.
     * グループ1/メンバー0がメンバー2に当てたメッセージ
     */
    @Test
    public void testCreateMessage2() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(0);
        param.groupKey = groupKeyList.get(1);
        param.memberKeyStrings = new String[]{Datastore.keyToString(memberKeyList.get(2))};
        
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //Messageの確認
        List<MessageModel> actualMessages = messageDao.getAllList();
        assertThat(actualMessages.size(), is(1));
        MessageModel actualMessage = actualMessages.get(0);
        assertThat(actualMessage.getBody().getValue(), is("ほえほえほえ"));
        assertThat(actualMessage.getCreateMemberKey(), is(memberKeyList.get(0)));
        assertThat(actualMessage.getGroupKey(), is(groupKeyList.get(1)));
        
        //宛先の確認
        List<MessageAddressModel> actualMessageAddresses = messageAddressDao.getAllList();
        assertThat(actualMessageAddresses.size(), is(2));
        MessageAddressModel actualMessageAddress = actualMessageAddresses.get(0);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(1)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(2)));
        assertThat(actualMessageAddress.getMessageKey(), is(actualMessage.getKey()));
        
        actualMessageAddress = actualMessageAddresses.get(1);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(1)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(0)));
        assertThat(actualMessageAddress.getMessageKey(), is(actualMessage.getKey()));        
        
        //未読の確認
        List<UnreadMessageModel> actualUnreadMessagees = unreadMessageDao.getAllList();
        assertThat(actualUnreadMessagees.size(), is(1));
        UnreadMessageModel actualUnreadMessage = actualUnreadMessagees.get(0);
        assertThat(actualUnreadMessage.getMemberKey(), is(memberKeyList.get(2)));
        assertThat(actualUnreadMessage.getMessageKey(), is(actualMessage.getKey()));
    }

    /**
     * createMessageのテスト.
     * グループ1/メンバー0がメンバー1,メンバー2に当てたメッセージ
     */
    @Test
    public void testCreateMessage3() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(0);
        param.groupKey = groupKeyList.get(1);
        param.memberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(1)),
            Datastore.keyToString(memberKeyList.get(2))
            };
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //宛先の確認
        //メンバー1は指定グループに含まれない
        List<MessageAddressModel> actualMessageAddresses = messageAddressDao.getAllList();
        assertThat(actualMessageAddresses.size(), is(2));
        MessageAddressModel actualMessageAddress = actualMessageAddresses.get(0);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(1)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(2)));
        
        actualMessageAddress = actualMessageAddresses.get(1);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(1)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(0)));
        
        //未読の確認
        List<UnreadMessageModel> actualUnreadMessagees = unreadMessageDao.getAllList();
        assertThat(actualUnreadMessagees.size(), is(1));
        UnreadMessageModel actualUnreadMessage = actualUnreadMessagees.get(0);
        assertThat(actualUnreadMessage.getMemberKey(), is(memberKeyList.get(2)));
    }

    /**
     * createMessageのテスト.
     * グループ1/メンバー1がメンバー2に当てたメッセージ
     */
    @Test
    public void testCreateMessage4() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(1);
        param.memberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(2))
            };
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //登録メンバーがグループに関連づいていないので、登録されない
        List<MessageModel> actualMessages = messageDao.getAllList();
        assertThat(actualMessages.size(), is(0));
    }

    /**
     * createMessageのテスト.
     * グループ0/メンバー0で宛先未指定
     */
    @Test
    public void testCreateMessage5() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(0);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{};
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //宛先が未設定なので、未登録
        List<MessageModel> actualMessages = messageDao.getAllList();
        assertThat(actualMessages.size(), is(0));
    }

    /**
     * createCommentのテスト.
     * グループ0/メンバー1が全員に当てたメッセージに対してメンバー0がコメント登録
     */
    @Test
    public void testCreateComment() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
        
        service.createMessage(param);
        
        //未読を削除
        List<UnreadMessageModel> unreadMessagees = unreadMessageDao.getAllList();
        for(UnreadMessageModel target : unreadMessagees) {
            unreadMessageDao.delete(target.getKey());
        }
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> actualMessages = messageDao.getAllList();
        
        MessageEditService.CreateCommentParam commentParam = new MessageEditService.CreateCommentParam();
        commentParam.body = "コメントですよん";
        commentParam.createMemberKey = memberKeyList.get(0);
        commentParam.groupKey = groupKeyList.get(0);
        commentParam.messageKey = actualMessages.get(0).getKey();
        service.createComment(commentParam);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(1));
        CommentModel actualComment = actualComments.get(0);
        assertThat(actualComment.getBody().getValue(), is("コメントですよん"));
        assertThat(actualComment.getCreateMemberKey(), is(commentParam.createMemberKey));
        assertThat(actualComment.getMessageKey(), is(commentParam.messageKey));
        
        //未読の確認
        //自分は未読として登録されない
        List<UnreadMessageModel> actualUnreadMessagees = unreadMessageDao.getAllList();
        assertThat(actualUnreadMessagees.size(), is(1));
        UnreadMessageModel actualUnreadMessage = actualUnreadMessagees.get(0);
        assertThat(actualUnreadMessage.getMemberKey(), is(memberKeyList.get(1)));
        assertThat(actualUnreadMessage.getMessageKey(), is(commentParam.messageKey));
        
    }

    /**
     * createCommentのテスト.
     * グループ0/メンバー1が全員に当てたメッセージに対して
     * グループ0に紐付かないメンバー2がコメント登録
     */
    @Test
    public void testCreateComment2() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
        
        service.createMessage(param);
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> actualMessages = messageDao.getAllList();
        
        MessageEditService.CreateCommentParam commentParam = new MessageEditService.CreateCommentParam();
        commentParam.body = "コメントですよん";
        commentParam.createMemberKey = memberKeyList.get(2);
        commentParam.groupKey = groupKeyList.get(0);
        commentParam.messageKey = actualMessages.get(0).getKey();
        service.createComment(commentParam);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(0));
    }
    
    /**
     * createCommentのテスト.
     * グループ1/メンバー0がメンバー0に当てたメッセージに対して
     * 宛先に紐付かないメンバー2がコメント登録
     */
    @Test
    public void testCreateComment3() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(0);
        param.groupKey = groupKeyList.get(1);
        param.memberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(0))
            };
        
        service.createMessage(param);
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> actualMessages = messageDao.getAllList();
        
        MessageEditService.CreateCommentParam commentParam = new MessageEditService.CreateCommentParam();
        commentParam.body = "コメントですよん";
        commentParam.createMemberKey = memberKeyList.get(2);
        commentParam.groupKey = groupKeyList.get(1);
        commentParam.messageKey = actualMessages.get(0).getKey();
        service.createComment(commentParam);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(0));
    }
    
    /**
     * createCommentのテスト.
     * グループ1/メンバー0がメンバー0に当てたメッセージに対して
     * グループ0/メンバー0がコメント登録
     */
    @Test
    public void testCreateComment4() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(0);
        param.groupKey = groupKeyList.get(1);
        param.memberKeyStrings = new String[]{
            Datastore.keyToString(memberKeyList.get(0))
            };
        
        service.createMessage(param);
        
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> actualMessages = messageDao.getAllList();
        
        MessageEditService.CreateCommentParam commentParam = new MessageEditService.CreateCommentParam();
        commentParam.body = "コメントですよん";
        commentParam.createMemberKey = memberKeyList.get(0);
        commentParam.groupKey = groupKeyList.get(0);
        commentParam.messageKey = actualMessages.get(0).getKey();
        service.createComment(commentParam);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(0));
    }
    
    /**
     * deleteAddressのテスト.
     * グループ0/メンバー1が全員に当てたメッセージをメンバー1が削除
     */
    @Test
    public void testDeleteAddress() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
        
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> messages = messageDao.getAllList();
        Key massageKey = messages.get(0).getKey();
        
        //Messageを削除
        service.deleteAddress(massageKey, memberKeyList.get(1), groupKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //宛先の確認
        List<MessageAddressModel> actualMessageAddresses = messageAddressDao.getAllList();
        assertThat(actualMessageAddresses.size(), is(0));
    }

    /**
     * deleteAddressのテスト.
     * グループ0/メンバー1が全員に当てたメッセージをメンバー0が削除
     */
    @Test
    public void testDeleteAddress2() {
        createInitData();
        
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
        
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<MessageModel> messages = messageDao.getAllList();
        Key massageKey = messages.get(0).getKey();
        
        //Messageを削除
        service.deleteAddress(massageKey, memberKeyList.get(0), groupKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //宛先の確認
        List<MessageAddressModel> actualMessageAddresses = messageAddressDao.getAllList();
        assertThat(actualMessageAddresses.size(), is(1));
        MessageAddressModel actualMessageAddress = actualMessageAddresses.get(0);
        assertThat(actualMessageAddress.getGroupKey(), is(groupKeyList.get(0)));
        assertThat(actualMessageAddress.getMemberKey(), is(memberKeyList.get(1)));
    }

    /**
     * deleteCommentのテスト.
     * グループ0/メンバー1が全員に当てたメッセージに対してメンバー0がコメント登録し、メンバー0が削除処理
     */
    @Test
    public void testDeleteComment() {
        Key[] keys = cretaeDeleteCommentInitData();
        
        //コメントの削除
        service.deleteComment(keys[0], keys[1], memberKeyList.get(0));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        //本文が固定文言で上書きされていることの確認
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(1));
        CommentModel actualComment = actualComments.get(0);
        assertThat(actualComment.getBody().getValue(), is("deleted."));
    }
    
    /**
     * deleteCommentのテスト.
     * グループ0/メンバー1が全員に当てたメッセージに対してメンバー0がコメント登録し、メンバー2が削除処理
     */
    @Test
    public void testDeleteComment2() {
        Key[] keys = cretaeDeleteCommentInitData();
        
        //コメントの削除
        service.deleteComment(keys[0], keys[1], memberKeyList.get(2));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        //本文が固定文言で上書きされていないことの確認
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(1));
        CommentModel actualComment = actualComments.get(0);
        assertThat(actualComment.getBody().getValue(), is("コメントですよん"));
    }

    /**
     * deleteCommentのテスト.
     * グループ0/メンバー1が全員に当てたメッセージに対してメンバー0がコメント登録し、メンバー1が削除処理
     */
    @Test
    public void testDeleteComment3() {
        Key[] keys = cretaeDeleteCommentInitData();
        
        //コメントの削除
        service.deleteComment(keys[0], keys[1], memberKeyList.get(1));
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        //本文が固定文言で上書きされていないことの確認
        List<CommentModel> actualComments = commentDao.getAllList();
        assertThat(actualComments.size(), is(1));
        CommentModel actualComment = actualComments.get(0);
        assertThat(actualComment.getBody().getValue(), is("deleted."));
    }
    
    /**
     * deleteComment用テストデータ作成.
     * グループ0/メンバー1が全員に当てたメッセージに対してメンバー0がコメント登録した状態にします。
     * @return index 0: MessageKey, index 1:CommentKey
     */
    private Key[] cretaeDeleteCommentInitData() {
        createInitData();
        
        //Messageの登録
        CreateParam param = new CreateParam();
        param.body = "ほえほえほえ";
        param.createMemberKey = memberKeyList.get(1);
        param.groupKey = groupKeyList.get(0);
        param.memberKeyStrings = new String[]{MessageEditService.TARGET_ALL};
        service.createMessage(param);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        
        //コメントの登録
        List<MessageModel> actualMessages = messageDao.getAllList();
        MessageEditService.CreateCommentParam commentParam = new MessageEditService.CreateCommentParam();
        commentParam.body = "コメントですよん";
        commentParam.createMemberKey = memberKeyList.get(0);
        commentParam.groupKey = groupKeyList.get(0);
        commentParam.messageKey = actualMessages.get(0).getKey();
        service.createComment(commentParam);
        GlobalTransaction.transaction.get().commit();
        GlobalTransaction.transaction.get().begin();
        List<CommentModel> actualComments = commentDao.getAllList();
        Key commentKey = actualComments.get(0).getKey();
        return new Key[]{actualMessages.get(0).getKey(), commentKey};
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
