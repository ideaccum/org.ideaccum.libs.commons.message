/**
 * フレームワークが提供する外部定義メッセージリソースのスクリプト用アクセスインタフェースを提供します。<br>
 * <p>
 * このスクリプトでは、{@link org.ideaccum.libs.commons.message.Messages}が提供するメッセージ情報と同様のメッセージを提供します。<br>
 * このスクリプトファイルはクライアントからの直接リンクを行うものではなく、{@link org.ideaccum.libs.commons.message.MessagesResponse}から動的にレスポンスして利用されます。<br>
 * </p>
 * 
 *<!--
 * 更新日      更新者           更新内容
 * 2019/05/08  Kitagawa         新規作成
 *-->
 */
(function(window, document, undefined) {
	"use strict";

	/**
	 * 文字列がnull又は空文字列か判定します。<br>
	 * @param value 文字列
	 * @returns 文字列がnull又は空文字列の場合にtrueを返却 
	 */
	var isEmpty = function(value) {
		return value === undefined || !(typeof value === "string") || value.length === 0;
	};

	/**
	 * オブジェクトが配列オブジェクトか判定します。<br>
	 * @param object 判定対象オブジェクト
	 * @returns 配列オブジェクトの場合にtrueを返却 
	 */
	var isArray = function(object) {
		return Object.prototype.toString.call(object) === "[object Array]";
	};

	/**
	 * 文字列の置換を行い、その結果を取得します。<br>
	 * 当メソッドは正規表現は使用しません。<br>
	 * @param source 処理の対象の文字列
	 * @param before 置換前の文字列
	 * @param after 置換後の文字列
	 * @return 置換処理後の文字列
	 */
	var replace = function(source, before, after) {
		if (isEmpty(source)) {
			return "";
		}
		if (source.indexOf(before) < 0) {
			return source;
		}
		var result = "";
		var index = source.indexOf(before);
		result += source.substring(0, index) + after;
		if (index + before.length < source.length) {
			var rest = source.substring(index + before.length, source.length);
			result += replace(rest, before, after);
		}
		return result;
	};

	/**
	 * 文字列内の{n}に対してパラメータ文字列配列順に文字列を挿入して提供します。<br>
	 * パラメータ文字列がnullの場合は空文字を挿入します。<br>
	 * @param source バインド対象文字列
	 * @param params バインド文字列配列(対象バインド分並べて指定します)
	 * @return バインド編集後文字列
	 */
	var bind = function(source, params) {
		if (isEmpty(source)) {
			return "";
		}
		if (params === undefined) {
			return source === undefined ? "" : source;
		}
		var binds = [];
		for (var i = 1; i <= arguments.length - 1; i++) {
			var argument = arguments[i];
			if (argument === undefined) {
				binds.push("");
				continue;
			}
			if (isArray(argument)) {
				for (var j = 0; j <= argument.length - 1; j++) {
					var e = argument[j];
					if (e === undefined) {
						binds.push("");
					} else {
						binds.push(e.toString());
					}
				}
				continue;
			}
			binds.push(argument.toString());
		}
		var render = source;
		for (var i = 0; i <= binds.length - 1; i++) {
			var value = binds[i];
			render = replace(render, "{" + i + "}", value);
		}
		return render;
	};

	/**
	 * クラススクリプト。<br>
	 */
	var Messages = function() {

		/** メッセージ定義情報 */
		var messages = {};

		/**
		 * メッセージ定義を追加します。<br>
		 * @param code メッセージコード
		 * @param level メッセージレベル
		 * @param message メッセージ内容
		 */
		var add = this.add = function(code, level, message) {
			messages[code] = {
				code : code,
				level : level,
				message : message
			};
		};

		/**
		 * メッセージを取得します。<br>
		 * @param code メッセージコード
		 * @param params バインドパラメータ(対象バインド分並べて指定します)
		 * @return バインドパラメータがバインドされたメッセージ文字列
		 */
		var get = this.get = function(code, params) {
			var message = messages[code];
			if (message === undefined) {
				return "";
			}
			var binds = [];
			for (var i = 1; i <= arguments.length - 1; i++) {
				binds.push(arguments[i]);
			}
			return bind(message.message, binds);
		};
	};

	/*
	 * ウィンドウオブジェクトスコープ設定
	 */
	window["Messages"] = Messages;
})(window, document);

/*
 * インスタンス生成
 */
var Messages = new Messages();
