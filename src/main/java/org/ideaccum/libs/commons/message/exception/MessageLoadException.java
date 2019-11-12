package org.ideaccum.libs.commons.message.exception;

/**
 * メッセージリソース読み込み処理に失敗した場合にスローされる例外クラスです。<br>
 * <p>
 * メッセージリソース読み込み時の入出力例外等が発生した場合にスローされます。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2018/06/13  Kitagawa         新規作成
 *-->
 */
public class MessageLoadException extends RuntimeException {

	/**
	 * コンストラクタ<br>
	 * @param cause ルート例外要因
	 */
	public MessageLoadException(Throwable cause) {
		super(cause);
	}
}
