
public class Matrix{
public int rows=0;
public int columns=0;
public double [][]M;
public Matrix(int r,int c)
{
	rows=r;
	columns=c;
	M=new double[rows][columns];
}
public Object add(Object matrix)
{
	Matrix N=(Matrix)matrix;
	Matrix result = new Matrix(rows,columns);
	//result.columns=columns;
	//result.rows=rows;
	double[][]Mresult=new double[rows][columns];
	for(int i=0;i<rows;i++)
	{
		for(int j=0;j<columns;j++)
		{
		    	Mresult[i][j]=M[i][j]+N.M[i][j];
		}
	}
	result.M=Mresult;
	return result;
}


public int SetNumbers(int[]values)
{
	if(values.length>=rows*columns)
	{
		int k=0;
		for(int i=0;i<rows;i++)
		{
			for(int j=0;j<columns;j++)
			{
				M[i][j]=values[k];
				k++;
			}
		}
	
	return 1;
	}
	else
		return 0;
	}
public void print()
{
	for(int i=0;i<rows;i++)
	{
		for(int j=0;j<columns;j++)
		{
			System.out.print(M[i][j]);
			System.out.print(" ");
		}
		 System.out.print("\n");
	}
	
}
public void deepCopyingMatrix(Matrix B) {
	int A_row=this.rows;
	int A_col=this.columns;
	for(int i=0;i<A_row;i++) {
		for(int j=0;j<A_col;j++) {
			this.M[i][j]=B.M[i][j];
		}
	}
}
public Matrix multiply(Matrix B)
{		
	int A_row=this.rows;
	int A_col=this.columns;
	int B_col=B.columns;
	int B_row=B.rows;
	try{
		if(this.columns==B.rows)
		{
			Matrix result=new Matrix(this.rows,B.columns);
			for(int i=0;i<A_row;i++)
			{
				for(int j=0;j<B_col;j++)
				{
					result.M[i][j]=0;
					for(int k=0;k<B_row;k++)
					{
						result.M[i][j]=result.M[i][j]+this.M[i][k]*B.M[k][j];
						//System.out.println(result.M[i][j]);
						
					}
				}
			}
			return result;
		}
		else
		{
			String message ="Exception occured while trying to multiply 2 matrcies of dimensions";
			String messageA=" A("+Integer.toString(A_row)+","+Integer.toString(A_col)+") and";
			String messageB=" B("+Integer.toString(B_row)+","+Integer.toString(B_col)+")";
			message+=messageA+messageB;
			throw new MultiplicationException(message);
		}
	}
	catch(MultiplicationException x) {
		x.message();
	}
	Matrix Merr=new Matrix(0,0);
	return Merr;
	
	
}

final static class MultiplicationException extends Exception
{
	public String errorMessage;
	MultiplicationException(String str){
		this.errorMessage=str;
	}
	public void message() {
		System.out.format("%s%n", this.errorMessage);
	}
	
}



}


















