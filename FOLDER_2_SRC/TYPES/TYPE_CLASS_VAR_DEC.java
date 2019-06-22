package TYPES;

public class TYPE_CLASS_VAR_DEC extends TYPE
{
	public TYPE_CLASS t;
	public String name_instance;

	public TYPE_CLASS_VAR_DEC(TYPE_CLASS t,String name_instance)
	{
		this.t = t;
		this.name_instance = name_instance;
	}
	public boolean isArray(){ return false;}

}
