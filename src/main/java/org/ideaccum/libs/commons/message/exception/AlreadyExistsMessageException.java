package org.ideaccum.libs.commons.message.exception;

/**
 * 既に管理されているメッセージコードに対して上書きでメッセージ情報を追加しようとした場合にスローされる例外クラスです。<br>
 * <p>
 * {@link org.ideaccum.libs.commons.message.Messages#setPermitOverwrite(boolean)}によって上書きを許容する設定となっている場合は発生しない例外です。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2018/07/10  Kitagawa         新規作成
 *-->
 */
public class AlreadyExistsMessageException extends RuntimeException {

	/**
	 * コンストラクタ<br>
	 * @param code 利用メッセージコード
	 */
	public AlreadyExistsMessageException(String code) {
		super("Already exists message code (" + code + ")");
	}
}
