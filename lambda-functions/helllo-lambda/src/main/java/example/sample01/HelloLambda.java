package example.sample01;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

/**
 * 受け取った名前情報を利用して挨拶文を返すLambda関数
 * 
 * @param <UserName> Lambda関数の実行時にインプット（リクエスト）として受け取る名前
 * @param <String>  アウトプット（レスポンス）として返答する挨拶文
 */
public class HelloLambda implements RequestHandler<UserName, String> {

	/**
	 * handleRequestメソッド
	 * Lambda関数の実行リクエストを取り扱う
	 * @param username 名前
	 * @param context AWS Lambda Context オブジェクト
	 * @return 挨拶文
	 */
	@Override
	public String handleRequest(UserName username, Context context) {

		String greeting = String.format("Hello %s %s !", username.firstName, username.lastName);
		return greeting;
	}

}

/**
 * 名前の情報を格納するクラス
 */
class UserName {

	//名
	public String firstName;
	
	//姓
	public String lastName;
}