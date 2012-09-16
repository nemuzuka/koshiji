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

import java.lang.reflect.Method;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;

import jp.co.nemuzuka.annotation.NoRegistCheck;
import jp.co.nemuzuka.annotation.Validation;
import jp.co.nemuzuka.exception.AlreadyExistKeyException;

import org.slim3.controller.Navigation;
import org.slim3.controller.validator.Validators;

/**
 * Htmlを返却するControllerの基底クラス.
 * 基本的に、これを継承したControllerからデータストアのアクセスは考えていません。
 * @author kazumune
 */
public abstract class HtmlController extends AbsController {
	
	/**
	 * メイン処理.
	 * @return 遷移先Navigation
	 * @throws Exception 例外
	 */
	abstract protected Navigation execute() throws Exception;

	/**
	 * メイン処理.
	 * 正常終了時、commitしてThreadLocalから削除します。
	 * ConcurrentModificationExceptionやAlreadyExistKeyExceptionは
	 * 本クラスを継承したクラスでは発生しない設計思想なので、
	 * エラー画面に遷移させます。
	 * ※更新は、Ajax側で行う
	 * @see org.slim3.controller.Controller#run()
	 */
	@Override
	protected Navigation run() throws Exception {
		
		//グローバルトランザクションの設定を行う
		setTransaction();
		
		Navigation navigation = null;
		try {
			navigation = execute();
			//commit
			executeCommit();
		} catch (ConcurrentModificationException e) {
			//今回の思想では、こちらのケースで排他エラーになるような処理は無いので、
			//強制的にエラー画面を表示させることとする
			//ここに来たら設計バグ
			navigation = forward(ERR_URL_SYSERROR);
		} catch(AlreadyExistKeyException e) {
			//一意制約エラーの場合
			//ここに来たら設計バグ
			navigation = forward(ERR_URL_SYSERROR);
		} catch(Exception e) {
			logger.log(Level.SEVERE, e.getMessage(), e);
			throw e;
		} finally {
            super.tearDown();
		}
		return navigation;
	}
	
	/**
	 * 前処理.
	 * ・ActionFormの設定
	 * ・validation
	 * を行います。
	 * @see org.slim3.controller.Controller#setUp()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected Navigation setUp() {
		super.setUp();
		Class clazz = getClass();

		setUserService();
		
		boolean sessionCheck = executeSessionCheck(clazz);
		if(sessionCheck == false) {
			//SessionTimeoutの場合、エラー画面に遷移
			return forward(ERR_SESSION_TIMEOUT);
		}

		//ログインユーザの情報を元に、データストアに設定されているかチェック
		Navigation navigation = checkSettingUser(clazz);
		if(navigation != null) {
			return navigation;
		}
		
		//GroupManagerの設定確認
		boolean groupManager = isGroupManager(clazz);
		if(groupManager == false) {
			//不正なエラーの場合、ログアウトしてTOP画面に遷移させる
		    return redirect(createLogoutPath());
		}
		
		//ActionFormの設定
		setActionForm(clazz);

		//validationの実行
		return executeValidation(clazz);
	}
	
	/**
	 * ログインユーザ設定チェック.
	 * 「@NoRegistCheck」が付与されている場合、強制的にnullを返します。
	 * @param clazz 対象クラス
	 * @return 登録済みである場合/「@NoRegistCheck」が付与されている場合、null/それ以外、強制遷移先Navigation
	 */
	@SuppressWarnings({ "rawtypes" })
	private Navigation checkSettingUser(Class clazz) {
		
		//executeメソッドにValidatetionアノテーションが付与されている場合
		Method target = null;
		try {
			target = getDeclaredMethod(clazz, "execute", (Class[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		//NoRegistCheckアノテーションが付与されている場合、処理終了
		NoRegistCheck noRegistCheck = target.getAnnotation(NoRegistCheck.class);
		if(noRegistCheck != null) {
			return null;
		}

		//登録済みユーザであることを確認する
		if(isExistsUser(userService.getCurrentUser().getEmail()) == false) {
			//存在しないので遷移先のNavigation
			return forward(MOVE_URL_NO_REGIST);
		}
		return null;
	}

	/**
	 * validation実行.
	 * メイン処理に「@Validation」が付与されれている場合、メソッドを呼び出し、validateを実行します。
	 * @param clazz 対象クラス
	 * @return エラーが無い or validatationが無い場合はnull/エラーが存在する場合、遷移先Navigation
	 */
	@SuppressWarnings({ "rawtypes" })
	private Navigation executeValidation(Class clazz) {
		//executeメソッドにValidatetionアノテーションが付与されている場合
		Method target = null;
		try {
			target = getDeclaredMethod(clazz, "execute", (Class[])null);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		Validation validation = target.getAnnotation(Validation.class);
		if(validation != null) {

			Validators validators = (Validators)invoke(clazz, validation.method());

			//validate実行
			boolean bret = validators.validate();

			//エラーが存在する場合
			if(bret == false) {
				//inputのメソッドで呼び出された定義を呼び出すようにする
				return (Navigation) invoke(getClass(), validation.input());
			}
		}
		return null;
	}
}
