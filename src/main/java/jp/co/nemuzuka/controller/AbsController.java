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
package jp.co.nemuzuka.controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import jp.co.nemuzuka.annotation.ActionForm;
import jp.co.nemuzuka.annotation.GroupManager;
import jp.co.nemuzuka.annotation.NoSessionCheck;
import jp.co.nemuzuka.common.TimeZone;
import jp.co.nemuzuka.entity.GlobalTransaction;
import jp.co.nemuzuka.entity.TransactionEntity;
import jp.co.nemuzuka.entity.UserInfo;
import jp.co.nemuzuka.entity.UserTimeZone;
import jp.co.nemuzuka.koshiji.service.MemberService;
import jp.co.nemuzuka.koshiji.service.impl.MemberServiceImpl;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.slim3.controller.Controller;
import org.slim3.util.BeanUtil;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Contorollerの基底クラス.
 * @author kazumune
 */
public abstract class AbsController extends Controller {

	/** logger. */
	protected final Logger logger = Logger.getLogger(getClass().getName());

	/** UserInfo格納キー. */
	protected static final String USER_INFO_KEY = "userInfo";
	
	/** token格納キー. */
	//Sessionも、リクエストパラメータもこの項目であることが前提です。
	protected static final  String TOKEN_KEY = "jp.co.nemuzuka.token";

	/** ログインユーザ情報. */
	protected UserService userService;
	
	//Service
	protected MemberService memberService = MemberServiceImpl.getInstance();

	//遷移先URL
	/** システムに登録されていないユーザからのアクセス. */
	protected static final String MOVE_URL_NO_REGIST = "/noregist/";
	/** システムエラー. */
	protected static final String ERR_URL_SYSERROR = "/syserror/";
	/** Sessionタイムアウト. */
	protected static final String ERR_SESSION_TIMEOUT = "/timeout/";
	
	/**
	 * 終了時処理.
	 * ThreadLocalに存在する場合、ロールバックして空にします。
	 * @see org.slim3.controller.Controller#tearDown()
	 */
	@Override
	protected void tearDown() {
		TransactionEntity entity = GlobalTransaction.transaction.get();
		if(entity != null) {
			entity.rollback();
			GlobalTransaction.transaction.remove();
	        GlobalTransaction.transaction.set(null);
		}
	};

	/**
	 * UserInfo取得.
	 * Sessionに格納されているUserInfoを取得します。
	 * @return UserInfoインスタンス
	 */
	protected UserInfo getUserInfo() {
		return sessionScope(USER_INFO_KEY);
	}

	/**
	 * ログインユーザ情報設定.
	 * ユーザに紐付くタイムゾーンをThreadLocalに設定します。
	 */
	protected void setUserService() {
		
		userService = UserServiceFactory.getUserService();
		
		//ThreadLocalにタイムゾーンを設定
		TimeZone timeZone = TimeZone.GMT_P_9;
		if(getUserInfo() != null) {
		    timeZone = getUserInfo().timeZone;
		}
        UserTimeZone.timeZone.set(java.util.TimeZone.getTimeZone(timeZone.getCode()).getID());
	}
	
	/**
	 * Method取得.
	 * メソッドを取得します。
	 * 存在しない場合、親クラスに対して検索します。
	 * @param clazz 対象Class
	 * @param methodName メソッド名
	 * @param paramClass パラメータクラス配列
	 * @return メソッド
	 * @throws NoSuchMethodException 親クラスまでさかのぼっても見つからなかった
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected Method getDeclaredMethod(Class clazz, String methodName, 
			Class[] paramClass) throws NoSuchMethodException {
		Method target = null;
		try {
			target = clazz.getDeclaredMethod(methodName, paramClass);
		} catch(NoSuchMethodException e) {
			Class superClazz = clazz.getSuperclass();
			if(superClazz == null) {
				throw e;
			}
			return getDeclaredMethod(superClazz, methodName, paramClass);
		}
		return target;
	}
	/**
	 * メソッド呼び出し.
	 * 引数なしでメソッドを呼び出します。
	 * @param clazz クラス
	 * @param methodName メソッド名
	 * @return 呼び出したメソッドの戻り値
	 */
	@SuppressWarnings({ "rawtypes" })
	protected Object invoke(Class clazz, String methodName) {

		//validateメソッドの呼び出し
		Method method = null;
		try {
			method = getDeclaredMethod(clazz, methodName, (Class[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//validateメソッド呼び出し
		Object obj = null;
		try {
			method.setAccessible(true);
			obj = method.invoke(this, (Object[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return obj;
	}

	
	/**
	 * ActionForm設定.
	 * 「@ActionForm」と定義されているものに対して、
	 * インスタンス生成し、値をリクエストパラメータよりコピーします。
	 * @param clazz 対象クラス
	 */
	@SuppressWarnings("rawtypes")
	protected void setActionForm(Class clazz) {

		//@ActionFormと定義されているものに対して、インスタンス生成し、コピーする
		Field[] fields = clazz.getDeclaredFields();
		for(Field target : fields) {
			Annotation[] annos = target.getAnnotations();
			for(Annotation targetAnno :annos) {
				if(targetAnno instanceof ActionForm) {

					//インスタンス生成
					Object obj = null;
					try {
						target.setAccessible(true);
						obj = target.getType().newInstance();
						BeanUtil.copy(request, obj);
						target.set(this, obj);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
	/**
	 * Token設定.
	 * SessionにTokenを設定します。
	 * @return 設定Token文字列
	 */
	protected String setToken() {
		String token = RandomStringUtils.randomAlphanumeric(32);
		sessionScope(TOKEN_KEY, token);
		return token;
	}
	

	/**
	 * グローバルトランザクション設定.
	 * ThreadLocalに開始状態のトランザクションを設定します。
	 */
	protected void setTransaction() {
		TransactionEntity transactionEntity = new TransactionEntity();
		GlobalTransaction.transaction.set(transactionEntity);
	}

	/**
	 * Commit実行.
	 * Commitを発行し、ThreadLocalから削除します。
	 */
	protected void executeCommit() {
		TransactionEntity entity = GlobalTransaction.transaction.get();
		entity.commit();
		GlobalTransaction.transaction.remove();
        GlobalTransaction.transaction.set(null);
	}
	
	/**
	 * Group管理者チェック実行.
	 * メイン処理に「@GroupManager」が付与されれている場合、Group管理者であるかチェックを行います。
	 * 管理者でない場合、戻り値をfalseに設定します。
	 * @param clazz 対象クラス
	 * @return Group管理者である or 付与されていない場合、true/Group管理者でない場合、false
	 */
	@SuppressWarnings("rawtypes")
	protected boolean isGroupManager(Class clazz) {
		Method target = null;
		try {
			target = getDeclaredMethod(clazz, "execute", (Class[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		GroupManager groupManager = target.getAnnotation(GroupManager.class);
		if(groupManager != null) {
			//UserInfoにおいて、管理者であるかの結果を返す
			return getUserInfo().groupManager;
		}
		return true;
	}

	/**
	 * Session存在チェック.
	 * Sessionが存在するか確認します。
	 * @param clazz 対象クラス
	 * @return Sessionが存在する or NoSessionCheckアノテーションが付与されている場合、true/Sessionが存在しない、false
	 */
	@SuppressWarnings("rawtypes")
	protected boolean executeSessionCheck(Class clazz) {
		Method target = null;
		try {
			target = getDeclaredMethod(clazz, "execute", (Class[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		NoSessionCheck noSessionCheck = target.getAnnotation(NoSessionCheck.class);
		if(noSessionCheck != null) {
			//executeメソッドにNoSessionCheckアノテーションが付与されている場合、無条件でOKとする
			return true;
		}
		UserInfo userInfo = getUserInfo();
		if(userInfo == null) {
			return false;
		}
		return true;
	}

	/**
	 * ユーザ存在チェック.
	 * メールアドレスがシステム上に登録されているかチェックします。
	 * @param email チェック対象メールアドレス
	 * @return　登録されている場合、true
	 */
	protected boolean isExistsUser(String email) {
		Key key = memberService.getKey(email);
		if(key == null) {
			//存在しない
			return false;
		}
		return true;
	}

	/**
	 * Tokenチェック.
	 * リクエストパラメータとSession上のTokenが合致するかチェックします。
	 * @return 合致する場合、true
	 */
	protected boolean isTokenCheck() {
		String reqToken = asString(TOKEN_KEY);
		String sessionToken = sessionScope(TOKEN_KEY);
		removeSessionScope(TOKEN_KEY);
		if(ObjectUtils.equals(reqToken, sessionToken) == false) {
			return false;
		}
		return true;
	}
	
	/**
	 * ログアウトPath生成.
	 * Sessionを破棄し、ログアウト状態にし、ログアウトのPathを返却します。
	 * @return ログアウトPath
	 */
	protected String createLogoutPath() {
        HttpSession session = request.getSession(false);
        if(session != null) {
            session.invalidate();
        }
        
        String requestURL = request.getRequestURL().toString();
        String requestURI = request.getRequestURI();
        String path = requestURL.replaceAll(requestURI, "/");
        return userService.createLogoutURL(path);
	}
}
