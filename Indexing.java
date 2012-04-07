import java.io.*;

class Indexing
{
	public static void main(String args[])
	{
		String bCast = "";
		
		// make sure file name is supplied
		if(args.length != 1)
		{
			System.err.println("Usage: java Indexing <filename>");
			return;
		}
	
		try{
			// Open the file that is the first 
			// command line parameter
			FileInputStream fstream = new FileInputStream(args[0]);
			
			// Get the object of DataInputStream
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			
			//Read brodacast schedule
			bCast = br.readLine();
			
			System.out.println("The broadcast schedule is " + bCast);
			
			//Close the input stream
			in.close();
		}catch (Exception e){//Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
		
		// determine the depth of index tree
		System.out.println("depth of index tree");
		System.out.println("k = ceil(lg(|D|))");
		System.out.println("k = ceil(lg(" + bCast.length() + "))");
		int k = (int)Math.ceil(Math.log(bCast.length()) / Math.log(2));
		System.out.println("k = " + k);
		
		
	}
}