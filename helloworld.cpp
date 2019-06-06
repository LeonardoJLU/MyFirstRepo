#include <iostream>
#include <math.h>
using namespace std;
int main(void)
{
	double f(int);		// 声明要用的函数f(x)
	int valueX;			// 计算用的x值
	int n;				// 计算n次
	cout << "请输入n个数" <<endl;
	cin >> n;
	while(n--)			// n次循环，到0终止
	{
		cout << "请输入x的值" <<endl;
		cin >> valueX;
		cout << "Y值为：" << f(valueX) <<endl;
	}
	return 0;
}

double f(int x)
{
	if(x < -2)
	{
		return x*x-sin(x);
	}
	else if(x>=-2 && x<=2) 
	{
		return 2*x+x;
	}
	else 
	{
		return x+1+x*x;
	}
}
