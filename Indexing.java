import java.io.*;
import javax.swing.tree.*;
import java.util.*;

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
		
		// create index tree from the bottom up
		System.out.println("creating index tree");
		
		DefaultMutableTreeNode [] indexes = new DefaultMutableTreeNode[bCast.length()];
		System.out.println("leaf level has " + indexes.length + " nodes");
		
		for(int i = 0; i < indexes.length; ++i)
			indexes[i] = new DefaultMutableTreeNode(bCast.charAt(i));
		
		DefaultMutableTreeNode root = joinIndexes(indexes);
		
		printTreeStartingAt(root);
	}
	
	public static DefaultMutableTreeNode joinIndexes(DefaultMutableTreeNode [] indexArr)
	{
		// if there is only one node left to join, it is the root and function is complete
		if(indexArr.length == 1)
			return indexArr[0];
		
		// create a parent node for the children, two at a time, and put them in nextLevel
		DefaultMutableTreeNode [] nextLevel = new DefaultMutableTreeNode[(int)Math.ceil((double)indexArr.length / 2)];
		
		for(int i = 0; i < nextLevel.length; ++i)
		{
			Range r;
			int start, end;
			int childIndex = i * 2;
			boolean twoChildren = (childIndex + 1 < indexArr.length);
			
			// if the nodes have no children, we assume this is the first recursion
			if(indexArr[childIndex].isLeaf())
			{
				// just use the indexes of the pages
				start = childIndex;
				if(!twoChildren)
					end = start;
				else
					end = childIndex + 1;
			}
			else
			{
				// if we are in the interior of the tree, use the ranges from the lower level
				start = ((Range)indexArr[childIndex].getUserObject()).lowerBound();
				if(!twoChildren)
					end = ((Range)indexArr[childIndex].getUserObject()).upperBound();
				else
					end = ((Range)indexArr[childIndex + 1].getUserObject()).upperBound();
			}
			
			// put the indexes into a range, and put the range into the parent node
			r = new Range(start, end);
			nextLevel[i] = new DefaultMutableTreeNode(r);
			nextLevel[i].add(indexArr[childIndex]);
			if(twoChildren)
				nextLevel[i].add(indexArr[childIndex + 1]);
		}
		
		// call the function again to create the next level up
		System.out.println("creating new level with " + nextLevel.length + " nodes");
		
		return joinIndexes(nextLevel);
	}
	
	// prints out the hierarchy of the tree on the screen
	public static void printTreeStartingAt(DefaultMutableTreeNode top)
	{		
		Enumeration e = top.breadthFirstEnumeration();
		for(; e.hasMoreElements();)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			if(node.isLeaf())
				System.out.println(node);
			else
			{
				Range r = (Range)node.getUserObject();
				r.print();
			}
		}
	}
}