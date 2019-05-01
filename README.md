`リポジトリの主な目的は個人的なシステム構築時の実装効率を上げるためのライブラリ管理のため網羅的なテスト実施はしていません`

# Ideaccum Commons Message
Commons Messageは、外部定義されたメッセージ定義情報を利用する際の操作を標準化するためのクラスが提供されるパッケージです。  

外部定義されたメッセージを利用する処理においては、単純にプロパティリソースからのアクセスで特にラップするまでの処理は通常発生しません。  
このパッケージでは、メッセージ定義コード体系に規約を持たせ、それぞれのメッセージにおけるメッセージレベルをコード体系で標準化します。  
メッセージコード体系を標準化することで、メッセージを利用する外部の実装においてユーザーが利用したメッセージがエラー系のメッセージであるか、情報系のメッセージであるかを形式的に判断し、メッセージ出力処理時のメッセージレベルごとの処理分岐を単純化させることを目的として作成されました。  

- 標準化されたメッセージレベルのコード体系  
  Commons Messageではメッセージコードのサフィックスでメッセージレベルを表現するコード体系を規約化しています。  
  メッセージを定義するプロパティリソースでは、下記のようにコードを定義します。  

  ```
  MSG0001-E=エラーメッセージ。
  MSG0002-W=警告メッセージ。
  MSG0003-I=情報メッセージ。
  MSG0004-D=デバッグメッセージ。
  MSG0005-T=トレースメッセージ。
  MSG0006-H=非表示内部メッセージ。
  ```

- メッセージレベルを考慮したメッセージの利用  
  コード体系化したメッセージにより下記のように利用者側で該当のメッセージを利用して上位に例外をスローした場合、上位ではエラーレベルのメッセージがスローされたのか、情報レベルのメッセージがスローされたのかなどの判断を行えます。  
  下記は例外を用いた極端な例となりますが、メッセージ定義側にメッセージレベル定義情報を持たせることで、メッセージごとの業務実装分岐を軽減することを目的としています(ログ出力などにおいてもメッセージによって出力先を分岐させる際にログ出力メソッド自体の切り替えを上位で吸収することなどを想定しています)。  

  ```java
  private void test() {
    try {
      messageThrow();
    } catch(SampleException e) {
      Message message = e.getMessage();
      if (message.getLevel() == MessageLevel.ERROR) {
        // エラーメッセージ時の処理
      } else {
        // エラーメッセージ以外時の処理
      }
    }
  }

  private void messageThrow() throws SampleException {
    Message message = Messages.instance().get("MSG0001");
    throw new SampleException(message);
  }
  ```

## Documentation
ライブラリに関するAPI仕様は各クラスのJavadocにて記載しています。  

## Source Code
最新のプログラムソースはすべて[GitHub](https://github.com/ideaccum/org.ideaccum.libs.commons.message)で管理しています。  

## Dependent Libraries
このライブラリパッケージの依存ライブラリ及び、ライセンスは[LIBRARIES.md](https://github.com/ideaccum/org.ideaccum.libs.commons.message/blob/master/LIBRARIES.md)に記載しています。  

## License
プログラムソースは[MIT License](https://github.com/ideaccum/org.ideaccum.libs.commons.message/blob/master/LICENSE.md)です。  

## Copyright
Copyright (c) 2018 Hisanori Kitagawa  

## Other
2010年より[SourceForge.jp](https://osdn.net/projects/phosphoresce/)にて公開していたリポジトリ上のWebcoreパッケージから分岐／移行し、更新しているライブラリとなります。  
