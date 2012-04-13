import java.io.*;
import javax.swing.tree.*;
import java.util.*;

class Indexing
{
	public static void main(String args[])
	{
		int replicationLevel = 2;
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
		
		DefaultMutableTreeNode [] dataPages = new DefaultMutableTreeNode[bCast.length()];
		System.out.println("leaf level has " + dataPages.length + " nodes");
		
		for(int i = 0; i < dataPages.length; ++i)
			dataPages[i] = new DefaultMutableTreeNode(bCast.charAt(i));
		
		DefaultMutableTreeNode root = joinIndexes(dataPages, bCast);
		
		// create new broadcast schedule that includes indexes
		ArrayList<DefaultMutableTreeNode> schedule = new ArrayList<DefaultMutableTreeNode>();
		
		ArrayList<DefaultMutableTreeNode> replicationLevelNodes = new ArrayList<DefaultMutableTreeNode>();
		Enumeration e = root.breadthFirstEnumeration();
		while(e.hasMoreElements())
		{
			DefaultMutableTreeNode m = (DefaultMutableTreeNode)e.nextElement();
			if(m.getDepth() == replicationLevel)
				replicationLevelNodes.add(m);
		}
		
		ArrayList<Integer> controlIndexes = new ArrayList<Integer>();
		DefaultMutableTreeNode currentNode, nextNode;
		
		// sets up the schedule based on the replication level
		for(int i = 0; i < replicationLevelNodes.size(); ++i)
		{
			currentNode = replicationLevelNodes.get(i);
			e = currentNode.breadthFirstEnumeration();
			
			if(i == 0)
			{
				TreeNode [] path = replicationLevelNodes.get(0).getPath();
				for(int b = 0; b < path.length - 1; ++b)
					schedule.add((DefaultMutableTreeNode)path[b]);
			}	
			
			while(e.hasMoreElements())
				schedule.add((DefaultMutableTreeNode)(e.nextElement()));
			
			if(i != replicationLevelNodes.size() - 1)
			{
				nextNode = replicationLevelNodes.get(i + 1);
				
				while(e.hasMoreElements())
					schedule.add((DefaultMutableTreeNode)(e.nextElement()));
				
				// replicate all index pages between nextNode and lca(currentNode, nextNode)
				DefaultMutableTreeNode lca = (DefaultMutableTreeNode)currentNode.getSharedAncestor(nextNode);
				e = nextNode.pathFromAncestorEnumeration(lca);
				while(e.hasMoreElements())
				{
					DefaultMutableTreeNode aNode = (DefaultMutableTreeNode)e.nextElement();
					if(aNode != nextNode)
						schedule.add(aNode);
				}
			}
		}
		
		// for control indexes
		// find the next page that has the start index of this index page
		for(int i = 0; i < schedule.size(); ++i)
		{
			if(schedule.get(i).isLeaf())
				controlIndexes.add(-1);
			else if(!schedule.get(i).isLeaf())
			{
				int j = 1;
				boolean done = false;
				while(!done)
				{
					if(schedule.get(i + j).isLeaf())
					{
						char page = schedule.get(i + j).toString().charAt(0);
						if(page == bCast.charAt(((Range)(schedule.get(i).getUserObject())).lowerBound()))
						{	
							controlIndexes.add(i + j);
							done = true;
						}
					}
					else
					{
						Range range = (Range)schedule.get(i).getUserObject();
						if(range.contains(((Range)(schedule.get(i + j).getUserObject())).lowerBound()))
						{
							controlIndexes.add(i + j);
							done = true;
						}
					}
					
					++j;
					
					if(i + j == schedule.size())
					{
						controlIndexes.add(0);
						done = true;
					}
				}
			}
		}
		
		// output the schedule
		System.out.println("\nschedule index\t\tdata\t\tcontrol index");
		for(int i = 0; i < schedule.size(); ++i)
		{
			String printMe = Integer.toString(i) + "\t\t\t" + schedule.get(i).toString() + "\t\t\t";
			if(controlIndexes.get(i) != -1)
				printMe += Integer.toString(controlIndexes.get(i));
				
			System.out.println(printMe);
		}
	}
	
	public static DefaultMutableTreeNode joinIndexes(DefaultMutableTreeNode [] indexArr, String bCast)
	{
		if(indexArr.length == 0)
			return null;
		
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
			r = new Range(start, end, bCast);
			nextLevel[i] = new DefaultMutableTreeNode(r);
			nextLevel[i].add(indexArr[childIndex]);
			if(twoChildren)
				nextLevel[i].add(indexArr[childIndex + 1]);
		}
		
		// call the function again to create the next level up
		System.out.println("creating new level with " + nextLevel.length + " nodes");
		
		return joinIndexes(nextLevel, bCast);
	}
	
	// prints out the hierarchy of the tree on the screen
	public static void printTreeStartingAt(DefaultMutableTreeNode top)
	{		
		Enumeration e = top.breadthFirstEnumeration();
		for(; e.hasMoreElements();)
		{
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.nextElement();
			System.out.println(node.toString());
		}
	}
}