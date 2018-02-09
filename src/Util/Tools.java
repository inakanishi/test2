package Util;

import java.util.Calendar;

public class Tools {


	public int Str2Int(String str, int def){

		int i = def;

		try{
			i = Integer.parseInt(str);
		}catch(NumberFormatException e){
			new Warning(e + " vals : " + str);
		}

		return i;
	}


	public double Str2Double(String str, double def){

		double j = def;

		try{
			j = Double.parseDouble(str);
		}catch(NumberFormatException e){
			new Warning(e + " vals : " + str);
		}

		return j;
	}


	public boolean strEndWith(String in, String endKey){


		if(in==null || endKey==null){
			new Warning("Input String is null, 'in':" + in + ", endKey:" + endKey);
			return false;
		}

		//new Log(in.substring(in.length()-endKey.length()),3);

		if(!in.substring(in.length()-endKey.length()).equals(endKey))
			return false;

		return true;
	}

	public String strEndCut(String in, String endKey){

		String str = in;

		if(!strEndWith(in,endKey))
			new Warning("Couldn't find endKey, 'in':" + in + ", endKey:" + endKey);
		else
			str = str.substring(0, in.length()-endKey.length());

		return str;
	}

	public String nowTime(){

		 Calendar now = Calendar.getInstance();

		 String y = String.valueOf(now.get(Calendar.YEAR));
		 String mo = String.valueOf(now.get(Calendar.MONTH)+1);
		 String d = String.valueOf(now.get(Calendar.DATE));
		 String h = String.valueOf(now.get(now.HOUR_OF_DAY));
		 String m = String.valueOf(now.get(now.MINUTE));

	     if(Integer.parseInt(mo)<10)
	    	 mo = "0" + mo;

	     if(Integer.parseInt(m)<10)
	    	 m = "0" + m;

	     return y + mo + d + h + m ;
	}


	public String[] combineStr(String[] str1, String[] str2){

		if(str1==null && str2==null)
			return null;
		else if(str1==null)
			return str2;
		else if(str2==null)
			return str1;

		String[] comStr = new String[str1.length+str2.length];

		for(int i=0;i<str1.length;i++)
			comStr[i] = str1[i];
		for(int i=str1.length; i<str1.length+str2.length; i++)
			comStr[i] = str2[i-str1.length];

		return comStr;
	}

	public String[] combineStr(String[] str1, String str2){

		String[] str3 = new String[1];

		str3[0] = str2;

		return combineStr(str1, str3);
	}


	public void wait(int ms){
		
		
		try {
		    Thread.sleep(ms);
		    //new Log("hoge");
		} catch (InterruptedException e) {
		}
		
	}


}
