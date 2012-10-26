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
package jp.co.nemuzuka.koshiji.validator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import jp.co.nemuzuka.utils.ConvertUtils;
import jp.co.nemuzuka.utils.DateTimeChecker;
import jp.co.nemuzuka.utils.DateTimeUtils;

import org.apache.commons.lang.StringUtils;
import org.slim3.controller.validator.AbstractValidator;
import org.slim3.util.ApplicationMessage;

/**
 * 2つの日付時刻の相関チェックのvalidator.
 * nameには、カンマ区切りで「開始日付,開始時間,終了日付,終了時間」の形で設定されていることを想定します。
 * さらにデータ型はyyyyMMdd(日付)、HHmm(時間)である想定です。
 * 日付は必須です。
 * 時間は両方共設定されているか両方未設定である必要があります。
 * @author k-katagiri
 */
public class DateTimeRangeValidator extends AbstractValidator {

	/**
	 * コンストラクタ.
	 * @param maxlength 1要素の文字数
	 */
	public DateTimeRangeValidator() {
		super();
	}
	
	/**
     * コンストラクタ.
     * @param message メッセージキー
     */
    public DateTimeRangeValidator(String message) {
        super(message);
    }
	
	/* (non-Javadoc)
	 * @see org.slim3.controller.validator.Validator#validate(java.util.Map, java.lang.String)
	 */
	@Override
	public String validate(Map<String, Object> parameters, String name) {
		String[] names = name.split(",");
		if(names.length != 4) {
			//カンマ区切りで渡ってきていない場合、処理終了
			return null;
		}
		
        String startDateStr = (String) parameters.get(names[0]);
        String startTimeStr = (String) parameters.get(names[1]);
        String endDateStr = (String) parameters.get(names[2]);
        String endTimeStr = (String) parameters.get(names[3]);
        
        //日付が設定されていること、時間が両方共設定または未設定であることの確認
        String errorMsg = checkDateTime(startDateStr, startTimeStr, endDateStr, endTimeStr, names);
        if(errorMsg != null) {
            return errorMsg;
        }
        
        //時間が設定されていなければ、00:00が設定されたとみなす
        if(StringUtils.isEmpty(startTimeStr)) {
            startTimeStr = "0000";
            endTimeStr = "0000";
        }
        
		Long startTime = getTime(startDateStr + " " + startTimeStr);
		Long endTime = getTime(endDateStr + " " + endTimeStr);
		
		if(startTime.longValue() > endTime.longValue()) {
			//開始日>終了日の関係の場合、エラーメッセージを返却
	        if (message != null) {
	            return message;
	        }
	        return ApplicationMessage.get(
	            getMessageKey(),
	            getLabel(names[2]),
	            getLabel(names[0]),
	            getLabel("date"));
		}
		return null;
	}

	/**
	 * 入力チェック.
	 * 日付文字列は必須で、日付フォーマットに合致すること
	 * 時間文字列は時間フォーマットに合致すること、両方設定されているか両方未設定であること
	 * をチェックします。
	 * @param parameters リクエストパラメータ
	 * @param names パラメータ名配列
	 * @return 想定内の文字列の場合、null/想定外の文字の場合、メッセージ
	 */
	private String checkDateTime(String startDateStr, String startTimeStr,
	        String endDateStr,String endTimeStr, String[] names) {

	    if(DateTimeChecker.isDay(startDateStr) == false) {
            return ApplicationMessage.get(
                "validator.dateType", getLabel(names[0]), "yyyyMMdd");
	    }
	    if(StringUtils.isNotEmpty(startTimeStr) && DateTimeChecker.isHourMinute(startTimeStr) == false) {
            return ApplicationMessage.get(
                "validator.timeType", getLabel(names[1]), "HHmm");
	    }
        if(DateTimeChecker.isDay(endDateStr) == false) {
            return ApplicationMessage.get(
                "validator.dateType", getLabel(names[2]), "yyyyMMdd");
        }
        if(StringUtils.isNotEmpty(endTimeStr) && DateTimeChecker.isHourMinute(endTimeStr) == false) {
            return ApplicationMessage.get(
                "validator.timeType", getLabel(names[3]), "HHmm");
        }
	    
        //時間のチェック
        if((StringUtils.isEmpty(startTimeStr) && StringUtils.isNotEmpty(endTimeStr)) ||
                (StringUtils.isNotEmpty(startTimeStr) && StringUtils.isEmpty(endTimeStr))) {
            return ApplicationMessage.get(
                "validator.timeRange");
        }
        
        return null;
    }

    /* (non-Javadoc)
	 * @see org.slim3.controller.validator.AbstractValidator#getMessageKey()
	 */
	@Override
	protected String getMessageKey() {
		return "validator.check.between";
	}

	/**
	 * 日付ミリ秒取得.
	 * 引数の日付文字列が未設定、または不正の場合、nullを返却します。
	 * @param dateStr 日付文字列(yyyyMMdd HHmmフォーマット)
	 * @return 日付ミリ秒
	 */
	private Long getTime(String dateStr) {
		SimpleDateFormat sdf = DateTimeUtils.createSdf("yyyyMMdd HHmm");
		Date date = null;
		try {
			date = ConvertUtils.toDate(dateStr, sdf);
			if(date != null) {
				return date.getTime(); 
			}
		} catch(RuntimeException e) {}
		return null;
	}
	
}
