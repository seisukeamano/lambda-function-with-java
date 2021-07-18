package example.sample06;

import java.util.HashMap;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class FileOutputApi implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

		// APIGatewayへ渡すレスポンスを作成
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		// Base64エンコード（文字種の制限）は無効
		response.setIsBase64Encoded(false);

		// APIのHTTPステータスコードは200（OK）を設定
		response.setStatusCode(200);

		// コンテンツタイプを設定
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/csv");
//	headers.put("Content-Disposition", "attachment; filename=\"aaa.csv\"");
		response.setHeaders(headers);

		// レスポンスボディの設定
		response.setBody("Hello from Lambda!");

		return response;
	}
}
