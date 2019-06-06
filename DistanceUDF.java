import org.apache.hadoop.hive.ql.exec.UDF;
public class DistanceCountDF {
	private final static double PI = 3.14159265358979323;	//PiԲ����
	private final static double R=6371230;	//����뾶-m
	public float Count_DISTANCE(Geo geo_a, Geo geo_b){
			double x, y;
			float dst;	//���룬��Ҫת������
			x=(geo_b.longt-geo_a.longt)*PI*R
					*Math.cos(((geo_a.lat + geo_b.lat)/2)*PI/180)/180;
			y=(geo_b. lat-geo_a. lat)*PI*R/180;
			dst= (float) Math.hypot(x, y);
			return dst;
	}
}
