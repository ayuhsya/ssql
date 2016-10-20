package supersql.codegenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.channels.FileChannel;

import supersql.codegenerator.HTML.HTMLEnv;
import supersql.codegenerator.Mobile_HTML5.Mobile_HTML5Env;
import supersql.codegenerator.Web.WebEnv;
import supersql.common.GlobalEnv;
import supersql.common.Log;

//added by goto 20141201
public class Jscss implements Serializable {
	public static final String fs = GlobalEnv.OS_FS;
	public static final String outdirPath = GlobalEnv.getOutputDirPath();
	public static final String generateCssFileDir = "jscss";
	public static final String media = CodeGenerator.getMedia().toLowerCase();
	private static boolean flag = false; // masato 20150101
	
	
	public Jscss() {

	}
	
	public static void process() {
		if(!flag){
			flag = true;
			copyJSCSS_to_outputDir();	//goto 20141201
			generateCssFile();			//goto 20141209
		}
	}
	
	//return the CSS file name which will be generated by SSQL
	//0:HTMLEnv, 1:file name
	public static String getGenerateCssFileName(int x) {
		// modified by masato 20151118 little change for eHTML
		// TODO 別ファイルに
		String f = "";
		if(Ehtml.flag || Incremental.flag){
			f = "ssqlResult" + GlobalEnv.getQueryNum();
		} else if ((Ehtml.flag || Incremental.flag) && !GlobalEnv.getoutfilename().isEmpty()) {
			f = GlobalEnv.getoutfilename();
		} else {
			f = new File(GlobalEnv.getfilename()).getName().toString();
		} 
		if(f.contains("."))
			f = f.substring(0, f.lastIndexOf("."));
		if(Ehtml.flag || Incremental.flag) {
			String fileName = GlobalEnv.getoutfilename();
			String phpFileName = fileName.substring(fileName.lastIndexOf(GlobalEnv.OS_FS), fileName.lastIndexOf("."));
			return generateCssFileDir+((x==0)? "/":fs ) + phpFileName + ((x==0)? "/":fs ) + f +".css";
		}
		else return generateCssFileDir+((x==0)? "/":fs )+f+".css";
		
	}
	
	//copy JSCSS to the output directory
	private static void copyJSCSS_to_outputDir() {
		String media = CodeGenerator.getMedia();
		String ep = GlobalEnv.EXE_FILE_PATH;
		
		File from = null;
		// add 20141204 masato for ehtml
		if (media.equalsIgnoreCase("html") || media.equalsIgnoreCase("ehtml") || media.equalsIgnoreCase("web"))
			from = new File(ep+fs+"jscss"+fs+"forHTML"+fs+"jscss");
		else if (media.equalsIgnoreCase("mobile_html5") || media.equalsIgnoreCase("bhtml") || media.equalsIgnoreCase("html_bootstrap"))
			from = new File(ep+fs+"jscss");
		
		if (!directoryCopy(from, new File(outdirPath)))
			Log.err("<<Warning>> Copy JSCSS failed.");
	}
	
	//directoryCopy
	private static Boolean directoryCopy(File fromDir, File toDir) {
		File[] fromFile = fromDir.listFiles();
		toDir = new File(toDir.getPath() + fs + fromDir.getName());
		
		toDir.mkdir();

		if (fromFile != null) {
			for (File f : fromFile) {
				if (f.isFile()) {
					if (!fileCopy(f, toDir)) {
						return false;
					}
				} else {
					if (!directoryCopy(f, toDir)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	//fileCopy
	private static Boolean fileCopy(File file, File dir) {
		File copyFile = new File(dir.getPath() + fs + file.getName());
		
		if (!copyFile.isHidden()) {	//if it's not a hidden file
			FileChannel channelFrom = null;
			FileChannel channelTo = null;
			
			try {
				copyFile.createNewFile();
				channelFrom = new FileInputStream(file).getChannel();
				channelTo = new FileOutputStream(copyFile).getChannel();
				channelFrom.transferTo(0, channelFrom.size(), channelTo);
				return true;
			} catch (IOException e) {
				return false;
			} finally {
				try {
					if (channelFrom != null) {
						channelFrom.close();
					}
					if (channelTo != null) {
						channelTo.close();
					}
					copyFile.setLastModified(file.lastModified());	//copy the update date
				} catch (IOException e) {
					return false;
				}
			}
		}
		return true;
	}

	//generate a CSS file for the generated HTML
	private static void generateCssFile() {
		String css = "";
		if(media.equals("html") || media.equals("ehtml"))
			css = HTMLEnv.commonCSS() + HTMLEnv.css;
		else if(media.equals("mobile_html5"))
			css = Mobile_HTML5Env.commonCSS() + Mobile_HTML5Env.css;
		else if (media.equals("web"))
			css = WebEnv.commonCSS() + WebEnv.css;
		else if (media.equals("bhtml") || media.equals("html_bootstrap")) // 20160603 bootstrap
			css = Mobile_HTML5Env.commonCSS() + Mobile_HTML5Env.css + Sass.compile();
		String outputCssFileName = outdirPath+fs+fs+getGenerateCssFileName(1);
		
		if(!createFile(outputCssFileName, css))
			Log.err("<<Warning>> Generate CSS failed.");
	}
	
	//createFile
	//create a new file to the fileName directory 
	private static boolean createFile(String fileName, String content) {
		
		if(flag || Ehtml.flag){
			File file = new File(fileName.substring(0, fileName.lastIndexOf(GlobalEnv.OS_FS)));
			if ( !file.exists() ) {
				file.mkdirs();
			}
		}
		
		try {
			PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(fileName), "UTF-8")));
			pw.println(content);
			pw.close();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	

	
	
	
//	// 20140625_masato 実習用　コピー元は/home/kyozai/toyama/SuperSQL/jscss/~　と/home/kyozai/toyama/SuperSQL/ssqltool~
//	public static void CopyJscss(){
////		String path = System.getProperty("java.class.path");
////		path = getWorkingDir(path);
//		
//		// コピーするjsファイル　実習専用
//		File jsFile1 = new File("/home/kyozai/toyama/SuperSQL/jscss/jquery.js");
//		File jsFile2 = new File("/home/kyozai/toyama/SuperSQL/jscss/jquery-p.js");
//		File jsFile3 = new File("/home/kyozai/toyama/SuperSQL/jscss/ssql-pagination.js");
//		File cssFile = new File("/home/kyozai/toyama/SuperSQL/jscss/ssql-pagination.css");
//
//		File destPath1 = new File(GlobalEnv.getoutdirectory() + GlobalEnv.OS_FS + "jscss" + GlobalEnv.OS_FS + "jquery.js");
//		File destPath2 = new File(GlobalEnv.getoutdirectory() + GlobalEnv.OS_FS + "jscss" + GlobalEnv.OS_FS + "jquery-p.js");
//		File destPath3 = new File(GlobalEnv.getoutdirectory() + GlobalEnv.OS_FS + "jscss" + GlobalEnv.OS_FS + "ssql-pagination.js");
//		File destPath4 = new File(GlobalEnv.getoutdirectory() + GlobalEnv.OS_FS + "jscss" + GlobalEnv.OS_FS + "ssql-pagination.css");
//		
//		copyTransfer(jsFile1, destPath1);
//		copyTransfer(jsFile2, destPath2);
//		copyTransfer(jsFile3, destPath3);
//		copyTransfer(cssFile, destPath4);
//	}
//	
////	public static String getWorkingDir(String path){
////		String workingDir = new File(path).getAbsolutePath(); // 実行jarファイルの絶対パスを取得
////		if (!System.getProperty("os.name").contains("Windows") && workingDir.contains(":")) {// ビルドバスの追加を行うと参照ライブラリ内のファイルのパスも付け加えてしまう仕様らしいので、:移行カット
////				workingDir = workingDir.substring(0, workingDir.indexOf(":"));
////		}
////		if (workingDir.endsWith(".jar")) { // jarファイルを実行した場合（Eclipseから起動した場合は入らない）
////			workingDir = workingDir.substring(0, workingDir.lastIndexOf(GlobalEnv.OS_FS));
////		}
////		return workingDir;
////	}
//	
//	public static void copyTransfer(final File src, final File dest) {
//		try {
//			File tmp = new File(dest.toString().substring(0, dest.toString().lastIndexOf("/")));
//			if (src.isDirectory()) {
//				// ディレクトリがない場合、作成
//				if(!dest.exists()){
//					dest.mkdir();
//				}
//	
//				String[] files = src.list();
//				for (String file : files) {
//					File srcFile = new File(src, file);
//					File destFile = new File(dest, file);
//					if(!srcFile.isHidden()){	//隠しファイルではない場合
//						copyTransfer(srcFile, destFile);
//					}
//				}
//			} else {
//				if(!tmp.exists()){
//					tmp.mkdir();
//				}
//				
//				FileChannel srcChannel = new FileInputStream(src).getChannel();
//				FileChannel destChannel = new FileOutputStream(dest).getChannel();
//				try {
//					srcChannel.transferTo(0, srcChannel.size(), destChannel);
//				} catch(Exception e){
//					System.err.println(e);
//				} finally {
//					srcChannel.close();
//					destChannel.close();
//				}
//			}
//		}catch (Exception e) {
//			//e.printStackTrace();
//		}
//	}
}
