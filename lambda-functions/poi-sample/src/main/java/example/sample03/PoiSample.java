package example.sample03;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * Excelファイルを出力してS3バケットへ格納するLambda関数
 * 
 * @param <Object> インプット（リクエスト）では情報は受け取らないためObjectを指定
 * @param <String> アウトプット（レスポンス）は関数実行後の結果をメッセージ
 */
public class PoiSample implements RequestHandler<Object, String> {

	/**
	 * handleRequestメソッド Lambda関数の実行リクエストを取り扱う
	 * 
	 * @param obj インプットは利用しないためobjを指定
	 * @param context AWS Lambda Context オブジェクト
	 * @return 実行結果
	 */
	@Override
	public String handleRequest(Object obj, Context context) {

		try {

            //　新規ワークブックを作成
            XSSFWorkbook workbook = new XSSFWorkbook();

            //　新規ワークシートを作成
            Sheet sheet1 = workbook.createSheet();

            //　作成したシート名を変更
            workbook.setSheetName(0,"sheet1");    

            //　行オブジェクトの作成
            Row row1 = sheet1.createRow(1);
            Row row2 = sheet1.createRow(2);

            //　セルオブジェクトの作成
            Cell cellA1 = row1.createCell(1);
            Cell cellA2 = row2.createCell(1);

            //　セルに値を設定
            cellA1.setCellValue("This file is created at:");

            Date currentTime = new Date();
            cellA2.setCellValue(currentTime.toString());
        
            //　日付を利用してファイル名を指定
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String fileName = "PoiSample_" + dateFormat.format(currentTime).toString() + ".xlsx";

            //　ファイルオブジェクトに書き込むためのファイル出力ストリームを作成
            FileOutputStream outputFile = new FileOutputStream("/tmp/"+fileName);

            //　ワークブックオブジェクトの内容を書き込み
            workbook.write(outputFile);
            workbook.close();
            
			//　S3にアップロードするために書き込みを行ったファイルをFileクラスに変換
			File file = new File("/tmp/"+fileName);    
            
            // アップロードバケット名
            String bucketName = "put-text-bucket";
		
            // バケットのキー情報（ディレクトリ＋ファイル名）
            String key = fileName;	//　今回はディレクトリ指定無し
		
            // S3にアップロード
            AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("APNortheast1").build();
            s3.putObject(new PutObjectRequest(bucketName, key, file));

            String message = "Excelファイルを格納しました。";			
            return message;
		
		} catch (IOException e) {
			
			System.out.println(e);
			String message = "Excelファイルを格納できませんでした。";			
			return message;
		
		}
	
	}

}