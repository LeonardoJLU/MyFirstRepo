package com.hive.test;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDF;
import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.serde2.lazy.LazyString;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.StringObjectInspector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

/**
 * 数据格式 人 时间 地点
 * 
 */
public class HelloUDF extends GenericUDF {
	// 输入变量定义
	private ObjectInspector peopleObj;
	private ObjectInspector timeObj;
	private ObjectInspector placeObj;
	// 之前记录保存
	String strPreTime = "";
	String strPrePlace = "";
	String strPrePeople = "";

	@Override
	// 1.确认输入类型是否正确
	// 2.输出类型的定义
	public ObjectInspector initialize(ObjectInspector[] arguments) throws UDFArgumentException {
		peopleObj = (ObjectInspector) arguments[0];
		timeObj = (ObjectInspector) arguments[1];
		placeObj = (ObjectInspector) arguments[2];
		// 输出结构体定义
		ArrayList<String> structFieldNames = new ArrayList<String>();
		ArrayList<ObjectInspector> structFieldObjectInspectors = new ArrayList<ObjectInspector>();
		structFieldNames.add("people");
		structFieldNames.add("day");
		structFieldNames.add("from_time");
		structFieldNames.add("from_place");
		structFieldNames.add("to_time");
		structFieldNames.add("to_place");

		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);
		structFieldObjectInspectors.add(PrimitiveObjectInspectorFactory.writableStringObjectInspector);

		StructObjectInspector si2;
		si2 = ObjectInspectorFactory.getStandardStructObjectInspector(structFieldNames, structFieldObjectInspectors);
		return si2;
	}

	// 遍历每条记录
	@Override
	public Object evaluate(DeferredObject[] arguments) throws HiveException {
		LazyString LPeople = (LazyString) (arguments[0].get());
		String strPeople = ((StringObjectInspector) peopleObj).getPrimitiveJavaObject(LPeople);

		LazyString LTime = (LazyString) (arguments[1].get());
		String strTime = ((StringObjectInspector) timeObj).getPrimitiveJavaObject(LTime);

		LazyString LPlace = (LazyString) (arguments[2].get());
		String strPlace = ((StringObjectInspector) placeObj).getPrimitiveJavaObject(LPlace);

		Object[] e;
		e = new Object[6];

		try {
			// 如果是同一个人，同一天
			if (strPrePeople.equals(strPeople) && IsSameDay(strTime)) {
				e[0] = new Text(strPeople);
				e[1] = new Text(GetYearMonthDay(strTime));
				e[2] = new Text(GetTime(strPreTime));
				e[3] = new Text(strPrePlace);
				e[4] = new Text(GetTime(strTime));
				e[5] = new Text(strPlace);
			} else {
				e[0] = new Text(strPeople);
				e[1] = new Text(GetYearMonthDay(strTime));
				e[2] = new Text("null");
				e[3] = new Text("null");
				e[4] = new Text(GetTime(strTime));
				e[5] = new Text(strPlace);
			}
		} catch (java.text.ParseException ex) {
		}

		strPrePeople = new String(strPeople);
		strPreTime = new String(strTime);
		strPrePlace = new String(strPlace);

		return e;
	}

	@Override
	public String getDisplayString(String[] getString) {
		assert (getString.length > 0);

		StringBuilder sb = new StringBuilder();
		sb.append("helloGenericUDF(");
		sb.append(getString[0]);
		sb.append(")");

		return sb.toString();
	}

	// 比较相邻两个时间段是否在同一天
	private boolean IsSameDay(String strTime) throws java.text.ParseException {
		if (strPreTime.isEmpty()) {
			return false;
		}
		String curDay = GetYearMonthDay(strTime);
		String preDay = GetYearMonthDay(strPreTime);
		return curDay.equals(preDay);
	}

	// 获取年月日
	private String GetYearMonthDay(String strTime) throws java.text.ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = df.parse(strTime);
		df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(curDate);
	}

	// 获取时间
	private String GetTime(String strTime) throws java.text.ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date curDate = df.parse(strTime);
		df = new SimpleDateFormat("HH:mm:ss");
		return df.format(curDate);
	}

}
