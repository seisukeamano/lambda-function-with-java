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
import org.apache.poi.ss.usermodel.Font;
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
 * @param <Map<String, Object>> インプット（リクエスト）のJSON配列の形式
 * @param <String>     アウトプット（レスポンス）は関数実行後の結果
 */
public class EmployeeInfoOutput implements RequestHandler<Map<String, Object>, String> {

	/**
	 * AWS Lambda のハンドラーメソッド
	 *
	 * @param event   JSON配列で受け取るインプット（リクエスト）
	 * @param context AWS Lambda Context オブジェクト
	 * @return 実行結果
	 */
	@Override
	public String handleRequest(Map<String, Object> empInfo, Context context) {

		// 受け取るJSONから社員情報の部分を取得してArrayListに格納
		ArrayList<Map<String, String>> empInfoList = (ArrayList<Map<String, String>>) empInfo.get("data");

		try {
			// ワークブックに社員情報を記入してファイルを作成
			File file = Const.writeEmpInfo(empInfoList);

			// ExcelファイルをS3にアップロード
			Const.upload(file);

			// 実行結果を返答
			String message = "Excelファイルを作成しました。";
			return message;

		} catch (Exception e) {
			//　例外を出力
			e.printStackTrace();

			// 実行結果を返答
			String message = "Excelファイルを作成できませんでした。";
			return message;
		}
	}

}

/**
 * Excelファイルの作成メソッド
 * 
 * @return ワークブックオブジェクト
 */
class Const {

	// 出力するファイルの名前
	private static String fileName;

	// シート１の名前
	private static String sheetName1 = "sheet1";
	
	// 見出しを設定する行
	private static int headderRowNum = 1;

	// 見出しに設定する項目
	private static String[] headderItems = { "名前", "かな", "性別", "年齢", "誕生日", "部署", "職種", "地域", "興味分野" };

	// セルのスタイル
	private static XSSFCellStyle style;

	// セルのフォント
	private static Font font;

	// フォントの種類
	private static String fontType = "游ゴシック";
	
	// アップロード対象のS3バケット名
	private static String bucketName = "put-text-bucket";

	/**
	 * セルのフォントを指定するメソッド
	 * 
	 * @param workbook スタイルを利用するワークブック
	 */
	public static void setFont(XSSFWorkbook workbook, XSSFCellStyle style) {

		// フォントオブジェクトを作成
		font = workbook.createFont();
		
		// フォントの種類を指定
		font.setFontName(fontType);
		
		// セルのスタイルにフォントを指定
		style.setFont(font);
	}
	
	/**
	 * セルの枠線スタイルを指定するメソッド
	 * 
	 * @param workbook スタイルを利用するワークブック
	 */
	public static void setCellBorder(XSSFWorkbook workbook, XSSFCellStyle style) {

		// セルのスタイルに枠線の種類を指定
		style.setBorderTop(BorderStyle.MEDIUM);
		style.setBorderBottom(BorderStyle.MEDIUM);
		style.setBorderLeft(BorderStyle.MEDIUM);
		style.setBorderRight(BorderStyle.MEDIUM);

		// セルのスタイルに枠線の色を指定
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
		workbook.setSheetName(0, sheetName1);

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

		// セルのスタイルオブジェクトを作成
		style = workbook.createCellStyle();

		//　フォントと枠線の指定
		setFont(workbook, style);
		
		// 枠線スタイルの指定
		setCellBorder(workbook, style);

		int counter = 0;

		// 見出しセルに値と枠線を設定
		for (Cell cell : headderCells) {
			cell.setCellStyle(style);
			cell.setCellValue(headderItems[counter]);
			counter++;
		}
		return workbook;
	}

	/**
	 * Excelワークブックに社員情報を書き込んでファイルを作成するメソッド
	 * 
	 * @param empInfoList    書き込みを行う社員情報リスト
	 * @return File 		 ファイルオブジェクト
	 * @throws Exception 
	 */
	public static File writeEmpInfo(ArrayList<Map<String, String>> empInfoList) throws Exception {

		// 見出しの記入されたExcelワークブックを準備
		XSSFWorkbook workbook = createWorkbook();

		// 社員情報の記入を行うシートを準備
		XSSFSheet sheet1 = workbook.getSheetAt(0);

		// 出力開始行
		int outputRowNum = headderRowNum + 1;

		// employeeInfoListに格納されている社員情報を出力
		for (Map<String, String> empInfo : empInfoList) {

			// 出力を行う社員情報をリストに格納
			List<String> empInfoValues = new ArrayList<String>(Arrays.asList
												(empInfo.get("name"), 
												 empInfo.get("furigana"), 
												 empInfo.get("gender"), 
												 empInfo.get("age"), 
												 empInfo.get("birthday"),
												 empInfo.get("department"), 
												 empInfo.get("jobRole"),
												 empInfo.get("city"), 
												 empInfo.get("interest")));

			// 出力対象となる行オブジェクトの用意
			Row outputRow = sheet1.createRow(outputRowNum);

			// 出力対象となるセルを管理するリスト
			ArrayList<Cell> outputCells = new ArrayList<>();

			// 出力対象となるセルオブジェクトの作成
			for (int i = 0; i < empInfoValues.size(); i++) {
				Cell outputCell = outputRow.createCell(i);
				outputCells.add(outputCell);
			}

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

		// 値を入力した列の列幅を自動調整
//		for(int i =0; i<headderItems.length; i++) {
//			sheet1.autoSizeColumn(i, true);
//		}
		
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

		// バケットのキー情報 （ディレクトリ＋ファイル名）
		String key = fileName;		// 今回はディレクトリ指定無し

		// ファイルをS3にアップロード
		AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion("APNortheast1").build();
		s3.putObject(new PutObjectRequest(bucketName, key, file));

	}
}
