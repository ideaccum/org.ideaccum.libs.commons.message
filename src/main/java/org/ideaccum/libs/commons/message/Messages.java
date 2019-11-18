package org.ideaccum.libs.commons.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.ideaccum.libs.commons.message.exception.MessageLoadException;
import org.ideaccum.libs.commons.util.PropertiesUtil;
import org.ideaccum.libs.commons.util.ResourceUtil;
import org.ideaccum.libs.commons.util.StringUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * コードとメッセージが対となる形式でのメッセージリソースを管理するインタフェースを提供します。<br>
 * <p>
 * このクラスではコード定義された内容をもとに動的な値バインドを可能とするテンプレートメッセージ内容を管理します。<br>
 * このメッセージリソースクラスはアプリケーション実行中は永続的に内容を保持し続けます。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2018/06/13  Kitagawa         新規作成
 * 2019/05/08  Kitagawa         Javascriptからのメッセージ定義利用用のスクリプトソース出力メソッド({@link #writeScript(PrintWriter)})を追加
 *-->
 */
public final class Messages implements Serializable {

	/** スクリプトリソースパス */
	private static final String SCRIPT_RESOURCE = "/" + Messages.class.getPackage().getName().replace(".", "/") + "/Messages.js";

	/** シングルトンインスタンス */
	private static Messages global = new Messages(false);

	/** ロックオブジェクト */
	private static Object lock = new Object();

	/** シングルトンインスタンス値継承フラグ */
	private boolean inheritGlobal;

	/** メッセージデータ */
	private Map<String, Message> messages;

	/**
	 * コンストラクタ<br>
	 * @param inheritGlobal シングルトンインスタンス値継承フラグ
	 */
	private Messages(boolean inheritGlobal) {
		super();
		this.messages = new HashMap<>();
		this.inheritGlobal = inheritGlobal;
	}

	/**
	 * クラスローダー上で単一インスタンスが保証されるグローバルメッセージ定義情報を取得します。<br>
	 * @return グローバルメッセージ定義情報
	 */
	public static Messages global() {
		return global;
	}

	/**
	 * グローバルメッセージ定義情報とは別のインスタンスとしてメッセージ定義情報を生成します。<br>
	 * @param inheritGlobal 個別メッセージ定義情報に情報が存在しない場合はメッセージ定義情報を継承して提供する場合にtrueを指定
	 * @return 環境設定情報
	 */
	public static Messages create(boolean inheritGlobal) {
		return new Messages(inheritGlobal);
	}

	/**
	 * グローバルメッセージ定義情報とは別のインスタンスとしてメッセージ定義情報を生成します。<br>
	 * 個別メッセージ定義情報に情報が存在しない場合はグローバルメッセージ定義情報を継承して値が提供されます。<br>
	 * @return 環境設定情報
	 */
	public static Messages create() {
		return create(true);
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
	 * 管理されているメッセージのキーセットを取得します。<br>
	 * @return 管理されているメッセージのキーセット
	 */
	public Set<String> keySet() {
		return messages.keySet();
	}

	/**
	 * 出力ストリームに対してメッセージ操作用スクリプトを出力します。<br>
	 * @param writer 出力ストリーム
	 * @throws IOException レスポンス操作時に入出力例外が発生した場合にスローされます
	 */
	public void writeScript(PrintWriter writer) throws IOException {
		writer.println(ResourceUtil.getText(SCRIPT_RESOURCE, "utf-8"));
		for (String key : keySet()) {
			Message message = get(key);
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
	 * メッセージリソース内容を読み込みクラスインスタンスに展開します。<br>
	 * @param filePath メッセージリソースパス
	 * @param mode メッセージリソース読み込み時の挙動
	 * @return ロード後の自身のインスタンス
	 */
	public Messages load(String filePath, MessagesLoadMode mode) {
		synchronized (lock) {
			try {
				/*
				 * 対象プロパティ読み込み
				 */
				Map<String, Message> loaded = loadDispatch(filePath);

				/*
				 * 読み込みモードごと処理
				 */
				if (mode == MessagesLoadMode.REPLACE_ALL || mode == null) {
					// すべてのプロパティを置き換える場合は現状の保持情報をクリア
					messages.clear();
					messages.putAll(loaded);
				} else if (mode == MessagesLoadMode.REPLACE_EXISTS) {
					// 既存プロパティに対しては上書きする場合は読み込んだプロパティをプット
					messages.putAll(loaded);
				} else if (mode == MessagesLoadMode.SKIP_EXISTS) {
					// 既存プロパティに対しては現状維持とする場合はプロパティごとに判定しながらプット
					for (String key : loaded.keySet()) {
						if (messages.containsKey(key)) {
							continue;
						}
						Message value = loaded.get(key);
						messages.put(key, value);
					}
				}
				return this;
			} catch (Throwable e) {
				throw new MessageLoadException(e);
			}
		}
	}

	/**
	 * メッセージリソース内容を読み込みクラスインスタンスに展開します。<br>
	 * このメソッドによる読み込みは現在管理されているメッセージ情報を破棄して新たに読み込みます。<br>
	 * 読み込み方法を指定してメッセージを反映する場合は{@link #load(String, MessagesLoadMode)}を利用して下さい。<br>
	 * @param filePath メッセージリソースパス
	 * @return ロード後の自身のインスタンス
	 */
	public Messages load(String filePath) {
		return load(filePath, MessagesLoadMode.REPLACE_ALL);
	}

	/**
	 * メッセージを読み込みます。<br>
	 * @param filePath メッセージリソースパス
	 * @return 読み込まれたメッセージリソース
	 * @throws IOException 入出力例外が発生した場合にスローされます
	 * @throws ParserConfigurationException XMLドキュメントビルダの生成に失敗した場合にスローされます
	 * @throws SAXException XML定義形式が不正な場合にスローされます
	 */
	private Map<String, Message> loadDispatch(String filePath) throws IOException, ParserConfigurationException, SAXException {
		if (!ResourceUtil.exists(filePath)) {
			return new HashMap<>();
		}
		if (filePath.endsWith(".xml")) {
			return loadFromXML(filePath);
		} else {
			return loadFromProperties(filePath);
		}
	}

	/**
	 * メッセージリソースからメッセージを読み込みます。<br>
	 * @param filePath メッセージリソースパス
	 * @return 読み込まれたメッセージリソース
	 * @throws IOException 入出力例外が発生した場合にスローされます
	 */
	private Map<String, Message> loadFromProperties(String filePath) throws IOException {
		Map<String, Message> map = new HashMap<>();
		Properties properties = PropertiesUtil.load(filePath);
		for (Object key : properties.keySet()) {
			Object property = properties.get(key);
			String code = key == null ? "" : key.toString();
			String value = property == null ? "" : property.toString();
			Message message = new Message(code, value);
			map.put(message.getCode(), message);
		}
		return map;
	}

	/**
	 * XMLリソースからメッセージを読み込みます。<br>
	 * @param filePath メッセージリソースパス
	 * @return 読み込まれたメッセージリソース
	 * @throws IOException 入出力例外が発生した場合にスローされます
	 * @throws ParserConfigurationException XMLドキュメントビルダの生成に失敗した場合にスローされます
	 * @throws SAXException XML定義形式が不正な場合にスローされます
	 */
	private Map<String, Message> loadFromXML(String filePath) throws IOException, ParserConfigurationException, SAXException {
		InputStream stream = null;
		try {
			Map<String, Message> map = new HashMap<>();

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			stream = ResourceUtil.getInputStream(filePath);
			Document document = builder.parse(stream);

			Element messagesElement = document.getDocumentElement();
			if (!"messages".equals(messagesElement.getNodeName())) {
				throw new SAXException();
			}

			NodeList messageElements = messagesElement.getElementsByTagName("message");
			for (int i = 0; i <= messageElements.getLength() - 1; i++) {
				Element messageElement = (Element) messageElements.item(i);
				String code = messageElement.getAttribute("code");
				String value = messageElement.getAttribute("value");
				if (StringUtil.isEmpty(code)) {
					throw new SAXException("message node is code attribute required");
				}
				//if (StringUtil.isEmpty(value)) {
				//	throw new SAXException("message node is value attribute required");
				//}
				Message message = new Message(code, value);
				map.put(message.getCode(), message);
			}

			return map;
		} finally {
			if (stream != null) {
				stream.close();
			}
		}
	}

	/**
	 * 管理されているメッセージ情報を全てクリアします。<br>
	 */
	public void destroy() {
		synchronized (lock) {
			messages.clear();
		}
	}

	/**
	 * 他のメッセージ情報内容を自身のインスタンスにマージします。<br>
	 * @param other マージ元インスタンス
	 */
	@SuppressWarnings("static-access")
	public void merge(Messages other) {
		if (other == null || other.equals(this)) {
			return;
		}
		this.global.messages.putAll(other.global.messages); // For other classloader
		this.messages.putAll(other.messages);
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
			message = messages.get(Message.getMessageCode(code));
			if (message != null) {
				return message;
			}
		}
		if (inheritGlobal) {
			message = global.messages.get(code);
			if (message != null) {
				return message;
			}
			if (Message.isValidDefineCode(code)) {
				message = global.messages.get(Message.getMessageCode(code));
				if (message != null) {
					return message;
				}
			}
		}
		return null;
	}
}
