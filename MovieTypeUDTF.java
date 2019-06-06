package com.hive.movie;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.PrimitiveObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;

/**
 * 一条字符串String转换成多条记录
 */
public class MovieTypeUDTF extends GenericUDTF{
	
	private PrimitiveObjectInspector stringOI = null;
	
	/**
	 * 做初始化：
	 * 1、做参数个数的校验
	 * 2、做参数类型的校验
	 * 
	 * 数据样式：21::Get Shorty (1995)::Action|Comedy|Drama
	 */
	@Override
	public StructObjectInspector initialize(ObjectInspector[] argOIs)
			throws UDFArgumentException {
		
		// 先判断参数类型是不是基本类型，
		// 再判断这个基本类型是不是String类型
		if(argOIs[0].getCategory() != ObjectInspector.Category.PRIMITIVE && 
				((PrimitiveObjectInspector)argOIs[0]).getPrimitiveCategory() != PrimitiveObjectInspector.PrimitiveCategory.STRING){
			throw new UDFArgumentException("myudtf need a string parameter");
		}
		
		stringOI = (PrimitiveObjectInspector)argOIs[0];
		// 存储字段名称
		List<String> fieldNames = new ArrayList<String>();
		// 存储字段类型
		List<ObjectInspector> fieldOIs = new ArrayList<ObjectInspector>();
		
		fieldNames.add("movieid");
		fieldNames.add("movietitle");
		fieldNames.add("movietype");
		
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);
		
		return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames, fieldOIs);
	}

	/**
	 * 
	 */
	@Override
	public void process(Object[] args) throws HiveException {
		// TODO Auto-generated method stub
		
		/**
		 * string = 21::Get Shorty (1995)::Action|Comedy|Drama
		 */
		String string = stringOI.getPrimitiveJavaObject(args[0]).toString();
		
		String[] fields = string.split("::");
		String[] movieTypes = fields[2].split("\\|");
		
		int length = movieTypes.length;
		for(int i=0; i<length; i++){
			String[] string2 = new String[]{fields[0],fields[1],movieTypes[i]};
			forward(string2);
		}
	}

	@Override
	public void close() throws HiveException {
		// TODO Auto-generated method stub
	}
}
