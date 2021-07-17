package example.sample04;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

/**
 * リクエストで受け取った社員情報をExcelファイルに出力してS3バケットへ格納するLambda関数
 * 
 * @param <Object> インプット（リクエスト）では情報は受け取らないためObjectを指定
 * @param <String> アウトプット（レスポンス）は関数実行後の結果をメッセージ
 */
public class EmployeeInfoOutput implements RequestHandler< Map<String, ArrayList<EmployeeInfo>>, String> {


/**
 * MapのArrayListで取るパターン
 * public class EmployeeInfoOutput implements RequestHandler< Map<String, ArrayList<Map<String, String>>>, String> {
 */

	
	/**
	 * AWS Lambda のハンドラーメソッド
	 *
	 * @param empInfoList JSON配列で受け取るインプット（リクエスト）
	 * @param context AWS Lambda Context オブジェクト
	 * @return 出力結果
	 */
	@Override
	public String handleRequest(Map<String, ArrayList<EmployeeInfo>> event, Context context) {

		ArrayList<EmployeeInfo> empInfo = event.get("data");
		String name1 = empInfo.get(0).name;
		System.out.println("name1: "+ name1);
				
		
/**	
 * MapのArrayListで取るパターン

	public String handleRequest(Map<String, ArrayList<Map<String,String>>> event, Context context) {

		// TODO : JSON配列を取得できてない・・
		
		ArrayList<Map<String, String>> empInfo = event.get("data");
		
		String name1 = empInfo.get(0).get(0) ;		
		System.out.println("name1: "+ name1);

*/
		
		try {
			// 新規ワークブックを作成
			XSSFWorkbook workbook = Const.createWorkbook();

/**
ArrayList<EmoloyeeInfo>で取ろうとしたけどこれだと取れないので一時コメントアウト

			// ワークブックに社員情報を記入してファイルを作成
			File file = Const.writeEmpInfo(workbook, empInfoList);

			// Excelファイルを出力
			Const.upload(file);

			String message = "Excelファイルを作成しました。"+" List size:"+empInfoList.size();
*/

			String message = "Excelファイルを作成しました。";
			
			return message;

		} catch (Exception e) {
	
			e.printStackTrace();
			String message = "Excelファイルを作成できませんでした。";
			return message;

		}
	}

}


class Data{
	List<EmployeeInfo> empInfoList;
}

/**
 * 社員情報を格納するためのクラス
 */
class EmployeeInfo {

	// 氏名
	String name;

	// ふりがな
	String furigana;

	// 性別
	String gender;

	// 年齢
	String age;

	// 生年月日
	String birthday;

	// 部署
	String department;

	// 職種
	String jobRole;

	// 居住エリア
	String city;

	// 興味分野
	String interest;

}

/**
 * Excelファイルの作成メソッド
 * 
 * @return ワークブックオブジェクト
 */
class Const {

	//　セルの枠線スタイル
	private static XSSFCellStyle style;

	//　見出しを設定する行
	private static int headderRowNum = 1;

	//　見出しに設定する項目
	private static String[] headderItems = { "名前", "かな", "性別", "年齢", "誕生日", "部署", "職種", "地域", "興味分野" };

	//　出力するファイル名
	private static String fileName;

	//　アップロードバケット名
    private static String bucketName = "put-text-bucket";

	/**
	 * セルの枠線スタイルを設定するメソッド
	 * 
	 * @param workbook スタイルを利用するワークブック
	 */
	public static void setStyle(XSSFWorkbook workbook) {

		// セルのスタイルオブジェクトを作成
		style = workbook.createCellStyle();

		// 枠線の種類を指定
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);

		// 枠線の色を指定
		style.setTopBorderColor(IndexedColors.BLACK.getIndex());
		style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		style.setRightBorderColor(IndexedColors.BLACK.getIndex());
	}

	/**
	 * 見出しをつけてExcelファイルを作成するメソッド
	 * 
	 * @return ワークブックオブジェクト
	 */
	public static XSSFWorkbook createWorkbook() {

		// 新規ワークブックを作成
		XSSFWorkbook workbook = new XSSFWorkbook();

		// 新規ワークシートを作成
		XSSFSheet sheet1 = workbook.createSheet();

		// 作成したシート名を変更
		workbook.setSheetName(0, "sheet1");

		// 見出しの作成
		// 見出し行オブジェクトの作成
		XSSFRow headderRow = sheet1.createRow(headderRowNum);

		// 見出しセルを管理するリスト
		ArrayList<Cell> headderCells = new ArrayList<>();

		// 見出しセルオブジェクトの作成
		for (int i = 0; i < headderItems.length; i++) {
			Cell headderCell = headderRow.createCell(i);
			headderCells.add(headderCell);
		}

		// 枠線スタイルの利用準備
		setStyle(workbook);

		int counter = 0;
		
		// 見出しセルに値と枠線を設定
		for (Cell cell : headderCells) {
			cell.setCellValue(headderItems[counter]);
			cell.setCellStyle(style);
			counter++;
		}
		return workbook;
	}

	/**
	 * Excelワークブックに社員情報を書き込んでファイルを作成するメソッド
	 * 
	 * @param workbookFormat 見出しの記入されたExcelワークブック
	 * @param empInfoList    書き込みを行う社員情報リスト
	 * @return ファイルオブジェクト
	 * @throws Exception 
	 */
	public static File writeEmpInfo(XSSFWorkbook workbookFormat, ArrayList<EmployeeInfo> empInfoList) throws Exception {

		// 見出しの記入されたExcelワークブックを準備
		XSSFWorkbook workbook = workbookFormat;

		// 社員情報の記入を行うシートを準備
		Sheet sheet1 = workbook.getSheetAt(0);

		// 出力開始行
		int outputRowNum = headderRowNum + 1;

		// employeeInfoListに格納されている社員情報を出力
		for (EmployeeInfo empInfo : empInfoList) {

			// 出力を行う社員情報をリストに格納
			List<String> empInfoValues = new ArrayList<String>(
					Arrays.asList(empInfo.name, empInfo.furigana, empInfo.gender, empInfo.age, empInfo.birthday,
							empInfo.department, empInfo.jobRole, empInfo.city, empInfo.interest));

			// 出力対象となる行オブジェクトの用意
			Row outputRow = sheet1.createRow(outputRowNum);

			// 出力対象となるセルを管理するリスト
			ArrayList<Cell> outputCells = new ArrayList<>();

			// 出力対象となるセルオブジェクトの作成
			for (int i = 0; i < empInfoValues.size(); i++) {
				Cell outputCell = outputRow.createCell(i);
				outputCells.add(outputCell);
			}

			// 枠線スタイルの利用準備
			setStyle(workbook);

			int counter = 0;
			
			// セルに値を出力
			for (Cell cell : outputCells) {
				cell.setCellValue(empInfoValues.get(counter));
				cell.setCellStyle(style);
				counter++;
			}

			// 出力行をスライド
			outputRowNum++;

		}

        //　日付を利用してファイル名を指定
        Date currentTime = new Date();    
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        fileName= "EmployeeInfo_" + dateFormat.format(currentTime).toString() + ".xlsx";

        //　ファイルオブジェクトに書き込むためのファイル出力ストリームを作成
        FileOutputStream outputFile = new FileOutputStream("/tmp/"+fileName);

        //　ワークブックオブジェクトの内容を書き込み
        workbook.write(outputFile);
        workbook.close();

		//　S3にアップロードするために書き込みを行ったファイルをFileクラスに変換
		File file = new File("/tmp/"+fileName);

		return file;
	}

	
	/**
	 * Excelファイルに現在時刻を利用したファイル名を設定してS3にアップロードするメソッド
	 * 
	 * @param file 出力するExcelファイル
	 */
	public static void upload(File file) {
	
        //　バケットのキー情報（ディレクトリ＋ファイル名）
        String key = fileName;    // 今回はディレクトリ指定無し
	
        //　S3にアップロード
        AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("APNortheast1").build();
        s3.putObject(new PutObjectRequest(bucketName, key, file));

	}
}
