package TYPES;

import java.util.*;

public class TYPE_LIST
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public TYPE head;
	public TYPE_LIST tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TYPE_LIST(TYPE head,TYPE_LIST tail)
	{
		this.head = head;
		this.tail = tail;
	}

	public void printList(){
		for (TYPE_LIST e = this; e != null; e = e.tail){
			System.out.format("%s, ", e.head.name);
		}
		System.out.format("\n");
	}

	public ArrayList getList(){
		ArrayList<String> res = new ArrayList<String>();
		for (TYPE_LIST e = this; e != null; e = e.tail){
			res.add(e.head.name);
		}
		return res;
	}
	public boolean isArray(){ return false;}

	/*public boolean overLoadingTest(TYPE_FUNCTION e){
		if (head != null && head instanceof TYPE_FUNCTION){
			if (e.name == head.name && !(((TYPE_FUNCTION)head).isSameFunc(e) && e.isSameFunc((TYPE_FUNCTION)head))){
				return false;
			}
		}
		for (TYPE_LIST it = tail; it != null; it = it.tail){
			if (it.head != null && it.head instanceof TYPE_FUNCTION){
				if (e.name == it.head.name && !(((TYPE_FUNCTION)(it.head)).isSameFunc(e))){
					return false;
				}
		}
	}*/
}
