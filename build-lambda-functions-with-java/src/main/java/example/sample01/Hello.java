package example.sample01;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Hello implements RequestHandler<UserName, String> {

	/**
	 * AWS Lambda のハンドラーメソッド
	 *
	 * @param username ユーザー情報（インプットとして関数の実行時に渡された値）
	 * @param context AWS Lambda Context オブジェクト
	 * @return 出力データ
	 */
	@Override
	public String handleRequest(UserName username, Context context) {
		
		String greeting = String.format("Hello %s %s !", username.firstName, username.lastName);
		return greeting;
		
	}

}
