package com.github.monkey.analyzer;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.github.monkey.analyzer.analyze.AnalyzerClient;
import com.github.monkey.analyzer.analyze.Constants;
import com.github.monkey.analyzer.mail.MailSender;
import com.github.monkey.analyzer.model.Abnormality;
import com.github.monkey.analyzer.report.Abnormalities2JSONReport;
import com.github.monkey.analyzer.report.JSONReport2HtmlReport;
import com.github.monkey.analyzer.statistics.AbnormalitiesAnalyzerWrapper;

/**
 * 
 * @author Alex Chen (apack1001@gmail.com)
 *
 */
public class Main {
	public static void main(String[] args) {
		CLIParser cli = new CLIParser();
		boolean success = cli.parse(args);
		if (!success)
			return;
		
		for (String dir : cli.workspaces) {
			AnalyzerClient client = new AnalyzerClient();

			//初始化log解析器
			client.analyze(dir,
					cli.monkeyLogFileName, 
					cli.bugreportFileName, 
					cli.tracesFileName,
					cli.logcatFileName, 
					cli.propertiesName, 
					cli.pkgName
				);
			
			final ArrayList<Abnormality> knownAbnormalities = client.getKnownAbnormalities();
			final ArrayList<Abnormality> unknownAbnormalities = client.getUnknownAbnormalities();

			//得到log下所有文件的数目
			final int count = client.getAbnormalitiesDirectoriesCount(dir);
			double duration = 0;
			try {
				duration = Double.parseDouble(cli.duration);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			double avg = AbnormalitiesAnalyzerWrapper.getAverage(
					knownAbnormalities, 
					unknownAbnormalities, 
					duration
				);
			HashMap<String, String> info = fillTestInfo(avg, cli.duration);

			String result = Abnormalities2JSONReport.toJSONFormatStringReport(
					knownAbnormalities, 
					unknownAbnormalities, 
					info, 
					duration,
					count);

			JSONReport2HtmlReport.toHTMLReport(result, 
					dir + File.separator + "index.html", "gbk");
			JSONReport2HtmlReport.toHTMLReport(result, 
					dir + File.separator + "index_utf8.html", "utf-8");
		}

		//读取那个html文件并发送报告出来
		File file = new File(cli.workspaces[0] +File.separator+"index_utf8.html");
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader=null;
		StringBuilder buffer=null;
		String htmlResult=null;
		try {
			 inputStreamReader = new InputStreamReader(new FileInputStream(file),"utf-8");
			 bufferedReader = new BufferedReader(inputStreamReader);
			 buffer = new StringBuilder();
			String data=null;
			while((data = bufferedReader.readLine())!=null){
				buffer.append(data);
			}
			htmlResult = buffer.toString();
			inputStreamReader.close();
			bufferedReader.close();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String host = "smtp.jd.com";
		String userName = "chenruikun";
		String passWord = "aSDFqWER!234";
		MailSender mailSender = new MailSender(host,userName,passWord);
		System.out.println("Sending mail.......");
		mailSender.sendMail("chenruikun@jd.com","Monkey测试报告",htmlResult);
	}

	private static HashMap<String, String> fillTestInfo(double average, String duration) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.put(Constants.JSONReport.KEY_DURATION, duration);
		hm.put(Constants.JSONReport.KEY_AVERAGE, "" + average);
		return hm;
	}
}