package org.ideaccum.libs.commons.message;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.ideaccum.libs.commons.message.exception.AlreadyExistsMessageCode;
import org.ideaccum.libs.commons.util.ResourceUtil;

/**
 * コードとメッセージが対となる形式でのメッセージリソースを管理するインタフェースを提供します。<br>
 * <p>
 * このクラスではコード定義された内容をもとに動的な値バインドを可能とするテンプレートメッセージ内容を管理します。<br>
 * このメッセージリソースクラスはアプリケーション実行中は永続的に内容を保持し続けます。<br>
 * </p>
 * 
 * @author Kitagawa<br>
 * 
 *<!--
 * 更新日		更新者			更新内容
 * 2018/06/13	Kitagawa		新規作成
 * 2019/05/08	Kitagawa		Javascriptからのメッセージ定義利用用のスクリプトソース出力メソッド({@link #writeMessagesScript(PrintWriter)})を追加
 *-->
 */
public final class Messages implements Serializable {

	/** スクリプトリソースパス */
	private static final String SCRIPT_RESOURCE = "/" + Messages.class.getPackage().getName().replace(".", "/") + "/Messages.js";

	/** クラスインスタンス */
	private static Messages instance;

	/** ロックオブジェクト */
	private static Object lock = new Object();

	/** メッセージデータ */
	private Map<String, Message> messages;

	/** メッセージ追加時の上書き許可フラグ */
	private boolean permitOverwrite;

	/**
	 * コンストラクタ<br>
	 */
	Messages() {
		super();
		this.messages = new HashMap<>();
		this.permitOverwrite = true;
	}

	/**
	 * クラスインスタンスを取得します。<br>
	 * @return クラスインスタンス
	 */
	public static Messages instance() {
		synchronized (lock) {
			if (instance == null) {
				instance = new Messages();
			}
			return instance;
		}
	}

	/**
	 * オブジェクト情報を文字列として提供します。<br>
	 * @return オブジェクト情報文字列
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return messages.toString();
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
		result = prime * result + ((messages == null) ? 0 : messages.hashCode());
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
		Messages other = (Messages) object;
		if (messages == null) {
			if (other.messages != null) {
				return false;
			}
		} else if (!messages.equals(other.messages)) {
			return false;
		}
		return true;
	}

	/**
	 * 出力ストリームに対してメッセージ操作用スクリプトを出力します。<br>
	 * @param writer 出力ストリーム
	 * @throws IOException レスポンス操作時に入出力例外が発生した場合にスローされます
	 */
	public void writeMessagesScript(PrintWriter writer) throws IOException {
		writer.println(ResourceUtil.getText(SCRIPT_RESOURCE, "utf-8"));
		for (String key : Messages.instance().keySet()) {
			Message message = Messages.instance().get(key);
			String code = message.getCode();
			String level = message.getLevel().getName();
			String define = message.getDefine() //
					.replace("\\", "\\\\") //
					.replace("\"", "\\\"") //
					.replace("\n", "\\n") //
			;
			writer.println("Messages.add(\"" + code + "\", \"" + level + "\", \"" + define + "\");");
		}
		writer.flush();
	}

	/**
	 * 管理されているメッセージのキーセットを取得します。<br>
	 * @return 管理されているメッセージのキーセット
	 */
	public Set<String> keySet() {
		return messages.keySet();
	}

	/**
	 * レベルサフィックス付きメッセージコードをもとにメッセージを追加設定します。<br>
	 * 既に同一キーでメッセージが管理されている場合は上書きされます。<br>
	 * @param defineCode レベルサフィックス付きメッセージコード
	 * @param defineMessage メッセージ内容
	 */
	public void addMessage(String defineCode, String defineMessage) {
		synchronized (lock) {
			Message message = new Message(defineCode, defineMessage);
			if (!permitOverwrite && messages.containsKey(message.getCode())) {
				throw new AlreadyExistsMessageCode(message.getCode());
			}
			messages.put(message.getCode(), message);
		}
	}

	/**
	 * レベルサフィックス付きメッセージコードで定義されているプロパティリソースからメッセージ定義内容を追加設定します。<br>
	 * 既に同一キーでメッセージが管理されている場合は上書きされます。<br>
	 * @param properties メッセージ定義プロパティリソース
	 */
	public void addMessage(Properties properties) {
		if (properties == null) {
			return;
		}
		for (Object key : properties.keySet()) {
			Object value = properties.get(key);
			String defineCode = key == null ? "" : key.toString();
			String defineMessage = value == null ? "" : value.toString();
			addMessage(defineCode, defineMessage);
		}
	}

	/**
	 * 管理されている全てのメッセージ内容をクリアします。<br>
	 */
	public void clear() {
		messages.clear();
	}

	/**
	 * メッセージコードで管理されているメッセージ内容を削除します。<br>
	 * メッセージコードはレベルサフィックスを持たないコード又は、レベルサフィックスを持つ定義コード共に指定可能です。<br>
	 * @param code メッセージコード
	 */
	public void remove(String code) {
		messages.remove(code);
		if (Message.isValidDefineCode(code)) {
			messages.remove(Message.getMessageCode(code));
		}
	}

	/**
	 * メッセージコードで管理されているメッセージ内容を取得します。<br>
	 * メッセージコードはレベルサフィックスを持たないコード又は、レベルサフィックスを持つ定義コード共に指定可能です。<br>
	 * @param code メッセージコード
	 * @return メッセージ内容
	 */
	public Message get(String code) {
		Message message = messages.get(code);
		if (message != null) {
			return message;
		}
		if (Message.isValidDefineCode(code)) {
			return messages.get(Message.getMessageCode(code));
		}
		return null;
	}

	/**
	 * メッセージ追加時の上書き許可フラグを取得します。<br>
	 * @return メッセージ追加時の上書き許可フラグ
	 */
	public boolean isPermitOverwrite() {
		return permitOverwrite;
	}

	/**
	 * メッセージ追加時の上書き許可フラグを設定します。<br>
	 * @param permitOverwrite メッセージ追加時の上書き許可フラグ
	 */
	public void setPermitOverwrite(boolean permitOverwrite) {
		this.permitOverwrite = permitOverwrite;
	}
}
