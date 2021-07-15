package example.sample02;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * テキストファイルを出力してS3バケットへ格納するLambda関数
 * 
 * @param <Object> インプット（リクエスト）として情報は受け取らないためObjectを指定
 * @param <String> 関数の実行結果をアウトプット（レスポンス）
 */
public class PutText implements RequestHandler<Object, String> {

	/**
	 * handleRequestメソッド
	 * Lambda関数の実行リクエストを取り扱う
	 * @param obj インプットは利用しないためobjを指定
	 * @param context AWS Lambda Context オブジェクト
	 * @return 実行結果
	 */
	@Override
	public String handleRequest(Object obj, Context context) {

		try {
			
			// テキストファイルの作成（Lambdaの実行環境に作成するので\tmp\）
			File file = new File("/tmp/sample.txt");
			
			// テキストファイルへの書き込み
			FileWriter writer = new FileWriter(file);
			writer.write("This text file is generated by lambda function.");
			writer.close();
			
			// アップロードバケット名
			String bucketName = "put-text-bucket";
			
			// バケットのキー情報（ディレクトリ＋ファイル名）
			String key = "sample.txt";
			
			// S3にアップロード
			AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("APNortheast1").build();
			s3.putObject(new PutObjectRequest(bucketName, key, file));

			String message = "ファイルを格納しました。";			
			return message;
			
		} catch (IOException e) {
			
			System.out.println(e);
			String message = "ファイルを格納できませんでした。";			
			return message;
		
		}
	}
}
