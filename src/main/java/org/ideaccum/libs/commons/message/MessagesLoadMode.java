package org.ideaccum.libs.commons.message;

/**
 * メッセージ定義情報読み込み時のモードを提供します。<br>
 * <p>
 * この列挙型で提供される読み込みモードは{@link org.ideaccum.libs.commons.message.Messages#load(String, MessagesLoadMode)}で利用します。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2018/06/14  Kitagawa         新規作成
 *-->
 */
public enum MessagesLoadMode {

	/** 既に定義されているメッセージは置き換えずに追加読み込みします */
	SKIP_EXISTS, //

	/** 既に定義されているメッセージは置き換える形で追加読み込みします(ディフォルト) */
	REPLACE_EXISTS, //

	/** 存在するメッセージをクリアして読み込み対象のメッセージですべての定義を置き換えます */
	REPLACE_ALL, //
}
