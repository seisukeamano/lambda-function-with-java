package helloworld;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.util.Map;

/**
 * 受け取った名前情報を利用して挨拶文を返すLambda関数
 * [RequestParam]
 * {
 * "firstName":"first",
 * "lastName":"last"
 * }
 *
 * @param <Map<String, Object>> Lambda関数の実行時にインプット（リクエスト）として受け取るJsonオブジェクト
 * @param <String>     アウトプット（レスポンス）として返答する挨拶文
 */
public class HelloLambda implements RequestHandler<Map<String, Object>, String> {

    /**
     * handleRequestメソッド
     * Lambda関数の実行リクエストを取り扱う
     *
     * @param objectMap InputJsonオブジェクト
     * @param context   AWS Lambda Context オブジェクト
     * @return 挨拶文
     */
    @Override
    public String handleRequest(Map<String, Object> objectMap, Context context) {
        String firstName = objectMap.get("firstName").toString();
        String lastName = objectMap.get("lastName").toString();
        return String.format("Hello %s %s !", firstName, lastName);
    }
}