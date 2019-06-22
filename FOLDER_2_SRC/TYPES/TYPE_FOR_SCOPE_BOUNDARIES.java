package TYPES;

public class TYPE_FOR_SCOPE_BOUNDARIES extends TYPE
{
	public char scope;
	public String f;
	public String returnType;
	public String cls;

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TYPE_FOR_SCOPE_BOUNDARIES(String name, char scope)
	{
		this.name = name;
		this.scope = scope;
	}

	public TYPE_FOR_SCOPE_BOUNDARIES(String name, char scope, String cls){
		this.name = name;
		this.scope = scope;
		this.cls = cls;
	}

	public TYPE_FOR_SCOPE_BOUNDARIES(String name, char scope, String f, String returnType)
	{
		this.name = name;
		this.scope = scope;
		this.f = f;
		this.returnType = returnType;
	}
	public boolean isArray(){ return false;}
}
