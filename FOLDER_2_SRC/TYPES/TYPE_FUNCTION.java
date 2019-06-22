package TYPES;

public class TYPE_FUNCTION extends TYPE
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public TYPE returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TYPE_LIST params;

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FUNCTION(TYPE returnType,String name,TYPE_LIST params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}

	public boolean isSameFunc(TYPE_FUNCTION e){
		if (this.returnType != e.returnType)
			return false;
		TYPE_LIST it2 = e.params;
		for (TYPE_LIST it = this.params; it != null; it = it.tail){
			if (!it.head.name.equals(it2.head.name))
				return false;
			it2 = it2.tail;
		}
		if (it2 != null)
			return false;
		return true;
	}
	public boolean isArray(){ return false;}
}
