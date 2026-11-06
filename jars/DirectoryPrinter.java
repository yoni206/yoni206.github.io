
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;


public class DirectoryPrinter {
	
	private class Tree<T> {
	    private Node<T> root;
	    
	    public Tree(T rootData) {
	        root = new Node<T>(); 
	        root.data = rootData;
	        root.children = new ArrayList<Node<T>>();
	    }
	    
	    
	}
	
	public class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children;
        
        public Node(T data, Node<T> parent, List<Node<T>> children) {
			super();
			this.data = data;
			this.parent = parent;
			this.children = children;
		}
        
        public Node() {
			super();
		}

        
		public T getData() {
			return data;
		}
		public void setData(T data) {
			this.data = data;
		}
		public Node<T> getParent() {
			return parent;
		}
		public void setParent(Node<T> parent) {
			this.parent = parent;
		}
		public List<Node<T>> getChildren() {
			return children;
		}
		public void setChildren(List<Node<T>> children) {
			this.children = children;
		}
		        
		
        
    }
	
	
	private Tree<String> directories;
	
	public DirectoryPrinter(String dirName) {
		directories = new Tree<String>(dirName);
	}
	
	
	
	public Tree<String> getDirectories() {
		return directories;
	}



	public void setDirectories(Tree<String> directories) {
		this.directories = directories;
	}



	public void createTree(String path, Node<String> parent) {
		File file = new File(path);
		for (String subPath : file.list()) {
			File subFile = new File(file.getAbsolutePath() + "/" + subPath);
			Node<String> newNode = new Node<String>(subPath, parent, new ArrayList<Node<String>>()); 
			parent.getChildren().add(newNode);
			if (subFile.isDirectory()) {
				newNode.data = "<B>DIRECTORY " + newNode.data;
				newNode.data = newNode.data + ": </B>";
				createTree(subFile.getAbsolutePath(), newNode);
			}
		}
	}
	
	public String getDirectoriesTree() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("");
		stringBuilder.append("<HTML>");
		stringBuilder.append("<HEAD>");
		stringBuilder.append("<meta charset='utf-8'>");
		stringBuilder.append("</HEAD>");
		stringBuilder.append("<BODY>");
		String space="";
		getDirectoriesRec(stringBuilder, directories.root, space);
		stringBuilder.append("</BODY>");
		stringBuilder.append("</HTML>");
		return stringBuilder.toString();
	}
	
	private void getDirectoriesRec(StringBuilder stringBuilder, Node<String> node, String space) {
		stringBuilder.append(space + node.getData());
		stringBuilder.append("<BR>");
		space+="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;";
		
		for (Node<String> son : node.getChildren()) {
			if (!son.data.contains("<B>") && !son.data.contains("<b>")) {
				getDirectoriesRec(stringBuilder, son, space);	
			}
		}
		
		for (Node<String> son : node.getChildren()) {
			if (son.data.contains("<B>") || son.data.contains("<b>")) {
				getDirectoriesRec(stringBuilder, son, space);	
			}
		}
		
	}
	
	public void saveStringToFile(String str, String fileName) {
		Writer writer = null;
		try
		{
			
			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName), "UTF8"));
			//writer = new BufferedWriter( new FileWriter( fileName));
			writer.append( str);

		}
		catch ( IOException e)
		{
			System.out.println(e);
		}
		finally
		{
			try
			{
				if ( writer != null)
					writer.close( );
			}
			catch ( IOException e)
			{
				System.out.println(e);
			}
		}
	}
	
	
	public static void main(String[] args) {
		String path = args[0];
		DirectoryPrinter directoryPrinter = new DirectoryPrinter(path);
		directoryPrinter.createTree(path, directoryPrinter.getDirectories().root);
		String str = directoryPrinter.getDirectoriesTree();
		directoryPrinter.saveStringToFile(str, path + "/" + "directory_listing.html");
	}

}
