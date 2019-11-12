package org.ideaccum.libs.commons.message.exception;

/**
 * 不正なログメッセージコード体系のコードを扱った場合にスローされる例外クラスです。<br>
 * <p>
 * ログメッセージコード体系はプレフィックスにログレベルを持たない純粋なメッセージコードとサフィックスに"-"を持つメッセージレベルを持ちます。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2018/06/13  Kitagawa         新規作成
 *-->
 */
public class IllegalMessageCodeException extends RuntimeException {

	/**
	 * コンストラクタ<br>
	 * @param code 利用メッセージコード
	 */
	public IllegalMessageCodeException(String code) {
		super("Illegal message code format (" + code + ")");
	}
}
