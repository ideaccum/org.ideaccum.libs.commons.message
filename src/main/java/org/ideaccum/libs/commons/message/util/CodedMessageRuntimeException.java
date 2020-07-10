package org.ideaccum.libs.commons.message.util;

import org.ideaccum.libs.commons.message.Messages;

/**
 * メッセージコードをもとにした例外クラスとして設置するための上位クラスです。<br>
 * <p>
 * このクラスを継承した例外とすることで例外メッセージは定義されたメッセージ定義内容をもとにした例外をスローします。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2020/07/06  Kitagawa         新規作成
 *-->
 */
public class CodedMessageRuntimeException extends RuntimeException {

	/**
	 * コンストラクタ<br>
	 * @param messages メッセージ定義情報
	 * @param code メッセージコード
	 * @param binds メッセージバインド文字列
	 * @param cause ルート要因
	 */
	public CodedMessageRuntimeException(Messages messages, String code, Object[] binds, Throwable cause) {
		super(messages == null ? Messages.global().get(code).getMessage(binds) : messages.get(code).getMessage(binds), cause);
	}

	/**
	 * コンストラクタ<br>
	 * @param code メッセージコード
	 * @param binds メッセージバインド文字列
	 * @param cause ルート要因
	 */
	public CodedMessageRuntimeException(String code, Object[] binds, Throwable cause) {
		super(Messages.global().get(code).getMessage(binds), cause);
	}

	/**
	 * コンストラクタ<br>
	 * @param messages メッセージ定義情報
	 * @param code メッセージコード
	 * @param cause ルート要因
	 */
	public CodedMessageRuntimeException(Messages messages, String code, Throwable cause) {
		super(messages == null ? Messages.global().get(code).getMessage() : messages.get(code).getMessage(), cause);
	}

	/**
	 * コンストラクタ<br>
	 * @param code メッセージコード
	 * @param cause ルート要因
	 */
	public CodedMessageRuntimeException(String code, Throwable cause) {
		super(Messages.global().get(code).getMessage(), cause);
	}

	/**
	 * コンストラクタ<br>
	 * @param messages メッセージ定義情報
	 * @param code メッセージコード
	 * @param binds メッセージバインド文字列
	 */
	public CodedMessageRuntimeException(Messages messages, String code, Object[] binds) {
		super(messages == null ? Messages.global().get(code).getMessage(binds) : messages.get(code).getMessage(binds));
	}

	/**
	 * コンストラクタ<br>
	 * @param code メッセージコード
	 * @param binds メッセージバインド文字列
	 */
	public CodedMessageRuntimeException(String code, Object[] binds) {
		this(null, code, binds);
	}

	/**
	 * コンストラクタ<br>
	 * @param messages メッセージ定義情報
	 * @param code メッセージコード
	 */
	public CodedMessageRuntimeException(Messages messages, String code) {
		super(messages == null ? Messages.global().get(code).getMessage() : messages.get(code).getMessage());
	}

	/**
	 * コンストラクタ<br>
	 * @param code メッセージコード
	 */
	public CodedMessageRuntimeException(String code) {
		super(Messages.global().get(code).getMessage());
	}
}
