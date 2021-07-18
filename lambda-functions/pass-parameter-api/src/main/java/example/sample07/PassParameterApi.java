package example.sample07;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class PassParameterApi implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent event, Context context) {

		// インプットパラメーター（StringParameters）を取得
        Map<String, String> inputParams = event.getQueryStringParameters();
        
        
		// APIGatewayへ渡すレスポンスを作成
		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();

		// Base64エンコード（文字種の制限）は無効
		response.setIsBase64Encoded(false);

		// APIのHTTPステータスコードは200（OK）を設定
		response.setStatusCode(200);

		// コンテンツタイプを設定
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "text/html");
		response.setHeaders(headers);

		// レスポンスボディの設定
		response.setBody("<!DOCTYPE html><html><head><title>Hello from AWS Lambda</title></head><body>"
				+ "<h1>Query Parameters:</h1>"
				+ "<p>Key1:"+  inputParams.get("key1")  +"</p>"
				+ "<p>Key2:"+  inputParams.get("key2")  +"</p>"
				+ "<p>Key3:"+  inputParams.get("key3")  +"</p>"
				+ "</body></html>");
		
		return response;
	}
}