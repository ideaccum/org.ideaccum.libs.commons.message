package org.ideaccum.libs.commons.message;

import org.ideaccum.libs.commons.util.StringUtil;

/**
 * メッセージリソースとして管理されるメッセージレベルを列挙型で提供します。<br>
 * <p>
 * メッセージリソースとして定義出来るレベルはこの列挙型で提供されるレベルとなります。<br>
 * また、各列挙定義が提供するサフィックス文字をメッセージリソースとして定義する際のコードのサフィックスに付与します。<br>
 * </p>
 * 
 * @author Kitagawa<br>
 * 
 *<!--
 * 更新日		更新者			更新内容
 * 2018/06/13	Kitagawa		新規作成
 *-->
 */
public enum MessageLevel {

	/** 不明レベル */
	UNKOWN("Unkown", "?????", '\0'),

	/** エラーレベル */
	ERROR("Error", "ERROR", 'E'),

	/** 警告レベル */
	WARNING("Warning", "WARN ", 'W'),

	/** 情報レベル */
	INFORMATION("Information", "INFO ", 'I'),

	/** デバッグレベル */
	DEBUG("Debug", "DEBUG", 'D'),

	/** トレースレベル */
	TRACE("Trace", "TRACE", 'T'),

	/** 非表示 */
	HIDE("Hide", "HIDE", 'H'),

	;

	/** レベル名 */
	private String name;

	/** レベル値 */
	private String value;

	/** サフィックス文字 */
	private char suffix;

	/**
	 * コンストラクタ<br>
	 * @param name レベル名
	 * @param value レベル値
	 * @param suffix サフィックス文字
	 */
	private MessageLevel(String name, String value, char suffix) {
		this.name = name;
		this.value = value;
		this.suffix = suffix;
	}

	/**
	 * レベル名を取得します。<br>
	 * @return レベル名
	 */
	public String getName() {
		return name;
	}

	/**
	 * レベル値を取得します。<br>
	 * @return レベル値
	 */
	public String getValue() {
		return value;
	}

	/**
	 * サフィックス文字を取得します。<br>
	 * @return サフィックス文字
	 */
	public char getSuffix() {
		return suffix;
	}

	/**
	 * メッセージコードのサフィックスを判定して該当するレベルを提供します。<br>
	 * メッセージコード形式が期待する形式(末尾が"-"+レベル文字)となっていない場合はnullが返却されます。<br>
	 * また、サフィックス文字に該当するレベルが存在しない場合は、一律{@link #UNKOWN}が返却されます。<br>
	 * @param defineCode メッセージコード
	 * @return メッセージレベル
	 */
	public static MessageLevel levelOf(String defineCode) {
		String buffer = StringUtil.trim(defineCode);
		if (StringUtil.len(buffer) < 3) {
			return null;
		}
		char separate = buffer.charAt(buffer.length() - 2);
		if (separate != '-') {
			return null;
		}
		char suffix = buffer.charAt(buffer.length() - 1);
		for (MessageLevel e : values()) {
			if (e.suffix == suffix) {
				return e;
			}
		}
		return null;
	}
}
