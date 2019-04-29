package org.ideaccum.libs.commons.message;

import java.io.Serializable;

import org.ideaccum.libs.commons.message.exception.IllegalMessageCode;
import org.ideaccum.libs.commons.util.Loop;
import org.ideaccum.libs.commons.util.StringUtil;

/**
 * 単一のメッセージ定義内容をレベル情報と共に管理するインタフェースを提供します。<br>
 * <p>
 * このクラスは利用者が新規にインスタンス生成することはなく、{@link Messages}クラス内のメッセージ情報エントリとして生成されます。<br>
 * また、このクラスが管理、提供するメッセージコードはレベルサフィックスを持たない、純粋なコードとなります。<br>
 * </p>
 * 
 * @author Kitagawa<br>
 * 
 *<!--
 * 更新日		更新者			更新内容
 * 2018/06/14	Kitagawa		新規作成
 *-->
 */
public final class Message implements Serializable {

	/** メッセージコード */
	private String code;

	/** メッセージレベル */
	private MessageLevel level;

	/** メッセージ内容 */
	private String message;

	/**
	 * コンストラクタ<br>
	 * @param defineCode レベルサフィックスを持つ定義メッセージコード
	 * @param defineMessage 定義メッセージ内容
	 */
	Message(String defineCode, String defineMessage) {
		super();
		if (!isValidDefineCode(defineCode)) {
			throw new IllegalMessageCode(defineCode);
		}
		String buffer = defineCode.trim();
		this.code = buffer.substring(0, buffer.lastIndexOf('-'));
		this.level = MessageLevel.levelOf(defineCode);
		this.message = defineMessage;
	}

	/**
	 * オブジェクト情報を文字列として提供します。<br>
	 * @return オブジェクト情報文字列
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(level.getValue());
		builder.append(" - ");
		builder.append(code);
		builder.append(" | ");
		builder.append(message);
		return builder.toString();
	}

	/**
	 * オブジェクトハッシュコードを取得します。<br>
	 * @return オブジェクトハッシュコード
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	/**
	 * オブジェクト等価比較を行います。<br>
	 * @return 等価の場合にtrueを返却
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		Message other = (Message) object;
		if (code == null) {
			if (other.code != null) {
				return false;
			}
		} else if (!code.equals(other.code)) {
			return false;
		}
		if (level != other.level) {
			return false;
		}
		if (message == null) {
			if (other.message != null) {
				return false;
			}
		} else if (!message.equals(other.message)) {
			return false;
		}
		return true;
	}

	/**
	 * 有効なメッセージ定義コードであるか判定します。<br>
	 * サフィックスにメッセージレベルを保持した形式(コード+"-"+メッセージレベル文字)であるかの判定を行います。<br>
	 * @param defineCode 判定対象メッセージ定義コード
	 * @return 正しいメッセージ定義コードである場合にtrueを返却
	 */
	public static boolean isValidDefineCode(String defineCode) {
		MessageLevel level = MessageLevel.levelOf(defineCode);
		if (level == null) {
			return false;
		}
		return true;
	}

	/**
	 * レベルサフィックスを持たないメッセージコードとレベル情報から外部定義メッセージコードを提供します。<br>
	 * @param code レベルサフィックスを持たないメッセージコード
	 * @param level メッセージレベル
	 * @return 外部定義メッセージコード
	 */
	public static String getDefineCode(String code, MessageLevel level) {
		String buffer = StringUtil.trim(code);
		if (StringUtil.len(buffer) <= 0) {
			throw new IllegalArgumentException("code is empty");
		}
		if (level == null) {
			throw new IllegalArgumentException("null is empty");
		}
		return buffer + '-' + level.getSuffix();
	}

	/**
	 * レベルサフィックスを持つ外部定義メッセージコードから管理されるレベルサフィックスを持たないメッセージコードを提供します。<br>
	 * 不正な形式の外部定義コードが指定された場合、例外がスローされる為、事前に{@link #isValidDefineCode(String)}によってコード形式のチェックを行って下さい。<br>
	 * @param defineCode レベルサフィックスを持つ外部定義メッセージコード
	 * @return 管理されるレベルサフィックスを持たないメッセージコード
	 */
	public static String getMessageCode(String defineCode) {
		if (!isValidDefineCode(defineCode)) {
			throw new IllegalMessageCode(defineCode);
		}
		String buffer = StringUtil.trim(defineCode);
		return buffer.substring(0, buffer.lastIndexOf('-'));
	}

	/**
	 * メッセージコードを取得します。<br>
	 * @return メッセージコード
	 */
	public String getCode() {
		return code;
	}

	/**
	 * メッセージレベルを取得します。<br>
	 * @return メッセージレベル
	 */
	public MessageLevel getLevel() {
		return level;
	}

	/**
	 * 動的値バインド前の定義メッセージ文字列を取得します。<br>
	 * @return 動的値バインド前の定義メッセージ文字列
	 */
	public String getDefine() {
		return message;
	}

	/**
	 * メッセージ内容を取得します。<br>
	 * @param binds バインドオブジェクト
	 * @return メッセージ内容
	 */
	public String getMessage(Object... binds) {
		String buffer = message;
		for (Loop<Object> loop : Loop.each(binds)) {
			int index = loop.index();
			String value = loop.value() == null ? "" : loop.value().toString();
			buffer = StringUtil.replace(buffer, "{" + index + "}", value);
		}
		return buffer;
	}
}
