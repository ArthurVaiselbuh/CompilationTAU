package TYPES;

import java.util.*;

public class TYPE_CLASS extends TYPE
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TYPE_CLASS father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TYPE_LIST data_members;
	public ArrayList<String> data_members_names;

	/* Additionals for vtable */
	public ArrayList<String> localVars;
	public ArrayList<String> localVarsValues;
	public ArrayList<String> localFuncs;
	public String functionTableLabel;
	public ArrayList<String> FuncsLabels = new ArrayList<String>();

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_CLASS(TYPE_CLASS father,String name,TYPE_LIST data_members, ArrayList<String> data_members_names)
	{
		this.name = name;
		this.father = father;
		this.data_members = data_members;
		this.data_members_names = data_members_names;
	}
	public boolean isClass(){ return true;}
	public boolean isArray(){ return true;}

	public void updateVars(ArrayList<String> localVars){
		this.localVars = localVars;
	}

	public void updateFuncs(ArrayList<String> localFuncs){
		this.localFuncs = localFuncs;
	}

	public TYPE getMember(int i){
		int index = 0;
		TYPE_LIST it = data_members;
		while (index < i && it != null){
			it = it.tail;
			index++;
		}
		if (it != null)
			return it.head;
		return null;
	}


}
